package io.amelia.engine.wrapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import io.amelia.support.Voluntary;

public class BootstrapMainStarter
{
	static Voluntary<Path> findLauncherJar( @Nonnull Path engineHome )
	{
		Path libDirectory = engineHome.resolve( "lib" );
		if ( Files.isDirectory( libDirectory ) )
			try
			{
				return Voluntary.of( Files.list( libDirectory ).filter( name -> name.getFileName().toString().matches( "engine-launcher-.*\\.jar" ) ).findFirst() );
			}
			catch ( IOException e )
			{
				// ignore
			}
		return Voluntary.empty();
	}

	public void start( String[] args, Path engineHome ) throws Exception
	{
		Voluntary<Path> engineJar = findLauncherJar( engineHome );
		if ( !engineJar.isPresent() )
			throw new RuntimeException( String.format( "Could not locate the engine launcher JAR in engine distribution '%s'.", engineHome ) );

		URLClassLoader contextClassLoader = new URLClassLoader( new URL[] {engineJar.get().toUri().toURL()}, ClassLoader.getSystemClassLoader().getParent() );
		Thread.currentThread().setContextClassLoader( contextClassLoader );
		Class<?> mainClass = contextClassLoader.loadClass( "io.amelia.engine.EntryPoint" );
		Method mainMethod = mainClass.getMethod( "main", String[].class );
		mainMethod.invoke( null, new Object[] {args} );
		contextClassLoader.close();
	}
}