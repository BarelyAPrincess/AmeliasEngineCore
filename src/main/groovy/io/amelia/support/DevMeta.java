package io.amelia.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javax.annotation.Nonnull;

import io.amelia.engine.EngineCore;
import io.amelia.lang.ApplicationException;
import io.amelia.extra.UtilityIO;

public class DevMeta implements DevMetaProvider
{
	private Properties prop = new Properties();

	public DevMeta() throws ApplicationException.Error, IOException
	{
		this( "build.properties" );
	}

	public DevMeta( @Nonnull Path propFile ) throws IOException, ApplicationException.Error
	{
		loadProp( Files.newInputStream( propFile ) );
	}

	public DevMeta( @Nonnull File propFile ) throws FileNotFoundException, ApplicationException.Error
	{
		loadProp( new FileInputStream( propFile ) );
	}

	public DevMeta( @Nonnull String fileName ) throws ApplicationException.Error, IOException
	{
		this( EngineCore.class, fileName );
	}

	public DevMeta( @Nonnull Class<?> cls, @Nonnull String fileName ) throws ApplicationException.Error
	{
		InputStream is = cls.getClassLoader().getResourceAsStream( fileName );
		if ( is == null )
			EngineCore.L.warning( "The DevMeta file \"" + fileName + "\" does not exist!" );
		else
			loadProp( is );
	}

	public DevMeta( @Nonnull InputStream is ) throws ApplicationException.Error
	{
		loadProp( is );
	}

	public String getProperty( @Nonnull String key )
	{
		return prop.getProperty( key );
	}

	private void loadProp( @Nonnull InputStream is ) throws ApplicationException.Error
	{
		try
		{
			prop.load( is );
		}
		catch ( IOException e )
		{
			throw new ApplicationException.Error( e );
		}
		finally
		{
			UtilityIO.closeQuietly( is );
		}
	}
}
