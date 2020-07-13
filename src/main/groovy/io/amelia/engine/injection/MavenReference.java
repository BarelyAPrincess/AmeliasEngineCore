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
import java.nio.file.Path;

import io.amelia.lang.ApplicationException;
import io.amelia.support.IO;

/**
 * Used to parse for a new library
 * This is a very very crude class but it will be improved upon one day soon
 */
public class MavenReference
{
	private final String group;
	private final String name;
	private final String source;
	private final String version;

	/**
	 * Constructs the MavenReference class from a maven string
	 *
	 * @param sourceName The library loading source
	 * @param maven      Maven library string, e.g., com.chiorichan:ChioriWebServer:9.3.0
	 */
	public MavenReference( String sourceName, String maven )
	{
		source = sourceName;

		String[] parts = maven.split( ":" );

		if ( parts.length > 3 || parts.length < 3 )
			throw new IllegalArgumentException( "Invalid array count, must equal exactly three parts with delimiter ':', i.e., group:name:version. " + maven );

		group = parts[0];
		name = parts[1];
		version = parts[2];
	}

	/**
	 * Constructs the MavenReference class from a separate group, name, and version
	 *
	 * @param sourceName The library loading source
	 * @param group      The library group, e.g., com.chiorichan
	 * @param name       The library name, e.g., ChioriWebServer
	 * @param version    The library version, e.g., 9.3.0
	 */
	public MavenReference( String sourceName, String group, String name, String version )
	{
		source = sourceName;
		this.group = group;
		this.name = name;
		this.version = version;
	}

	/**
	 * Produces a the base directory for JAR and POM file paths
	 *
	 * @return Library base directory, e.g., libraries/com/dropbox/core/dropbox-core-sdk/1.7.7
	 */
	public Path basePath()
	{
		Path basePath = Libraries.LIBRARY_DIR.resolve( getGroup().replaceAll( "\\.", "/" ) + "/" + getName() + "/" + getVersion() );
		try
		{
			IO.forceCreateDirectory( basePath );
		}
		catch ( IOException e )
		{
			throw new ApplicationException.Runtime( e );
		}
		return basePath;
	}

	/**
	 * @return The library group
	 */
	public String getGroup()
	{
		return group;
	}

	/**
	 * The library key
	 *
	 * @return The library key, e.g., com.chiorichan:ChioriWebServer
	 */
	String getKey()
	{
		return group + ":" + name;
	}

	/**
	 * @return The library name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * The source of what loaded this library
	 *
	 * @return The LibrarySource
	 */
	public LibrarySource getSource()
	{
		if ( "builtin".equals( source ) )
			return Libraries.SELF;

		// TODO Implement new source of LibrarySource
		// return PluginManager.instance().getPluginByNameWithoutException( source );

		return null;
	}

	/**
	 * @return The library version number
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Produces a local JAR file path
	 *
	 * @return JAR file path, e.g., libraries/com/dropbox/core/dropbox-core-sdk/1.7.7/dropbox-core-sdk-1.7.7.jar
	 */
	public Path jarPath()
	{
		return basePath().resolve( getName() + "-" + getVersion() + ".jar" );
	}

	/**
	 * Produces a Maven Download URL
	 *
	 * @param ext The url extension, i.e., jar or pom
	 *
	 * @return Maven Download URL, e.g., http://jcenter.bintray.com/org/xerial/sqlite-jdbc/3.8.11.2/sqlite-jdbc-3.8.11.2.jar
	 */
	public String mavenUrl( String ext )
	{
		return Libraries.BASE_MAVEN_URL + group.replaceAll( "\\.", "/" ) + "/" + name + "/" + version + "/" + name + "-" + version + "." + ext;
	}

	/**
	 * Produces a Maven Download URL using the alternative base URL
	 *
	 * @param ext The url extension, i.e., jar or pom
	 *
	 * @return Maven Download URL, e.g., http://jcenter.bintray.com/org/xerial/sqlite-jdbc/3.8.11.2/sqlite-jdbc-3.8.11.2.jar
	 */
	public String mavenUrlAlt( String ext )
	{
		return Libraries.BASE_MAVEN_URL_ALT + group.replaceAll( "\\.", "/" ) + "/" + name + "/" + version + "/" + name + "-" + version + "." + ext;
	}

	/**
	 * Produces a local POM file path
	 *
	 * @return POM file path, e.g., libraries/com/dropbox/core/dropbox-core-sdk/1.7.7/dropbox-core-sdk-1.7.7.pom
	 */
	public Path pomPath()
	{
		return basePath().resolve( getName() + "-" + getVersion() + ".pom" );
	}

	/**
	 * Produces a Maven String
	 *
	 * @return Maven String, e.g., io.amelia:AmeliaCommonLib:1.0.0
	 */
	@Override
	public String toString()
	{
		return group + ":" + name + ":" + version;
	}
}
