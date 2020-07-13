package io.amelia.engine.log;

import java.util.WeakHashMap;
import java.util.logging.Level;

import io.amelia.engine.EngineApplication;
import io.amelia.support.Reflection;

public class L
{
	public static WeakHashMap<String, EngineLogger> loggers = new WeakHashMap<>();

	public static void debug( String message, Object... args )
	{
		log( Level.CONFIG, message, args );
	}

	public static void debug( String source, String message, Object... args )
	{
		log( Level.CONFIG, source, message, args );
	}

	public static void fine( String message, Object... args )
	{
		log( Level.FINE, message, args );
	}

	public static void fine( String source, String message, Object... args )
	{
		log( Level.FINE, source, message, args );
	}

	public static void finest( String message, Object... args )
	{
		log( Level.FINEST, message, args );
	}

	public static void finest( String source, String message, Object... args )
	{
		log( Level.FINEST, source, message, args );
	}

	public static EngineLogger get( String loggerNamespace )
	{
		if ( EngineApplication.class.getName().equals( loggerNamespace ) )
			return EngineLogRegistry.GLOBAL;
		if ( loggers.containsKey( loggerNamespace ) )
			return loggers.get( loggerNamespace );
		return EngineLogRegistry.UNKNOWN;
	}

	public static EngineLogger get()
	{
		return get( Reflection.getCallerClassName( L.class ) );

	}

	public static void info( String message, Object... args )
	{
		log( Level.INFO, message, args );
	}

	public static void info( String source, String message, Object... args )
	{
		log( Level.INFO, source, message, args );
	}

	public static void init( String loggerNamespace )
	{
		String callerClassName = Reflection.getCallerClassName( L.class );
		if ( callerClassName != null )
			loggers.computeIfAbsent( callerClassName, k -> EngineLogRegistry.logger.getChild( loggerNamespace ) );
	}

	public static void log( Level level, String message, Object... args )
	{
		get().log( level, message, args );
	}

	public static void log( Level level, Throwable cause )
	{
		get().log( level, cause );
	}

	public static void log( Level level, String source, String message, Object... args )
	{
		get( source ).log( level, message, args );
	}

	public static void log( Level level, String source, Throwable cause )
	{
		get( source ).log( level, cause );
	}

	public static void log( Level level, Throwable cause, String message, Object... args )
	{
		log( level, message, args );
		log( level, cause );
	}

	public static void log( Level level, String source, Throwable cause, String message, Object... args )
	{
		log( level, source, message, args );
		log( level, source, cause );
	}

	public static void severe( Throwable cause )
	{
		log( Level.SEVERE, cause );
	}

	public static void severe( String message, Object... args )
	{
		log( Level.SEVERE, message, args );
	}

	public static void severe( String message, Throwable cause, Object... args )
	{
		log( Level.SEVERE, cause, message, args );
	}

	public static void severe( String source, Throwable cause )
	{
		log( Level.SEVERE, source, cause );
	}

	public static void severe( String source, String message, Object... args )
	{
		log( Level.SEVERE, source, message, args );
	}

	public static void severe( String source, String message, Throwable cause, Object... args )
	{
		log( Level.SEVERE, source, cause, message, args );
	}

	public static void warning( Throwable cause )
	{
		log( Level.WARNING, cause );
	}

	public static void warning( String message, Object... args )
	{
		log( Level.WARNING, message, args );
	}

	public static void warning( String message, Throwable cause, Object... args )
	{
		log( Level.WARNING, cause, message, args );
	}

	public static void warning( String source, Throwable cause )
	{
		log( Level.WARNING, source, cause );
	}

	public static void warning( String source, String message, Object... args )
	{
		log( Level.WARNING, source, message, args );
	}

	public static void warning( String source, String message, Throwable cause, Object... args )
	{
		log( Level.WARNING, source, cause, message, args );
	}
}
