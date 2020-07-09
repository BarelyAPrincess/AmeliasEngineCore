/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.support;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.amelia.data.ContainerWithValue;
import io.amelia.foundation.ConfigData;
import io.amelia.foundation.ConfigRegistry;
import io.amelia.lang.ConfigException;
import io.amelia.extra.UtilityObjects;
import io.amelia.extra.UtilityStrings;

/**
 * Provides an easy translator for content-types specified from configuration.
 */
public class ContentTypes
{
	public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

	public static void clearType( String ext ) throws ConfigException.Error
	{
		getConfigData().childDestroy( ext );
	}

	public static Stream<String> getAllTypes()
	{
		return getConfigData().getChildren().map( ContainerWithValue::getValue ).filter( io.amelia.support.Voluntary::isPresent ).map( io.amelia.support.Voluntary::get ).map( UtilityObjects::castToString );
	}

	private static ConfigData getConfigData()
	{
		return ConfigRegistry.config.getChildOrCreate( ConfigRegistry.ConfigKeys.CONTENT_TYPES );
	}

	@Nonnull
	public static Stream<String> getContentTypes( @Nonnull Path path )
	{
		if ( Files.isDirectory( path ) )
			return Stream.of( "directory" );

		return getContentTypes( path.getFileName().toString() );
	}

	@Nonnull
	public static Stream<String> getContentTypes( @Nonnull String filename )
	{
		String ext = UtilityStrings.regexCapture( filename, "\\.(\\w+)$" );
		return Stream.concat( getConfigData().getChildren().filter( child -> child.getLocalName().equalsIgnoreCase( ext ) && child.hasValue() ).flatMap( child -> UtilityStrings.split( child.getString().get(), "," ) ), Stream.of( "application/octet-stream" ) );
	}

	@Nonnull
	public static Stream<String> getContentTypes( @Nonnull File file )
	{
		if ( file.isDirectory() )
			return Stream.of( "directory" );

		return getContentTypes( file.getName() );
	}

	public static boolean isContentType( Path path, String test )
	{
		return getContentTypes( path ).anyMatch( contentType -> contentType.contains( test ) );
	}

	public static void setType( String ext, String type ) throws ConfigException.Error
	{
		ConfigData map = getConfigData().getChildOrCreate( ext );
		if ( map.hasValue() )
			map.setValue( map.getString().get() + "," + type );
		else
			map.setValue( type );
	}

	private ContentTypes()
	{
		// Static Access
	}
}
