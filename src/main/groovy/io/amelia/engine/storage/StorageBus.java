package io.amelia.engine.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.amelia.engine.log.L;
import io.amelia.extra.UtilityArrs;
import io.amelia.extra.UtilityIO;
import io.amelia.extra.UtilityLists;
import io.amelia.extra.UtilityObjects;
import io.amelia.extra.UtilityStrings;
import io.amelia.lang.ApplicationException;

// File Input Output System
public class StorageBus
{
	public static final String PATH_APP = "__app";
	public static final String PATH_CACHE = "__cache";
	public static final String PATH_LOGS = "__logs";
	public static final String PATH_LIBS = "__libs";
	public static final String PATH_CONFIG = "__config";
	public static final String PATH_PLUGINS = "__plugins";
	public static final String PATH_UPDATES = "__updates";
	public static final String PATH_STORAGE = "__storage";
	private static final Map<String, List<String>> APP_PATHS = new ConcurrentHashMap<>();
	private static Path appPath;

	static
	{
		setPath( PATH_CACHE, PATH_STORAGE, "cache" );
		setPath( PATH_LOGS, PATH_STORAGE, "logs" );
		setPath( PATH_LIBS, PATH_APP, "libs" );
		setPath( PATH_CONFIG, PATH_APP, "config" );
		setPath( PATH_PLUGINS, PATH_APP, "plugins" );
		setPath( PATH_UPDATES, PATH_APP, "updates" );
		setPath( PATH_STORAGE, PATH_APP, "storage" );
	}

	/**
	 * Builds a directory based on the provided slugs.
	 * Key based paths MUST start with double underscores.
	 * <p>
	 * The options are as follows:
	 * __app
	 * __webroot
	 * __config
	 * __plugins
	 * __updates
	 * __database
	 * __storage
	 * __sessions
	 * __cache
	 * __logs
	 * <p>
	 * Slugs not starting with double underscores will be treated as either a relative
	 * or absolute path depending on if it starts with a single forward slash.
	 * <p>
	 * Examples:
	 * __app -> /usr/share/honeypot
	 * __sessions -> /usr/share/honeypot/storage/sessions
	 * relative -> /usr/share/honeypot/relative
	 * /absolute -> /absolute
	 * <p>
	 *
	 * @param slugs The path slugs
	 *
	 * @return The absolute File
	 *
	 * @throws ApplicationException.Ignorable
	 */
	public static Path getPath( @Nonnull String... slugs )
	{
		UtilityObjects.notNull( slugs );

		if ( slugs.length == 0 )
			return getPath();

		if ( slugs[0].startsWith( "__" ) )
		{
			String key = slugs[0].substring( 2 );
			if ( key.equalsIgnoreCase( "app" ) )
				slugs[0] = getPath().toString();
			else if ( APP_PATHS.containsKey( key ) )
				slugs = Stream.concat( APP_PATHS.get( key ).stream(), Arrays.stream( slugs ).skip( 1 ) ).toArray( String[]::new );
			else
				throw new ApplicationException.Ignorable( "Path \"" + key + "\" is not set!" );

			return getPath( slugs );
		}
		else if ( !slugs[0].startsWith( "/" ) )
			slugs = UtilityArrs.prepend( slugs, getPath().toString() );

		return UtilityIO.buildPath( true, slugs );
	}

	public static Path getPath()
	{
		UtilityObjects.notEmpty( appPath, "The app path is not set." );
		return appPath;
	}

	public static Path getPathAndCreate( String... slugs )
	{
		Path path = getPath( slugs );

		try
		{
			UtilityIO.forceCreateDirectory( path );
		}
		catch ( IOException e )
		{
			throw new ApplicationException.Ignorable( "The app path \"" + path.toString() + "\" does not exist and we couldn't create it.", e );
		}

		return path;
	}

	public static List<String> getPathSlugs()
	{
		return new ArrayList<>( APP_PATHS.keySet() );
	}

	public static void init()
	{

	}

	protected static void setAppPath( @Nonnull Path appPath ) throws IOException
	{
		UtilityObjects.notEmpty( appPath, "The app path is empty." );
		UtilityObjects.notFalse( Files.exists( appPath ), "The app path does not exist. {appPath=" + appPath.toString() + "}" );
		UtilityObjects.notFalse( Files.isDirectory( appPath ), "The app path is not a directory. {appPath=" + appPath.toString() + "}" );
		UtilityObjects.notFalse( Files.isWritable( appPath ), "The app path is not writable. {appPath=" + appPath.toString() + "}" );
		L.info( "App path set to \"" + appPath + "\"" );
		StorageBus.appPath = appPath.toRealPath();
	}

	public static void setPath( @Nonnull String pathKey, @Nonnull String... paths )
	{
		UtilityObjects.notEmpty( pathKey );
		if ( pathKey.startsWith( "__" ) )
			pathKey = pathKey.substring( 2 );

		final String key = pathKey.toLowerCase();

		if ( "app".equals( key ) )
			throw new IllegalArgumentException( "App path is set using the setAppPath() method." );
		if ( !Paths.get( paths[0] ).isAbsolute() && !paths[0].startsWith( "__" ) )
			throw new IllegalArgumentException( "App paths must be absolute or reference another app path, i.e., __app. Paths: [" + UtilityStrings.join( paths ) + "]" );
		APP_PATHS.put( key, UtilityLists.newArrayList( paths ) );
	}

	private StorageBus()
	{
		// Static Access
	}
}
