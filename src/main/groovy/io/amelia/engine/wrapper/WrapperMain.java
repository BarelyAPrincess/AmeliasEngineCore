package io.amelia.engine.wrapper;

import org.gradle.cli.CommandLineParser;
import org.gradle.cli.ParsedCommandLine;
import org.gradle.cli.SystemPropertiesCommandLineConverter;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;

public class WrapperMain
{
	public static final String USER_HOME_OPTION = "g";
	public static final String USER_HOME_DETAILED_OPTION = "engine-user-home";
	public static final String QUIET_OPTION = "q";
	public static final String QUIET_DETAILED_OPTION = "quiet";

	private static void addSystemProperties( Path engineHome, Path rootDir )
	{
		System.getProperties().putAll( SystemPropertiesHandler.getSystemProperties( engineHome.resolve( "engine.properties" ) ) );
		System.getProperties().putAll( SystemPropertiesHandler.getSystemProperties( rootDir.resolve( "engine.properties" ) ) );
	}

	private static Path engineUserHome( ParsedCommandLine options )
	{
		if ( options.hasOption( USER_HOME_OPTION ) )
			return Paths.get( options.option( USER_HOME_OPTION ).getValue() );
		return UserHomeLookup.getUserHome();
	}

	public static void main( String[] args ) throws Exception
	{
		Path wrapperJar = wrapperJar();
		Path propertiesFile = wrapperProperties( wrapperJar );
		Path rootDir = rootDir( wrapperJar );

		CommandLineParser parser = new CommandLineParser();
		parser.allowUnknownOptions();
		parser.option( USER_HOME_OPTION, USER_HOME_DETAILED_OPTION ).hasArgument();
		parser.option( QUIET_OPTION, QUIET_DETAILED_OPTION );

		SystemPropertiesCommandLineConverter converter = new SystemPropertiesCommandLineConverter();
		converter.configure( parser );

		ParsedCommandLine options = parser.parse( args );

		Properties systemProperties = System.getProperties();
		systemProperties.putAll( converter.convert( options, new HashMap<String, String>() ) );

		Path engineUserHome = engineUserHome( options );

		addSystemProperties( engineUserHome, rootDir );

		WrapperExecutor wrapperExecutor = WrapperExecutor.forWrapperPropertiesFile( propertiesFile );
		wrapperExecutor.execute( args, new Install( new Download( "enginew", Download.UNKNOWN_VERSION ), new PathAssembler( engineUserHome ) ), new BootstrapMainStarter() );
	}

	private static Path rootDir( Path wrapperJar )
	{
		return wrapperJar.getParent().getParent().getParent();
	}

	private static Path wrapperJar()
	{
		URI location;
		try
		{
			location = WrapperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		}
		catch ( URISyntaxException e )
		{
			throw new RuntimeException( e );
		}
		if ( !location.getScheme().equals( "file" ) )
		{
			throw new RuntimeException( String.format( "Cannot determine classpath for wrapper Jar from codebase '%s'.", location ) );
		}
		try
		{
			return Paths.get( location );
		}
		catch ( NoClassDefFoundError e )
		{
			return Paths.get( location.getPath() );
		}
	}

	private static Path wrapperProperties( Path wrapperJar )
	{
		return wrapperJar.getParent().resolve( wrapperJar.getFileName().toString().replaceFirst( "\\.jar$", ".properties" ) );
	}
}
