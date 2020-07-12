package io.amelia.engine.subsystem;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.amelia.engine.EngineLooper;
import io.amelia.engine.subsystem.injection.Libraries;
import io.amelia.engine.subsystem.injection.MavenReference;
import io.amelia.engine.subsystem.log.EngineLogRegistry;
import io.amelia.engine.subsystem.log.EngineLogger;
import io.amelia.engine.subsystem.looper.LooperRouter;
import io.amelia.engine.subsystem.looper.MainLooper;
import io.amelia.extra.UtilityObjects;
import io.amelia.lang.ApplicationException;
import io.amelia.lang.ExceptionReport;
import io.amelia.support.DateAndTime;
import io.amelia.support.DevMeta;
import io.amelia.support.EnumColor;
import io.amelia.support.LooperException;
import io.amelia.support.Runlevel;
import io.amelia.support.Timing;

public class EngineCore
{
	public static final EngineLogger L = EngineLogger.getLogger( EngineLogRegistry.GLOBAL_LOGGER_NAMESPACE );

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
			newThread.setUncaughtExceptionHandler( ( thread, exp ) -> ExceptionReport.handleSingleException( new ApplicationException.Uncaught( "Uncaught exception thrown on thread \"" + thread.getName() + "\".", exp ) ) );

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
	private static EngineApplication app;
	private static Runlevel currentRunlevel = Runlevel.INITIALIZATION;
	private static String currentRunlevelReason = null;
	private static DevMeta devMeta = new DevMeta();
	private static boolean init;
	private static Object phaseTimingObject = new Object();
	private static Runlevel previousRunlevel;
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
	}

	public static EngineApplication getApplication()
	{
		if ( app == null )
			throw new ApplicationException.Startup( "Application is not initialized!" );
		return app;
	}

	public static String getCurrentRunlevelReason()
	{
		return currentRunlevelReason;
	}

	public static DevMeta getDeveloperMeta()
	{
		return devMeta;
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

	public static Runlevel getRunlevel()
	{
		return currentRunlevel;
	}

	public static void setRunlevel( @Nonnull Runlevel level )
	{
		setRunlevel( level, null );
	}

	public static void init()
	{
		if ( init )
			return;

		synchronized ( EngineCore.class )
		{
			app = new EngineApplication();

			LooperRouter.setMainLooper( new EngineLooper() );

			if ( !app.hasArgument( "no-banner" ) )
			{
				L.info( EnumColor.NEGATIVE + "" + EnumColor.GOLD + "Starting " + EngineCore.getDeveloperMeta().getProductName() + " version " + EngineCore.getDeveloperMeta().getVersionDescribe() );
				L.info( EnumColor.NEGATIVE + "" + EnumColor.GOLD + EngineCore.getDeveloperMeta().getProductCopyright() );
			}

			L.info( "Application UUID: " + EnumColor.AQUA + app.uuid() );

			init = true;
		}
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

	public static boolean isRunlevel( Runlevel runlevel )
	{
		return currentRunlevel == runlevel;
	}

	/**
	 * Handles post runlevel change. Should almost always be the very last method call when the runlevel changes.
	 */
	private static void onRunlevelChange() throws ApplicationException.Error
	{
		Events.getInstance().callEventWithException( new RunlevelEvent( previousRunlevel, currentRunlevel ) );

		app.onRunlevelChange( previousRunlevel, currentRunlevel );

		// Internal runlevel changes happen after this point. Generally progressing the application from each runlevel to the next.

		if ( currentRunlevel == Runlevel.STARTUP )
		{
			UUID nullUuid = UUID.fromString( ConfigRegistry.config.getString( ConfigKeys.UUID_NULL ) );
			UUID rootUuid = UUID.fromString( ConfigRegistry.config.getString( ConfigKeys.UUID_ROOT ) );

			entityNull = getUsers().createVirtualUser( nullUuid );
			entityRoot = getUsers().createVirtualUser( rootUuid );

			// entityNull = Exceptions.tryCatchOrNotPresent( () -> make( EntitySubject.class, Maps.builder( "uuid", nullUuid ).hashMap() ), exp -> exp instanceof ApplicationException.Error ? ( ApplicationException.Error ) exp : new ApplicationException.Error( "Failed to create the NULL Entity.", exp ) );
			// entityRoot = Exceptions.tryCatchOrNotPresent( () -> make( EntitySubject.class, Maps.builder( "uuid", rootUuid ).hashMap() ), exp -> exp instanceof ApplicationException.Error ? ( ApplicationException.Error ) exp : new ApplicationException.Error( "Failed to create the ROOT Entity.", exp ) );
		}

		// Indicates the application has begun the main loop
		if ( currentRunlevel == Runlevel.MAINLOOP )
			if ( app instanceof NetworkedApplication )
				setRunlevelLater( Runlevel.NETWORKING );
			else
				setRunlevelLater( Runlevel.STARTED );

		// Indicates the application has started all and any networking
		if ( currentRunlevel == Runlevel.NETWORKING )
			setRunlevelLater( Runlevel.STARTED );

		// if ( currentRunlevel == Runlevel.CRASHED || currentRunlevel == Runlevel.RELOAD || currentRunlevel == Runlevel.SHUTDOWN )
		// L.notice( currentRunlevelReason );

		// TODO Implement the RELOAD runlevel!
		if ( currentRunlevel == Runlevel.RELOAD )
			throw new ApplicationException.Error( "Not Implemented. Sorry!" );

		if ( currentRunlevel == Runlevel.SHUTDOWN )
			app.quitSafely();

		if ( currentRunlevel == Runlevel.CRASHED )
			app.quitUnsafe();

		if ( currentRunlevel == Runlevel.DISPOSED )
		{
			// Runlevel DISPOSED is activated over the ApplicationLooper#joinLoop method returns.

			app.dispose();
			app = null;

			try
			{
				Thread.sleep( 100 );
			}
			catch ( InterruptedException e )
			{
				// Ignore
			}

			System.exit( 0 );
		}
	}

	public static void prepare()
	{
		requirePrimaryThread();
		requireRunlevel( Runlevel.INITIALIZATION, "prepare() must be called at runlevel INITIALIZATION" );

		L.info( "Loading deployment libraries from \"" + Libraries.LIBRARY_DIR + "\"" );
		try
		{
			/* Load Deployment Libraries */
			L.info( "Loading deployment libraries defined in \"dependencies.txt\"." );
			String depends = IO.resourceToString( "dependencies.txt" );
			if ( !UtilityObjects.isEmpty( depends ) ) // Will be null if the file does not exist
				for ( String depend : depends.split( "\n" ) )
					if ( !depend.startsWith( "#" ) )
						Libraries.loadLibrary( new MavenReference( "builtin", depend ) );
		}
		catch ( IOException e )
		{
			throw new ApplicationException.Startup( "Failed to read the built-in dependencies file.", e );
		}
		L.info( EnumColor.AQUA + "Finished downloading deployment libraries." );

		// Call to make sure the INITIALIZATION runlevel is acknowledged by the application.
		onRunlevelChange();
	}

	/**
	 * Systematically changes the application runlevel.
	 * If this method is called by the application main thread, the change is made immediate.
	 */
	public static void setRunlevel( @Nonnull Runlevel runlevel, @Nullable String reason )
	{
		UtilityObjects.notNull( runlevel );
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
				throw new ApplicationException.Crash();
		}
		catch ( ApplicationException.Error e )
		{
			if ( runlevel == Runlevel.CRASHED )
				throw new ApplicationException.Crash( e );
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
			L.warning( "shutdown() called but ignored because MainLooper is already quitting. {shutdownReason=" + reason + "}" );
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

	public static boolean isPrimaryThread()
	{
		if ( app == null )
			throw new ApplicationException.Startup( "Application is not set!" );
		return app.isPrimaryThread();
	}

	public static void requirePrimaryThread()
	{
		requirePrimaryThread( null );
	}

	public static void requirePrimaryThread( String errorMessage )
	{
		if ( !isPrimaryThread() )
			throw new ApplicationException.Startup( errorMessage == null ? "Method MUST be called from the primary thread that initialed started the Kernel." : errorMessage );
	}

	public static void requireRunlevel( Runlevel runlevel )
	{
		requireRunlevel( runlevel, null );
	}

	public static void requireRunlevel( Runlevel runlevel, String errorMessage )
	{
		if ( !isRunlevel( runlevel ) )
			throw new ApplicationException.Startup( errorMessage == null ? "Method MUST be called at runlevel " + runlevel.name() : errorMessage );
	}
}
