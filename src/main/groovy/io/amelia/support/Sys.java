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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.amelia.engine.EngineCore;
import io.amelia.extra.UtilityIO;

/**
 * Provides easy access to the server metadata plus operating system and jvm information
 */
public class Sys
{
	public static final String OS_NAME = System.getProperty( "os.name" );
	public static final String OS_VERSION = System.getProperty( "os.version" );

	private static boolean doesOSMatch( String prefix )
	{
		return OS_NAME.startsWith( prefix );
	}

	public static io.amelia.support.Voluntary<Path> getAppPath() throws IOException
	{
		try
		{
			if ( EngineCore.isDevelopment() )
				return io.amelia.support.Voluntary.of( Files.createDirectories( Paths.get( "workspace" ) ) );
			Path path = Paths.get( io.amelia.support.Sys.class.getProtectionDomain().getCodeSource().getLocation().toURI() );
			if ( Files.isDirectory( path ) )
				return io.amelia.support.Voluntary.of( path );
			if ( path.endsWith( ".jar" ) ) // Remove jar file
				path = path.getParent();
			if ( path.endsWith( ".class" ) )
				throw new IOException( "We failed to resolve app directory, app is running from compiled class files. Specify using \"app-dir\" argument." );
			if ( path.endsWith( "bin" ) ) // Remove bin directory if present.
				path = path.getParent();
			return io.amelia.support.Voluntary.of( path );
		}
		catch ( Exception e )
		{
			if ( e instanceof IOException )
				throw ( IOException ) e;
			else
				throw new IOException( e );
		}
	}

	/**
	 * Get the JVM name
	 *
	 * @return The JVM name
	 */
	public static String getJVMName()
	{
		// System.getProperty("java.vm.name");
		return ManagementFactory.getRuntimeMXBean().getVmName();
	}

	public static String getJarName( String def )
	{
		try
		{
			// TODO Will using this class cause the method to return the jar containing AmeliaCommonLib?
			Path path = Paths.get( io.amelia.support.Sys.class.getProtectionDomain().getCodeSource().getLocation().toURI() );
			if ( Files.isDirectory( path ) || !path.endsWith( ".jar" ) )
				return def;
			return UtilityIO.getLocalName( path );
		}
		catch ( Exception e )
		{
			if ( EngineCore.isDevelopment() )
				e.printStackTrace();
			return def;
		}
	}

	/**
	 * Get the Java Binary
	 *
	 * @return the Java Binary location
	 */
	public static String getJavaBinary()
	{
		String path = System.getProperty( "java.home" ) + File.pathSeparator + "bin" + File.pathSeparator;

		if ( isWindows() )
			if ( new File( path + "javaw.exe" ).isFile() )
				return path + "javaw.exe";
			else if ( new File( path + "java.exe" ).isFile() )
				return path + "java.exe";

		return path + "java";
	}

	/*
	 * Java and JVM Methods
	 */

	/**
	 * Get the Java version, e.g., 1.7.0_80
	 *
	 * @return The Java version number
	 */
	public static String getJavaVersion()
	{
		return System.getProperty( "java.version" );
	}

	public static Integer getProcessID()
	{
		// Confirmed working on Debian Linux, Windows?

		String pid = ManagementFactory.getRuntimeMXBean().getName();

		if ( pid != null && pid.contains( "@" ) )
			pid = pid.substring( 0, pid.indexOf( "@" ) );

		return Integer.parseInt( pid );
	}

	/**
	 * Get the system username
	 *
	 * @return The username
	 */
	public static String getUser()
	{
		return System.getProperty( "user.name" );
	}

	/**
	 * Indicates if we are running as either the root user for Unix-like or Administrator user for Windows
	 *
	 * @return True if Administrator or root
	 */
	public static boolean isAdminUser()
	{
		return "root".equalsIgnoreCase( System.getProperty( "user.name" ) ) || "administrator".equalsIgnoreCase( System.getProperty( "user.name" ) );
	}

	/**
	 * Indicates if we are running Mac OS X
	 *
	 * @return True if we are running on Mac
	 */
	public static boolean isMac()
	{
		return doesOSMatch( "Mac" );
	}

	/**
	 * Indicates if the provided PID is still running, this method is setup to work with both Windows and Linux, might need tuning for other OS's
	 *
	 * @param pid
	 *
	 * @return is the provided PID running
	 */
	public static boolean isPIDRunning( int pid ) throws IOException
	{
		String[] cmds;
		if ( isUnixLikeOS() )
			cmds = new String[] {"sh", "-c", "ps -ef | grep " + pid + " | grep -v grep"};
		else
			cmds = new String[] {"cmd", "/c", "tasklist /FI \"PID eq " + pid + "\""};

		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec( cmds );

		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader( inputstream );
		BufferedReader bufferedreader = new BufferedReader( inputstreamreader );
		String line;
		while ( ( line = bufferedreader.readLine() ) != null )
			if ( line.contains( " " + pid + " " ) )
				return true;

		return false;
	}

	/**
	 * Indicates if we are running on Solaris OS
	 *
	 * @return True if we are running on Solaris
	 */
	public static boolean isSolaris()
	{
		return doesOSMatch( "Solaris" );
	}

	/**
	 * Indicates if we are running on an Unix-like Operating System, e.g., Linux or Max OS X
	 *
	 * @return True if we are running on an Unix-like OS.
	 */
	public static boolean isUnixLikeOS()
	{
		for ( String prefix : new String[] {"AIX", "HP-UX", "Irix", "Linux", "LINUX", "Mac OS X", "FreeBSD", "OpenBSD", "NetBSD", "Solaris", "Sun OS"} )
			if ( doesOSMatch( prefix ) )
				return true;
		return false;
	}

	/**
	 * Indicates if we are running on a Windows Operating System
	 *
	 * @return True if we are running on Windows OS
	 */
	public static boolean isWindows()
	{
		return doesOSMatch( "Windows" );
	}

	public static boolean terminatePID( int pid ) throws IOException
	{
		String[] cmds;
		if ( isUnixLikeOS() )
			cmds = new String[] {"sh", "-c", "kill -9 " + pid};
		else
			cmds = new String[] {"cmd", "/c", "taskkill /f /pid " + pid};

		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec( cmds );

		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader( inputstream );
		BufferedReader bufferedreader = new BufferedReader( inputstreamreader );
		String line;
		while ( ( line = bufferedreader.readLine() ) != null )
		{
			// TODO Wait until process returns
		}

		try
		{
			Thread.sleep( 1000 );
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}

		return !isPIDRunning( pid );
	}

	private Sys()
	{

	}
}
