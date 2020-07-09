/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.subsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import io.amelia.data.ContainerBase;
import io.amelia.engine.EngineCore;
import io.amelia.lang.ConfigException;
import io.amelia.extra.UtilityIO;
import io.amelia.extra.UtilityObjects;
import io.amelia.support.Streams;

public class ConfigRegistry
{
	public static final ConfigData config = ConfigData.empty();
	private static boolean loaded = false;

	// TODO This LOADER is not thread-safe but if the application initialization works as intended, this shouldn't be an issue.
	public static final ConfigLoader LOADER = new ConfigLoader()
	{
		private ThreadLocal<ConfigData> configs = new ThreadLocal<>();

		@Override
		public ConfigData beginConfig() throws ConfigException.Error
		{
			if ( configs.get() != null )
				throw new ConfigException.Error( configs.get(), "There is existing configuration, it must be first be committed or destroyed!" );

			ConfigData config = ConfigData.empty();
			configs.set( config );
			return config;
		}

		/**
		 *
		 * @param type Future Use
		 *
		 * @throws ConfigException.Error
		 */
		@Override
		public void commitConfig( @Nonnull ConfigLoader.CommitType type ) throws ConfigException.Error
		{
			ConfigData config = configs.get();
			if ( config == null )
				throw new ConfigException.Error( null, "There is no configuration to commit. Use destroy() to use beginConfig()." );
			Streams.forEachWithException( config.getChildren(), child -> config.addChild( null, child, ContainerBase.ConflictStrategy.MERGE ) );
			configs.remove();
			loaded = true;
		}

		@Override
		public ConfigData config() throws ConfigException.Error
		{
			ConfigData config = configs.get();
			if ( config == null )
				throw new ConfigException.Error( null, "There is no configuration to commit, you must first use beginConfig()." );
			return config;
		}

		@Override
		public void destroy()
		{
			configs.remove();
		}

		@Override
		public boolean hasBeganConfig()
		{
			return configs.get() != null;
		}
	};

	/*
	 * We set default config values here for end-user reference, they're then saved to the config file upon load (if unset).
	 */
	static
	{
		try
		{
			config.setValueIfAbsent( ConfigKeys.WARN_ON_OVERLOAD );
			config.setValueIfAbsent( ConfigKeys.DEVELOPMENT_MODE );
			config.setValueIfAbsent( ConfigKeys.DEFAULT_BINARY_CHARSET );
			config.setValueIfAbsent( ConfigKeys.DEFAULT_TEXT_CHARSET );
		}
		catch ( ConfigException.Error e )
		{
			// Ignore
		}
	}

	public static void clearCache( @Nonnull Path path, @Nonnegative long keepHistory )
	{
		UtilityObjects.notNull( path );
		UtilityObjects.notNull( keepHistory );
		UtilityObjects.notNegative( keepHistory );

		try
		{
			if ( Files.isDirectory( path ) )
				Streams.forEachWithException( Files.list( path ), file -> {
					if ( Files.isDirectory( file ) )
						clearCache( file, keepHistory );
					else if ( Files.isRegularFile( file ) && UtilityIO.getLastModified( file ) < System.currentTimeMillis() - keepHistory * 24 * 60 * 60 )
						Files.delete( file );
				} );
		}
		catch ( IOException e )
		{
			EngineCore.L.warning( "Exception thrown while clearing cache for directory " + path.toString(), e );
		}
	}

	public static void clearCache( @Nonnegative long keepHistory )
	{
		clearCache( Kernel.getPath( Kernel.PATH_CACHE ), keepHistory );
	}

	public static ConfigData getChild( String key )
	{
		return config.getChild( key );
	}

	public static ConfigData getChildOrCreate( String key )
	{
		return config.getChildOrCreate( key );
	}

	public static boolean isLoaded()
	{
		return loaded;
	}

	public static void save()
	{
		// TODO Save
	}

	/**
	 * Use with caution!
	 * ConfigRegistry will by default be marked as loaded once the ConfigLoader is committed for the first time.
	 *
	 * @param loaded the explicit value to set
	 */
	public static void setLoadedOverride( boolean loaded )
	{
		ConfigRegistry.loaded = loaded;
	}

	public static void setObject( String key, Object value ) throws ConfigException.Error
	{
		if ( value instanceof ConfigData )
			config.getChildOrCreate( key ).addChild( null, ( ConfigData ) value, ContainerBase.ConflictStrategy.OVERWRITE );
		else
			config.getChildOrCreate( key ).setValue( value );
	}

	private static void vendorConfig() throws IOException
	{
		// WIP Copies config from resources and plugins to config directories.

		Path configPath = Subsystem.FIOS.getPathAndCreate( Subsystem.FIOS.PATH_CONFIG );

		UtilityIO.extractResourceDirectory( "config", configPath, io.amelia.foundation.ConfigRegistry.class );
	}

	private ConfigRegistry()
	{
		// Static Access
	}
}
