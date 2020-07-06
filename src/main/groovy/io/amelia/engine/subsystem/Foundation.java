package io.amelia.engine.subsystem;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import io.amelia.engine.subsystem.log.EngineLogger;
import io.amelia.lang.ExceptionReport;
import io.amelia.lang.UncaughtException;
import io.amelia.support.DevMeta;

public class Foundation
{
	public static final EngineLogger L = EngineLogger.getLogger( EngineLogger.GLOBAL_LOGGER_NAME );

	/**
	 * An {@link Executor} that can be used to execute tasks in parallel.
	 */
	static final Executor EXECUTOR_PARALLEL;
	/**
	 * An {@link Executor} that executes tasks one at a time in serial order.
	 */
	static final Executor EXECUTOR_SERIAL;
	static final int KEEP_ALIVE_SECONDS = 30;
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	static final int THREAD_ROOL_SIZE_MAXIMUM = CPU_COUNT * 2 + 1;
	// We want at least 2 threads and at most 4 threads in the core pool,
	// preferring to have 1 less than the CPU count to avoid saturating
	// the CPU with background work
	static final int THREAD_POOL_SIZE_CORE = Math.max( 4, Math.min( CPU_COUNT - 1, 1 ) );
	static final ThreadFactory threadFactory = new ThreadFactory()
	{
		private final AtomicInteger mCount = new AtomicInteger( 1 );

		@Override
		public Thread newThread( @Nonnull Runnable runnable )
		{
			Thread newThread = new Thread( runnable, "AEC Thread #" + String.format( "%d04", mCount.getAndIncrement() ) );
			newThread.setUncaughtExceptionHandler( ( thread, exp ) -> ExceptionReport.handleSingleException( new UncaughtException( "Uncaught exception thrown on thread \"" + thread.getName() + "\".", exp ) ) );

			return newThread;
		}
	};

	static
	{
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor( THREAD_POOL_SIZE_CORE, THREAD_ROOL_SIZE_MAXIMUM, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory );
		threadPoolExecutor.allowCoreThreadTimeOut( true );
		EXECUTOR_PARALLEL = threadPoolExecutor;

		EXECUTOR_SERIAL = new Executor()
		{
			final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();
			Runnable mActive;

			public synchronized void execute( @Nonnull final Runnable r )
			{
				mTasks.offer( () -> {
					try
					{
						r.run();
					}
					finally
					{
						scheduleNext();
					}
				} );
				if ( mActive == null )
				{
					scheduleNext();
				}
			}

			protected synchronized void scheduleNext()
			{
				if ( ( mActive = mTasks.poll() ) != null )
				{
					EXECUTOR_PARALLEL.execute( mActive );
				}
			}
		};
	}

	public static DevMeta getDeveloperMeta()
	{
		return new DevMeta();
	}
}
