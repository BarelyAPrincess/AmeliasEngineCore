/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.injection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import io.amelia.engine.EngineCore;
import io.amelia.engine.storage.StorageBus;
import io.amelia.engine.log.L;
import io.amelia.extra.UtilityIO;
import io.amelia.extra.UtilityObjects;
import io.amelia.lang.ApplicationException;
import io.amelia.lang.ReportingLevel;
import io.amelia.support.EnumColor;
import io.amelia.support.Http;

/**
 * Used as a helper class for retrieving files from the central maven repository
 */
public class Libraries implements LibrarySource
{
	public static final String BASE_MAVEN_URL = "http://jcenter.bintray.com/";
	public static final String BASE_MAVEN_URL_ALT = "http://search.maven.org/remotecontent?filepath=";
	public static final Path INCLUDES_DIR;
	public static final Path LIBRARY_DIR;
	public static final Libraries SELF = new Libraries();
	public static Map<String, MavenReference> loadedLibraries = new HashMap<>();

	static
	{
		L.init( Libraries.class.getSimpleName() );

		LIBRARY_DIR = StorageBus.getPathAndCreate( StorageBus.PATH_LIBS );
		INCLUDES_DIR = Paths.get( "local" ).resolve( LIBRARY_DIR );

		try
		{
			UtilityIO.forceCreateDirectory( LIBRARY_DIR );
			UtilityIO.forceCreateDirectory( INCLUDES_DIR );

			UtilityIO.setOwnerReadWritePermissions( LIBRARY_DIR );
			UtilityIO.setOwnerReadWritePermissions( INCLUDES_DIR );
		}
		catch ( IOException e )
		{
			throw new ApplicationException.Uncaught( ReportingLevel.E_ERROR, "There was a problem confirming Read/Write permissions on the libraries directory." );
		}

		try
		{
			// Scans the 'libraries/local' directory for jar files that can be injected into the classpath
			Files.list( INCLUDES_DIR ).filter( file -> file.getFileName().toString().toLowerCase().endsWith( ".jar" ) ).forEach( Libraries::loadLibrary );
		}
		catch ( IOException e )
		{
			// Do Nothing!
		}
	}

	public static Path getLibraryDir()
	{
		return LIBRARY_DIR;
	}

	public static List<MavenReference> getLoadedLibraries()
	{
		return new ArrayList<>( loadedLibraries.values() );
	}

	public static List<MavenReference> getLoadedLibrariesBySource( LibrarySource source )
	{
		List<MavenReference> references = new ArrayList<>();

		for ( MavenReference ref : loadedLibraries.values() )
			if ( ref.getSource() == source )
				references.add( ref );

		return references;
	}

	public static MavenReference getReferenceByGroup( String group )
	{
		UtilityObjects.notNull( group );
		for ( MavenReference ref : loadedLibraries.values() )
			if ( group.equalsIgnoreCase( ref.getGroup() ) )
				return ref;
		return null;
	}

	public static MavenReference getReferenceByName( String name )
	{
		UtilityObjects.notNull( name );
		for ( MavenReference ref : loadedLibraries.values() )
			if ( name.equalsIgnoreCase( ref.getName() ) )
				return ref;
		return null;
	}

	public static boolean isLoaded( MavenReference lib )
	{
		return loadedLibraries.containsKey( lib.getKey() );
	}

	public static boolean loadLibrary( @Nonnull Path libPath )
	{
		if ( !Files.isRegularFile( libPath ) )
			return false;

		L.info( EnumColor.GRAY + "Loading the library \"" + UtilityIO.relPath( libPath ) + "\"" );

		try
		{
			LibraryClassLoader.addPath( libPath );
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
			return false;
		}

		try
		{
			UtilityIO.extractNatives( libPath, libPath.getParent() );
		}
		catch ( IOException e )
		{
			L.severe( "We had a problem trying to extract native libraries from jar file '" + libPath.toString() + "'", e );
		}

		return true;
	}

	public static boolean loadLibrary( MavenReference lib )
	{
		String urlJar = lib.mavenUrl( "jar" );
		String urlPom = lib.mavenUrl( "pom" );

		Path mavenLocalJar = lib.jarPath();
		Path mavenLocalPom = lib.pomPath();

		if ( urlJar == null || urlJar.isEmpty() || urlPom == null || urlPom.isEmpty() )
			return false;

		try
		{
			if ( !Files.isRegularFile( mavenLocalPom ) || !Files.isRegularFile( mavenLocalJar ) )
			{
				L.info( EnumColor.GOLD + "Downloading the library \"" + lib.toString() + "\" from url \"" + urlJar + "\"... Please Wait!" );

				// Try download from JCenter Bintray Maven Repository
				try
				{
					Http.downloadFile( urlPom, mavenLocalPom );
					Http.downloadFile( urlJar, mavenLocalJar );
				}
				catch ( IOException e )
				{
					// Try download from alternative Maven Central Repository
					String urlJarAlt = lib.mavenUrlAlt( "jar" );
					String urlPomAlt = lib.mavenUrlAlt( "pom" );

					L.warning( "Primary download location failed, trying secondary location \"" + urlJarAlt + "\"... Please Wait!", EngineCore.isDevelopment() ? e : null );

					try
					{
						Http.downloadFile( urlPomAlt, mavenLocalPom );
						Http.downloadFile( urlJarAlt, mavenLocalJar );
					}
					catch ( IOException ee )
					{
						L.severe( "Primary and secondary download location have FAILED!", EngineCore.isDevelopment() ? ee : null );
						return false;
					}
				}
			}

			L.info( "Loading library \"" + lib.toString() + "\" from file \"" + mavenLocalJar + "\"..." );

			LibraryClassLoader.addPath( mavenLocalJar );
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
			return false;
		}

		loadedLibraries.put( lib.getKey(), lib );
		try
		{
			UtilityIO.extractNatives( lib.jarPath(), lib.basePath() );
		}
		catch ( IOException e )
		{
			L.severe( "We had a problem trying to extract native libraries from jar file \"" + lib.jarPath() + "\"", e );
		}

		return true;
	}

	private Libraries()
	{

	}

	@Override
	public String getName()
	{
		return "builtin";
	}
}
