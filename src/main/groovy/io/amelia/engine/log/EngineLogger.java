package io.amelia.engine.log;

import java.lang.ref.WeakReference;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.amelia.data.ContainerBase;
import io.amelia.engine.EngineCore;
import io.amelia.extra.UtilityObjects;
import io.amelia.lang.ApplicationException;
import io.amelia.lang.ReportingLevel;
import io.amelia.support.BiFunctionWithException;
import io.amelia.support.EnumColor;
import io.amelia.support.Voluntary;

public class EngineLogger extends ContainerBase<EngineLogger, ApplicationException.Error> implements EngineLogTrait
{
	@Nonnull
	public static EngineLogger root()
	{
		try
		{
			return new EngineLogger( null, "" );
		}
		catch ( ApplicationException.Error error )
		{
			// This should never happen!
			throw new RuntimeException( error );
		}
	}

	private boolean hasErrored = false;
	private WeakReference<Logger> logger = null;

	protected EngineLogger( @Nonnull BiFunctionWithException<EngineLogger, String, EngineLogger, ApplicationException.Error> creator )
	{
		super( creator );
	}

	protected EngineLogger( @Nonnull BiFunctionWithException<EngineLogger, String, EngineLogger, ApplicationException.Error> creator, @Nonnull String localName ) throws ApplicationException.Error
	{
		super( creator, localName );
	}

	protected EngineLogger( @Nonnull BiFunctionWithException<EngineLogger, String, EngineLogger, ApplicationException.Error> creator, @Nullable EngineLogger parent, @Nonnull String localName ) throws ApplicationException.Error
	{
		super( creator, parent, localName );
	}

	public void clearHasErrored()
	{
		hasErrored = false;
	}

	protected Logger createLogger()
	{
		Logger newLogger = new SubLogger( getCurrentPath() );
		LogManager.getLogManager().addLogger( newLogger );
		logger = new WeakReference<>( newLogger );
		return newLogger;
	}

	@Override
	protected ApplicationException.Error getException( @Nonnull String message, @Nullable Exception exception )
	{
		return new ApplicationException.Error( ReportingLevel.E_ERROR, message, exception );
	}

	protected Voluntary<Logger> getLogger()
	{
		Voluntary<Logger> logger = getMyLogger();
		if ( !logger.isPresent() && hasParent() )
			logger = parent.getLogger();
		return logger;
	}

	protected Voluntary<Logger> getMyLogger()
	{
		if ( logger == null )
			return Voluntary.empty();
		Logger foundLogger = LogManager.getLogManager().getLogger( getCurrentPath() );
		if ( foundLogger == null )
			return Voluntary.empty();
		logger = new WeakReference<>( foundLogger );
		return Voluntary.of( foundLogger );
	}

	protected Voluntary<Logger> getMyLoggerOrCreate()
	{
		Voluntary<Logger> myLogger = getMyLogger();
		if ( !myLogger.isPresent() )
		{
			Logger newLogger = new SubLogger( getCurrentPath() );
			LogManager.getLogManager().addLogger( newLogger );
			logger = new WeakReference<>( newLogger );
			myLogger = Voluntary.of( newLogger );
		}
		return myLogger;
	}

	@Override
	protected boolean isTrimmable0()
	{
		return logger == null && logger.get() == null;
	}

	public void log( Level level, String message, Object... args )
	{
		getLogger().ifAbsentGet( this::createLogger ).get().log( level, args.length > 0 ? String.format( message, args ) : message );
	}

	public void log( Level level, Throwable cause, String message, Object... args )
	{
		message = args.length > 0 ? String.format( message, args ) : message;

		try
		{
			if ( !UtilityObjects.stackTraceAntiLoop( java.util.logging.Logger.class, "log" ) || hasErrored )
			{
				EngineLogRegistry.FAILOVER_OUTPUT_STREAM.println( "Failover Logger [" + level.getName() + "] " + message );
				cause.printStackTrace( EngineLogRegistry.FAILOVER_OUTPUT_STREAM );
			}
			else
				log( level, ( EngineLogRegistry.useColor() ? EnumColor.fromLevel( level ) : "" ) + message, cause );
		}
		catch ( Throwable tt )
		{
			throwError( tt );
			if ( EngineCore.isDevelopment() )
				throw tt;
		}
	}

	public void log( Level level, Throwable cause )
	{
		log( level, cause, "" );
	}

	public void setHasErrored()
	{
		hasErrored = true;
	}

	public void subscribeHandler( Handler handler )
	{
		getMyLogger().ifPresent( l -> l.addHandler( handler ) );
	}

	private void throwError( Throwable t )
	{
		hasErrored = true;

		EngineLogRegistry.FAILOVER_OUTPUT_STREAM.println( EnumColor.RED + "" + EnumColor.NEGATIVE + "The logger \"" + getCurrentPath() + "\" has thrown an unrecoverable exception!" );
		t.printStackTrace( EngineLogRegistry.FAILOVER_OUTPUT_STREAM );
	}

	public void unsubscribeAll()
	{
		getMyLogger().ifPresent( l -> {
			for ( Handler h : l.getHandlers() )
				l.removeHandler( h );
		} );
	}

	public void unsubscribeHandler( Handler handler )
	{
		getMyLogger().ifPresent( l -> l.removeHandler( handler ) );
	}
}
