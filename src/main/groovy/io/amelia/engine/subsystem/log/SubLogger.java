package io.amelia.engine.subsystem.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import io.amelia.engine.subsystem.EngineCore;
import io.amelia.engine.subsystem.ConfigRegistry;

public class SubLogger extends Logger
{
	protected SubLogger( String name )
	{
		super( name, null );

		setParent( EngineLogRegistry.ROOT );
		setLevel( Level.ALL );
	}

	@Override
	public void log( LogRecord logRecord )
	{
		if ( ConfigRegistry.isLoaded() && !ConfigRegistry.config.getBoolean( "console.hideLoggerName" ).orElse( false ) || EngineCore.isDevelopment() )
			logRecord.setMessage( "&7[" + getName() + "]&f " + logRecord.getMessage() );

		super.log( logRecord );
	}
}
