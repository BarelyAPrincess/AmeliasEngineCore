package io.amelia.engine;

import io.amelia.lang.ApplicationException;

public class EntryPoint
{
	public static void main( String... args ) throws Exception
	{
		EngineCore.init();

		EngineApplication app = EngineCore.getApplication();

		try
		{
			app.parse( args );
		}
		catch ( ApplicationException.StartupInterrupt | ApplicationException.Crash e )
		{
			// Prevent exception from being printed to console
			return;
		}

		EngineCore.prepare();
	}
}
