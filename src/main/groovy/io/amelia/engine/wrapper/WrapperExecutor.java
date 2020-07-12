package io.amelia.engine.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import io.amelia.data.parcel.Parcel;

public class WrapperExecutor
{
	public static final String DISTRIBUTION_URL_PROPERTY = "distributionUrl";
	public static final String DISTRIBUTION_BASE_PROPERTY = "distributionBase";
	public static final String DISTRIBUTION_PATH_PROPERTY = "distributionPath";
	public static final String DISTRIBUTION_SHA_256_SUM = "distributionSha256Sum";
	public static final String ZIP_STORE_BASE_PROPERTY = "zipStoreBase";
	public static final String ZIP_STORE_PATH_PROPERTY = "zipStorePath";

	public static WrapperExecutor forProjectDirectory( Path applicationDir )
	{
		return new WrapperExecutor( applicationDir.resolve( "engine/wrapper/engine-wrapper.properties" ), new Properties() );
	}

	public static WrapperExecutor forWrapperPropertiesFile( Path propertiesFile )
	{
		if ( !Files.exists( propertiesFile ) )
			throw new RuntimeException( String.format( "Wrapper properties file '%s' does not exist.", propertiesFile ) );
		return new WrapperExecutor( propertiesFile, new Properties() );
	}

	private static void loadProperties( Path propertiesFile, Properties properties ) throws IOException
	{
		InputStream inStream = Files.newInputStream( propertiesFile );
		try
		{
			properties.load( inStream );
		}
		finally
		{
			inStream.close();
		}
	}

	private final Parcel config = Parcel.empty();
	private final Properties properties;
	private final Path propertiesFile;

	WrapperExecutor( Path propertiesFile, Properties properties )
	{
		this.properties = properties;
		this.propertiesFile = propertiesFile;
		if ( Files.exists( propertiesFile ) )
		{
			try
			{
				loadProperties( propertiesFile, properties );

				config.setValue( DISTRIBUTION_URL_PROPERTY, prepareDistributionUri() );
				config.setValue( DISTRIBUTION_BASE_PROPERTY, getProperty( DISTRIBUTION_BASE_PROPERTY, PathAssembler.ENGINE_USER_HOME_STRING ) );
				config.setValue( DISTRIBUTION_PATH_PROPERTY, getProperty( DISTRIBUTION_PATH_PROPERTY, Install.DEFAULT_DISTRIBUTION_PATH ) );
				config.setValue( DISTRIBUTION_SHA_256_SUM, getProperty( DISTRIBUTION_SHA_256_SUM, null, false ) );
				config.setValue( ZIP_STORE_BASE_PROPERTY, getProperty( ZIP_STORE_BASE_PROPERTY, PathAssembler.ENGINE_USER_HOME_STRING ) );
				config.setValue( ZIP_STORE_PATH_PROPERTY, getProperty( ZIP_STORE_PATH_PROPERTY, Install.DEFAULT_DISTRIBUTION_PATH ) );
			}
			catch ( Exception e )
			{
				throw new RuntimeException( String.format( "Could not load wrapper properties from '%s'.", propertiesFile ), e );
			}
		}
	}

	public void execute( String[] args, Install install, BootstrapMainStarter bootstrapMainStarter ) throws Exception
	{
		Path engineHome = install.createDist( config );
		bootstrapMainStarter.start( args, engineHome );
	}

	/**
	 * Returns the configuration for this wrapper.
	 */
	public Parcel getConfiguration()
	{
		return config;
	}

	/**
	 * Returns the distribution which this wrapper will use. Returns null if no wrapper meta-data was found in the specified project directory.
	 */
	public URI getDistribution()
	{
		return config.getValue( DISTRIBUTION_URL_PROPERTY ).map( o -> ( URI ) o ).orElse( null );
	}

	private String getProperty( String propertyName )
	{
		return getProperty( propertyName, null, true );
	}

	private String getProperty( String propertyName, String defaultValue )
	{
		return getProperty( propertyName, defaultValue, true );
	}

	private String getProperty( String propertyName, String defaultValue, boolean required )
	{
		String value = properties.getProperty( propertyName );
		if ( value != null )
		{
			return value;
		}
		if ( defaultValue != null )
		{
			return defaultValue;
		}
		if ( required )
		{
			return reportMissingProperty( propertyName );
		}
		else
		{
			return null;
		}
	}

	private URI prepareDistributionUri() throws URISyntaxException
	{
		URI source = readDistroUrl();
		if ( source.getScheme() == null )
			//no scheme means someone passed a relative url. In our context only file relative urls make sense.
			return propertiesFile.getParent().resolve( source.getSchemeSpecificPart() ).toUri();
		else
			return source;
	}

	private URI readDistroUrl() throws URISyntaxException
	{
		if ( properties.getProperty( DISTRIBUTION_URL_PROPERTY ) == null )
			reportMissingProperty( DISTRIBUTION_URL_PROPERTY );
		return new URI( getProperty( DISTRIBUTION_URL_PROPERTY ) );
	}

	private String reportMissingProperty( String propertyName )
	{
		throw new RuntimeException( String.format( "No value with key '%s' specified in wrapper properties file '%s'.", propertyName, propertiesFile ) );
	}
}
