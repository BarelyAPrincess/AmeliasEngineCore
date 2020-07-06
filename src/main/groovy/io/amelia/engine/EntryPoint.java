package io.amelia.engine;

import io.amelia.engine.subsystem.Foundation;

public class EntryPoint
{
	public static void main( String... args ) throws Exception
	{
		Foundation.init();

		EngineCoreApplication app = new EngineCoreApplication();

		try
		{
			app.parse( args );
		}
		catch ( StartupInterruptException e )
		{
			// Prevent exception from being printed to console
			return;
		}


	}
}
