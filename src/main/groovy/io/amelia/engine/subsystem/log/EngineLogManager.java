package io.amelia.engine.subsystem.log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import io.amelia.support.EnumColor;
import io.amelia.extra.UtilityExceptions;
import io.amelia.extra.UtilityStrings;

public class EngineLogManager
{
	public static final SimpleDateFormat DEFAULT_TIMESTAMP = new SimpleDateFormat( "HH:mm:ss.SSS" );
	private static EngineLogHandler engineLogHandler;

	public static EngineLogHandler getHandler()
	{
		if ( engineLogHandler == null )
			engineLogHandler = new BuiltinEngineLogHandler();
		return engineLogHandler;
	}

	private EngineLogManager()
	{
		// Static Class
	}

	private static class BuiltinEngineLogHandler implements EngineLogHandler
	{
		@Override
		public void log( Level level, Class<?> source, String message, Object... args )
		{
			message = EnumColor.format( level, EnumColor.DARK_GRAY + DEFAULT_TIMESTAMP.format( new Date() ) + " [" + EnumColor.fromLevel( level ) + level.getName() + EnumColor.DARK_GRAY + "] " + EnumColor.fromLevel( level ) + ( args.length > 0 ? String.format( message, args ) : message ) );

			if ( level.intValue() <= Level.INFO.intValue() )
				System.out.println( message );
			else
				System.err.println( message );
		}

		@Override
		public void log( Level level, Class<?> source, Throwable cause )
		{
			UtilityStrings.split( UtilityExceptions.getStackTrace( cause ), "\n" ).forEach( str -> log( level, source, str ) );
		}
	}
}
