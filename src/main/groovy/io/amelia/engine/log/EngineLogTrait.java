package io.amelia.engine.log;

import java.util.logging.Level;

public interface EngineLogTrait
{
	default void debug( String message, Object... args )
	{
		log( Level.CONFIG, message, args );
	}

	default void fine( String message, Object... args )
	{
		log( Level.FINE, message, args );
	}

	default void finest( String message, Object... args )
	{
		log( Level.FINEST, message, args );
	}

	default void info( String message, Object... args )
	{
		log( Level.INFO, message, args );
	}

	void log( Level level, String message, Object... args );

	default void log( Level level, Throwable cause, String message, Object... args )
	{
		log( level, message, args );
		log( level, cause );
	}

	void log( Level level, Throwable cause );

	default void severe( Throwable cause )
	{
		log( Level.SEVERE, cause );
	}

	default void severe( String message, Object... args )
	{
		log( Level.SEVERE, message, args );
	}

	default void severe( String message, Throwable cause, Object... args )
	{
		log( Level.SEVERE, cause, message, args );
	}

	default void warning( Throwable cause )
	{
		log( Level.WARNING, cause );
	}

	default void warning( String message, Object... args )
	{
		log( Level.WARNING, message, args );
	}

	default void warning( String message, Throwable cause, Object... args )
	{
		log( Level.WARNING, cause, message, args );
	}
}
