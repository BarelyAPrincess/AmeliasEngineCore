package io.amelia.engine.subsystem.log;

import java.text.SimpleDateFormat;
import java.util.logging.Level;

public interface EngineLogTrait
{
	public static final SimpleDateFormat DEFAULT_TIMESTAMP = new SimpleDateFormat( "HH:mm:ss.SSS" );

	default void debug( Class<?> source, String message, Object... args )
	{
		log( Level.CONFIG, source, message, args );
	}

	default void fine( Class<?> source, String message, Object... args )
	{
		log( Level.FINE, source, message, args );
	}

	default void finest( Class<?> source, String message, Object... args )
	{
		log( Level.FINEST, source, message, args );
	}

	default void info( Class<?> source, String message, Object... args )
	{
		log( Level.INFO, source, message, args );
	}

	void log( Level level, Class<?> source, String message, Object... args );

	default void log( Level level, Class<?> source, Throwable cause, String message, Object... args )
	{
		log( level, source, message, args );
		log( level, source, cause );
	}

	void log( Level level, Class<?> source, Throwable cause );

	default void severe( Class<?> source, Throwable cause )
	{
		log( Level.SEVERE, source, cause );
	}

	default void severe( Class<?> source, String message, Object... args )
	{
		log( Level.SEVERE, source, message, args );
	}

	default void severe( Class<?> source, String message, Throwable cause, Object... args )
	{
		log( Level.SEVERE, source, cause, message, args );
	}

	default void warning( Class<?> source, Throwable cause )
	{
		log( Level.WARNING, source, cause );
	}

	default void warning( Class<?> source, String message, Object... args )
	{
		log( Level.WARNING, source, message, args );
	}

	default void warning( Class<?> source, String message, Throwable cause, Object... args )
	{
		log( Level.WARNING, source, cause, message, args );
	}
}
