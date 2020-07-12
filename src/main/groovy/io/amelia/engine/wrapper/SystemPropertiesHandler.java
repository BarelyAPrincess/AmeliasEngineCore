package io.amelia.engine.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SystemPropertiesHandler
{
	static final String SYSTEM_PROP_PREFIX = "systemProp.";

	public static Map<String, String> getSystemProperties( Path propertiesFile )
	{
		Map<String, String> propertyMap = new HashMap<>();
		if ( !Files.isRegularFile( propertiesFile ) )
			return propertyMap;
		Properties properties = new Properties();
		try
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
		catch ( IOException e )
		{
			throw new RuntimeException( "Error when loading properties file=" + propertiesFile, e );
		}

		for ( Object argument : properties.keySet() )
			if ( argument.toString().startsWith( SYSTEM_PROP_PREFIX ) )
			{
				String key = argument.toString().substring( SYSTEM_PROP_PREFIX.length() );
				if ( key.length() > 0 )
					propertyMap.put( key, properties.get( argument ).toString() );
			}

		return propertyMap;
	}
}
