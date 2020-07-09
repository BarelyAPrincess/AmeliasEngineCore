package io.amelia.engine.subsystem.log;

import java.util.logging.Level;

public class EngineLogger
{
	public static final String GLOBAL_LOGGER_NAME = "io.amelia";

	public static EngineLogger getLogger( String loggerName )
	{

	}

	private String loggerName;

	public EngineLogger( String loggerName )
	{
		this.loggerName = loggerName;
	}

	public void log( Level level, String message, Object... args )
	{
		EngineLogManager.getHandler().log( level, loggerName, message, args );
	}

	public void log( Level level, Throwable cause )
	{
		EngineLogManager.getHandler().log( level, loggerName, cause );
	}
}
