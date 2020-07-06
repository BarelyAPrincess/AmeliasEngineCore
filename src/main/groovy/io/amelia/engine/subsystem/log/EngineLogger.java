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

	public void debug( String message, Object... args )
	{
		EngineLogManager.getHandler().debug( loggerName, message, args );
	}

	public void fine( String message, Object... args )
	{
		EngineLogManager.getHandler().fine( loggerName, message, args );
	}

	public void finest( String message, Object... args )
	{
		EngineLogManager.getHandler().finest( loggerName, message, args );
	}

	public void info( String message, Object... args )
	{
		EngineLogManager.getHandler().info( loggerName, message, args );
	}

	public void log( Level level, String message, Object... args )
	{
		EngineLogManager.getHandler().log( level, loggerName, message, args );
	}

	public void log( Level level, Throwable cause )
	{
		EngineLogManager.getHandler().log( level, loggerName, cause );
	}

	public void severe( Throwable cause )
	{
		EngineLogManager.getHandler().severe( loggerName, cause );
	}

	public void severe( String message, Object... args )
	{
		EngineLogManager.getHandler().severe( loggerName, message, args );
	}

	public void severe( String message, Throwable cause, Object... args )
	{
		EngineLogManager.getHandler().severe( loggerName, message, cause, args );
	}

	public void warning( Throwable cause )
	{
		EngineLogManager.getHandler().warning( loggerName, cause );
	}

	public void warning( String message, Throwable cause, Object... args )
	{
		EngineLogManager.getHandler().warning( loggerName, message, cause, args );
	}

	public void warning( String message, Object... args )
	{
		EngineLogManager.getHandler().warning( loggerName, message, args );
	}
}
