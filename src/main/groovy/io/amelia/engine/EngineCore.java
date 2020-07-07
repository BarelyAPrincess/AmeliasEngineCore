package io.amelia.engine;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import io.amelia.engine.subsystem.ConfigKeys;
import io.amelia.engine.subsystem.ConfigRegistry;
import io.amelia.engine.subsystem.log.EngineLogger;
import io.amelia.lang.ExceptionReport;
import io.amelia.lang.UncaughtException;
import io.amelia.support.DateAndTime;
import io.amelia.support.DevMeta;
import io.amelia.support.Timing;

public class EngineCore
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
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	static final int THREAD_ROOL_SIZE_MAXIMUM = CPU_COUNT * 2 + 1;
	// We want at least 2 threads and at most 4 threads in the core pool,
	// preferring to have 1 less than the CPU count to avoid saturating
	// the CPU with background work
	static final int THREAD_POOL_SIZE_CORE = Math.max( 4, Math.min( CPU_COUNT - 1, 1 ) );
	public static long startTime = System.currentTimeMillis();
	private static EngineCoreApplication app;
	private static Object runlevelTimingObject = new Object();
	private static Object timingObject = new Object();

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

		Timing.start( timingObject );

		init();
	}

	/**
	 * Indicates if we are running a development build of the server
	 *
	 * @return True is we are running in development mode
	 */
	public static boolean isDevelopment()
	{
		return devMeta != null && "0".equals( devMeta.getBuildNumber() ) || ConfigRegistry.config.getBoolean( ConfigRegistry.ConfigKeys.DEVELOPMENT_MODE );
	}

	public static EngineCoreApplication getApplication()
	{
		return app;
	}

	public static String getCurrentRunlevelReason()
	{
		return currentRunlevelReason;
	}

	public static DevMeta getDeveloperMeta()
	{
		return new DevMeta();
	}

	public static Executor getExecutorParallel()
	{
		return EXECUTOR_PARALLEL;
	}

	public static Executor getExecutorSerial()
	{
		return EXECUTOR_SERIAL;
	}

	public static String getGenericRunlevelReason( @Nonnull Runlevel runlevel )
	{
		String uuid = getApplication().uuid().toString(); // getApplication().getEnv().getString( "instance-id" ).orElse( null );

		if ( runlevel == Runlevel.RELOAD )
			return String.format( "Server \"%s\" is reloading. Be back soon. :D", uuid );
		else if ( runlevel == Runlevel.CRASHED )
			return String.format( "Server \"%s\" has crashed. Sorry about that. :(", uuid );
		else if ( runlevel == Runlevel.SHUTDOWN )
			return String.format( "Server \"%s\" is shutting down. Good bye! :|", uuid );
		else
			return "No reason provided.";
	}

	public static Runlevel getLastRunlevel()
	{
		return previousRunlevel;
	}

	public static void init()
	{
		if ( init )
			return;

		synchronized ( EngineCore.class )
		{
			app = new EngineCoreApplication();

			LooperRouter.setMainLooper( new FoundationLooper( app ) );

			if ( !app.hasArgument( "no-banner" ) )
				app.showBanner( L );

			L.info( "Application UUID: " + EnumColor.AQUA + app.uuid() );

			init = true;
		}
	}

	public static void setRunlevel( @Nonnull Runlevel level )
	{
		setRunlevel( level, null );
	}

	/**
	 * Systematically changes the application runlevel.
	 * If this method is called by the application main thread, the change is made immediate.
	 */
	public static void setRunlevel( @Nonnull Runlevel runlevel, @Nullable String reason )
	{
		Objs.notNull( runlevel );
		MainLooper mainLooper = LooperRouter.getMainLooper();

		// If we confirm that the current thread is the same one that run the Looper, we make the runlevel change immediate instead of posting it for later.
		if ( !mainLooper.isThreadJoined() && app.isPrimaryThread() || mainLooper.isHeldByCurrentThread() )
			setRunlevel0( runlevel, reason );
			// joinLoop has not been called and yet we're crashing, time for an interrupt signal.
		else if ( !mainLooper.isThreadJoined() && runlevel == Runlevel.CRASHED )
			throw new StartupException( "Application has CRASHED!" );
			// Otherwise all other runlevels need to be scheduled, including a CRASH.
		else
			setRunlevelLater( runlevel, reason );
	}

	private synchronized static void setRunlevel0( @Nonnull Runlevel runlevel, @Nullable String reason )
	{
		try
		{
			if ( reason == null || reason.length() == 0 )
				reason = getGenericRunlevelReason( runlevel );

			if ( LooperRouter.getMainLooper().isThreadJoined() && !LooperRouter.getMainLooper().isHeldByCurrentThread() )
				throw new ApplicationException.Error( "Runlevel can only be set from the main looper thread. Be more careful next time." );
			if ( currentRunlevel == runlevel )
			{
				L.warning( "Runlevel is already set to \"" + runlevel.name() + "\". This might be a severe race bug." );
				return;
			}
			if ( !runlevel.checkRunlevelOrder( currentRunlevel ) )
				throw new ApplicationException.Error( "RunLevel \"" + runlevel.name() + "\" was set out of order. Present runlevel was \"" + currentRunlevel.name() + "\". This is potentially a race bug or there were exceptions thrown." );

			Timing.start( runlevelTimingObject );

			previousRunlevel = currentRunlevel;
			currentRunlevel = runlevel;
			currentRunlevelReason = reason;

			if ( runlevel == Runlevel.RELOAD || runlevel == Runlevel.SHUTDOWN || runlevel == Runlevel.CRASHED )
				L.info( EnumColor.join( EnumColor.GOLD ) + "Application is entering runlevel \"" + runlevel.name() + "\", for reason: " + reason + "." );

			onRunlevelChange();

			if ( currentRunlevel == Runlevel.DISPOSED )
				L.info( EnumColor.join( EnumColor.GOLD ) + "Application has shutdown! It ran for a total of " + Timing.finish( timingObject ) + "ms!" );
			else if ( currentRunlevel == Runlevel.STARTED )
			{
				L.info( EnumColor.join( EnumColor.GOLD ) + "Application has started! Startup took a total of " + Timing.finish( timingObject ) + "ms!" );
				Timing.start( timingObject );
			}
			else if ( currentRunlevel == Runlevel.CRASHED )
				L.info( EnumColor.join( EnumColor.GOLD ) + "Application has crashed started! It ran for a total of " + Timing.finish( timingObject ) + "ms!" );

			L.info( EnumColor.AQUA + "Application has entered runlevel \"" + runlevel.name() + "\". onRunlevelChange() took " + Timing.finish( runlevelTimingObject ) + "ms!" );

			if ( runlevel == Runlevel.CRASHED )
				throw new FoundationCrashException();
		}
		catch ( ApplicationException.Error e )
		{
			if ( runlevel == Runlevel.CRASHED )
				throw new ApplicationException.Runtime( e );
			ExceptionReport.handleSingleException( e );
		}
	}

	public static void setRunlevelLater( @Nonnull Runlevel runlevel )
	{
		setRunlevelLater( runlevel, null );
	}

	public static void setRunlevelLater( @Nonnull Runlevel runlevel, @Nullable String reason )
	{
		if ( reason == null || reason.length() == 0 )
			setRunlevelLater( runlevel, getGenericRunlevelReason( runlevel ) );
		else
			LooperRouter.getMainLooper().postTask( entry -> setRunlevel0( runlevel, reason ) );
	}

	public static void shutdown( String reason )
	{
		try
		{
			setRunlevel( Runlevel.SHUTDOWN, reason );
		}
		catch ( LooperException.InvalidState e )
		{
			// L.warning( "shutdown() called but ignored because MainLooper is already quitting. {shutdownReason=" + reason + "}" );
			// TEMP?
			e.printStackTrace();
			System.exit( 1 );
		}
	}

	/**
	 * Will process the application load based on the information provided by the BaseApplication.
	 * Takes Runlevel from INITIALIZATION to RUNNING.
	 * <p>
	 * start() will not return until the main looper quits.
	 */
	public static void start() throws ApplicationException.Error
	{
		requirePrimaryThread();
		requireRunlevel( Runlevel.INITIALIZATION, "start() must be called at runlevel INITIALIZATION" );

		if ( getRunlevel() == Runlevel.CRASHED )
			return;

		// Initiate startup procedures.
		setRunlevel( Runlevel.STARTUP );

		if ( !ConfigRegistry.config.getBoolean( ConfigKeys.DISABLE_METRICS ) )
		{
			// TODO Implement!

			// Send Metrics

			final String instanceId = app.getEnv().getString( "instance-id" ).orElse( null );
		}

		// Abort
		if ( getRunlevel() == Runlevel.CRASHED )
			return;

		// Join this thread to the main looper.
		LooperRouter.getMainLooper().joinLoop();

		// Sets the application to the disposed state once the joinLoop method returns exception free.
		if ( getRunlevel() != Runlevel.CRASHED )
			setRunlevel( Runlevel.DISPOSED );
	}

	public static long uptime()
	{
		return System.currentTimeMillis() - startTime;
	}

	public static String uptimeDescribe()
	{
		return DateAndTime.formatDuration( System.currentTimeMillis() - startTime );
	}

	// TODO Not Implemented
	public static boolean useTimings()
	{
		return false;
	}
}
