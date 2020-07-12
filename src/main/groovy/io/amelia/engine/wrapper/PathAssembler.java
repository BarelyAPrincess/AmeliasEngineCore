package io.amelia.engine.wrapper;

import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import io.amelia.data.parcel.Parcel;

public class PathAssembler
{
	public static final String ENGINE_USER_HOME_STRING = "ENGINE_USER_HOME";
	public static final String PROJECT_STRING = "PROJECT";

	private Path gradleUserHome;

	public PathAssembler()
	{
	}

	public PathAssembler( Path gradleUserHome )
	{
		this.gradleUserHome = gradleUserHome;
	}

	private Path getBaseDir( String base )
	{
		if ( base.equals( ENGINE_USER_HOME_STRING ) )
			return gradleUserHome;
		else if ( base.equals( PROJECT_STRING ) )
			return Paths.get( System.getProperty( "user.dir" ) );
		else
			throw new RuntimeException( "Base: " + base + " is unknown" );
	}

	private String getDistName( URI distUrl )
	{
		String path = distUrl.getPath();
		int p = path.lastIndexOf( "/" );
		if ( p < 0 )
		{
			return path;
		}
		return path.substring( p + 1 );
	}

	/**
	 * Determines the local locations for the distribution to use given the supplied configuration.
	 */
	public LocalDistribution getDistribution( Parcel configuration )
	{
		String baseName = getDistName( configuration.getValue( WrapperExecutor.DISTRIBUTION_URL_PROPERTY ).map( o -> ( URI ) o ).orElse( null ) );
		String distName = removeExtension( baseName );
		String rootDirName = rootDirName( distName, configuration );
		Path distDir = getBaseDir( configuration.getString( WrapperExecutor.DISTRIBUTION_BASE_PROPERTY ).orElse( null ) ).resolve( configuration.getString( WrapperExecutor.DISTRIBUTION_PATH_PROPERTY ).orElse( null ) + "/" + rootDirName );
		Path distZip = getBaseDir( configuration.getString( WrapperExecutor.ZIP_STORE_BASE_PROPERTY ).orElse( null ) ).resolve( configuration.getString( WrapperExecutor.ZIP_STORE_PATH_PROPERTY ).orElse( null ) + "/" + rootDirName + "/" + baseName );
		return new LocalDistribution( distDir, distZip );
	}

	/**
	 * This method computes a hash of the provided {@code string}.
	 * <p>
	 * The algorithm in use by this method is as follows:
	 * <ol>
	 *    <li>Compute the MD5 value of {@code string}.</li>
	 *    <li>Truncate leading zeros (i.e., treat the MD5 value as a number).</li>
	 *    <li>Convert to base 36 (the characters {@code 0-9a-z}).</li>
	 * </ol>
	 */
	private String getHash( String string )
	{
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );
			byte[] bytes = string.getBytes();
			messageDigest.update( bytes );
			return new BigInteger( 1, messageDigest.digest() ).toString( 36 );
		}
		catch ( Exception e )
		{
			throw new RuntimeException( "Could not hash input string.", e );
		}
	}

	private String removeExtension( String name )
	{
		int p = name.lastIndexOf( "." );
		if ( p < 0 )
		{
			return name;
		}
		return name.substring( 0, p );
	}

	private String rootDirName( String distName, Parcel configuration )
	{
		String urlHash = getHash( Download.safeUri( configuration.getValue( WrapperExecutor.DISTRIBUTION_URL_PROPERTY ).map( o -> ( URI ) o ).orElse( null ) ).toString() );
		return distName + "/" + urlHash;
	}

	public static class LocalDistribution
	{
		private final Path distDir;
		private final Path distZip;

		public LocalDistribution( Path distDir, Path distZip )
		{
			this.distDir = distDir;
			this.distZip = distZip;
		}

		/**
		 * Returns the location to install the distribution into.
		 */
		public Path getDistributionDir()
		{
			return distDir;
		}

		/**
		 * Returns the location to install the distribution ZIP file to.
		 */
		public Path getZipFile()
		{
			return distZip;
		}
	}
}
