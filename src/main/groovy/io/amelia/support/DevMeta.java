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
import io.amelia.engine.log.L;
import io.amelia.lang.ApplicationException;
import io.amelia.extra.UtilityIO;

public class DevMeta implements DevMetaProvider
{
	private Properties prop = new Properties();

	public DevMeta()
	{
		this( "build.properties" );
	}

	public DevMeta( @Nonnull Path propFile )
	{
		try
		{
			loadProp( Files.newInputStream( propFile ) );
		}
		catch ( IOException e )
		{
			L.severe( "The DevMeta file \"" + propFile + "\" does not exist!" );
		}
	}

	public DevMeta( @Nonnull File propFile )
	{
		try
		{
			loadProp( new FileInputStream( propFile ) );
		}
		catch ( FileNotFoundException e )
		{
			L.severe( "The DevMeta file \"" + propFile + "\" does not exist!" );
		}
	}

	public DevMeta( @Nonnull String fileName )
	{
		this( EngineCore.class, fileName );
	}

	public DevMeta( @Nonnull Class<?> cls, @Nonnull String fileName )
	{
		InputStream is = cls.getClassLoader().getResourceAsStream( fileName );
		if ( is == null )
			L.severe( "The DevMeta file \"" + fileName + "\" does not exist!" );
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

	private void loadProp( @Nonnull InputStream is )
	{
		try
		{
			prop.load( is );
		}
		catch ( IOException e )
		{
			L.severe( e );
		}
		finally
		{
			UtilityIO.closeQuietly( is );
		}
	}
}
