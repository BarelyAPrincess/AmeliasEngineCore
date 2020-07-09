package io.amelia.engine.subsystem.log;

import java.util.logging.Level;

import io.amelia.engine.subsystem.Subsystem;
import sun.reflect.Reflection;

public class L implements EngineLogTrait
{
	public static final EngineLogger GLOBAL = EngineLogger.getLogger( EngineLogger.GLOBAL_LOGGER_NAME );

	public static void init( String loggerName )
	{

	}

	/**
	 * Gets the EngineLogger based on the calling class.
	 *
	 * @return
	 */
	public EngineLogger getLogger()
	{
		Thread.currentThread().getStackTrace()[1].getClassName();



		return GLOBAL;
	}

	public void log( Level level, Class<?> source, String message, Object... args )
	{
		Subsystem.L.log( level, source, message, args );
	}

	public void log( Level level, Class<?> source, Throwable cause )
	{
		Subsystem.L.log( level, source, cause );
	}
}
