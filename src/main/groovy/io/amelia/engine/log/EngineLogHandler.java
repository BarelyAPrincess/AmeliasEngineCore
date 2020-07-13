package io.amelia.engine.log;

import java.util.logging.Level;

public interface EngineLogHandler
{
	default void debug( String loggerName, String message, Object... args )
	{
		log( Level.CONFIG, loggerName, message, args );
	}

	default void fine( String loggerName, String message, Object... args )
	{
		log( Level.FINE, loggerName, message, args );
	}

	default void finest( String loggerName, String message, Object... args )
	{
		log( Level.FINEST, loggerName, message, args );
	}

	default void info( String loggerName, String message, Object... args )
	{
		log( Level.INFO, loggerName, message, args );
	}

	void log( Level level, String loggerName, String message, Object... args );

	default void log( Level level, String loggerName, Throwable cause, String message, Object... args )
	{
		log( level, loggerName, message, args );
		log( level, loggerName, cause );
	}

	void log( Level level, String loggerName, Throwable cause );

	default void severe( String loggerName, Throwable cause )
	{
		log( Level.SEVERE, loggerName, cause );
	}

	default void severe( String loggerName, String message, Object... args )
	{
		log( Level.SEVERE, loggerName, message, args );
	}

	default void severe( String loggerName, String message, Throwable cause, Object... args )
	{
		log( Level.SEVERE, loggerName, cause, message, args );
	}

	default void warning( String loggerName, Throwable cause )
	{
		log( Level.WARNING, loggerName, cause );
	}

	default void warning( String loggerName, String message, Object... args )
	{
		log( Level.WARNING, loggerName, message, args );
	}

	default void warning( String loggerName, String message, Throwable cause, Object... args )
	{
		log( Level.WARNING, loggerName, cause, message, args );
	}
}
