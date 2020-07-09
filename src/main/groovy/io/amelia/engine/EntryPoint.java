package io.amelia.engine;

import io.amelia.lang.StartupInterruptException;

public class EntryPoint
{
	public static void main( String... args ) throws Exception
	{
		EngineCore.init();

		EngineCoreApplication app = EngineCore.getApplication();

		try
		{
			app.parse( args );
		}
		catch ( StartupInterruptException e )
		{
			// Prevent exception from being printed to console
			return;
		}

		EngineCore.prepare();
	}
}
