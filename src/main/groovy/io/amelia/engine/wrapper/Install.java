package io.amelia.engine.wrapper;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.security.MessageDigest;
import java.util.zip.ZipInputStream;

import io.amelia.data.parcel.Parcel;
import io.amelia.engine.log.L;
import io.amelia.extra.UtilityIO;
import io.amelia.support.Sys;

public class Install
{
	public static final String DEFAULT_DISTRIBUTION_PATH = "wrapper/dists";
	private final IDownload download;
	private final ExclusiveFileAccessManager exclusiveFileAccessManager = new ExclusiveFileAccessManager( 120000, 200 );
	private final PathAssembler pathAssembler;

	public Install( IDownload download, PathAssembler pathAssembler )
	{
		this.download = download;
		this.pathAssembler = pathAssembler;
	}

	private String calculateSha256Sum( Path file ) throws Exception
	{
		MessageDigest md = MessageDigest.getInstance( "SHA-256" );
		InputStream fis = Files.newInputStream( file );
		try
		{
			int n = 0;
			byte[] buffer = new byte[4096];
			while ( n != -1 )
			{
				n = fis.read( buffer );
				if ( n > 0 )
				{
					md.update( buffer, 0, n );
				}
			}
		}
		finally
		{
			fis.close();
		}

		byte byteData[] = md.digest();

		StringBuffer hexString = new StringBuffer();
		for ( int i = 0; i < byteData.length; i++ )
		{
			String hex = Integer.toHexString( 0xff & byteData[i] );
			if ( hex.length() == 1 )
			{
				hexString.append( '0' );
			}
			hexString.append( hex );
		}

		return hexString.toString();
	}

	private void copyInputStream( InputStream in, OutputStream out ) throws IOException
	{
		byte[] buffer = new byte[1024];
		int len;

		while ( ( len = in.read( buffer ) ) >= 0 )
		{
			out.write( buffer, 0, len );
		}

		in.close();
		out.close();
	}

	public Path createDist( final Parcel configuration ) throws Exception
	{
		final URI distributionUrl = configuration.getValue( WrapperExecutor.DISTRIBUTION_URL_PROPERTY ).map( o -> ( URI ) o ).orElse( null );
		final String distributionSha256Sum = configuration.getString( WrapperExecutor.DISTRIBUTION_SHA_256_SUM ).orElse( null );

		final PathAssembler.LocalDistribution localDistribution = pathAssembler.getDistribution( configuration );
		final Path distDir = localDistribution.getDistributionDir();
		final Path localZipFile = localDistribution.getZipFile();

		return exclusiveFileAccessManager.access( localZipFile, new Callable<Path>()
		{
			public Path call() throws Exception
			{
				final Path markerFile = localZipFile.getParent().resolve( localZipFile.getFileName() + ".ok" );
				if ( Files.isDirectory( distDir ) && Files.isRegularFile( markerFile ) )
				{
					InstallCheck installCheck = verifyDistributionRoot( distDir, UtilityIO.relPath( distDir ) );
					if ( installCheck.isVerified() )
					{
						return installCheck.engineHome;
					}
					// Distribution is invalid. Try to reinstall.
					System.err.println( installCheck.failureMessage );
					Files.delete( markerFile );
				}

				boolean needsDownload = !Files.isRegularFile( localZipFile );
				URI safeDistributionUrl = Download.safeUri( distributionUrl );

				if ( needsDownload )
				{
					Path tmpZipFile = localZipFile.getParent().resolve( localZipFile.getFileName() + ".part" );
					Files.delete( tmpZipFile );
					L.info( "Downloading " + safeDistributionUrl );
					download.download( distributionUrl, tmpZipFile );
					Files.move( tmpZipFile, localZipFile );
				}

				List<Path> topLevelDirs = listDirs( distDir );
				for ( Path dir : topLevelDirs )
				{
					L.info( "Deleting directory " + UtilityIO.relPath( dir ) );
					UtilityIO.deleteIfExists( dir );
				}

				verifyDownloadChecksum( configuration.getValue( WrapperExecutor.DISTRIBUTION_URL_PROPERTY ).map( o -> ( ( URI ) o ).toString() ).orElse( null ), localZipFile, distributionSha256Sum );

				try
				{
					unzip( localZipFile, distDir );
				}
				catch ( IOException e )
				{
					L.severe( "Could not unzip " + UtilityIO.relPath( localZipFile ) + " to " + UtilityIO.relPath( distDir ) + "." );
					L.severe( "Reason: " + e.getMessage() );
					throw e;
				}

				InstallCheck installCheck = verifyDistributionRoot( distDir, safeDistributionUrl.toString() );
				if ( installCheck.isVerified() )
				{
					setExecutablePermissions( installCheck.engineHome );
					Files.createFile( markerFile );
					return installCheck.engineHome;
				}
				// Distribution couldn't be installed.
				throw new RuntimeException( installCheck.failureMessage );
			}
		} );
	}

	private List<Path> listDirs( Path distDir )
	{
		List<Path> dirs = new ArrayList<Path>();
		if ( Files.exists( distDir ) )
			try
			{
				Files.list( distDir ).filter( Files::isDirectory ).forEach( dirs::add );
			}
			catch ( IOException e )
			{
				// ignore
			}
		return dirs;
	}

	private void setExecutablePermissions( Path engineHome )
	{
		if ( Sys.isWindows() )
			return;

		Path engineCommand = engineHome.resolve( "bin/engine" );
		String errorMessage = null;
		try
		{
			UtilityIO.setPermissionsSymbolic( engineCommand, "a=rx o=rwx" ); // 755
		}
		catch ( IOException e )
		{
			L.info( "Could not set executable permissions for: " + engineCommand.toAbsolutePath() );
		}
	}

	private void unzip( Path zip, Path dest ) throws IOException
	{
		ZipInputStream zipInputStream = null;
		try
		{
			zipInputStream = new ZipInputStream( Files.newInputStream( zip ) );
			ZipEntry entry;
			while ( ( entry = zipInputStream.getNextEntry() ) != null )
			{
				if ( entry.isDirectory() )
				{
					Files.createDirectories( dest.resolve( entry.getName() ) );
					continue;
				}

				OutputStream outputStream = new BufferedOutputStream( Files.newOutputStream( dest.resolve( entry.getName() ) ) );
				try
				{
					copyInputStream( zipInputStream, outputStream );
				}
				finally
				{
					outputStream.close();
				}
			}
		}
		finally
		{
			UtilityIO.closeQuietly( zipInputStream );
		}
	}

	private InstallCheck verifyDistributionRoot( Path distDir, String distributionDescription ) throws Exception
	{
		List<Path> dirs = listDirs( distDir );
		if ( dirs.isEmpty() )
		{
			return InstallCheck.failure( String.format( "Engine distribution '%s' does not contain any directories. Expected to find exactly 1 directory.", distributionDescription ) );
		}
		if ( dirs.size() != 1 )
		{
			return InstallCheck.failure( String.format( "Engine distribution '%s' contains too many directories. Expected to find exactly 1 directory.", distributionDescription ) );
		}

		Path engineHome = dirs.get( 0 );
		if ( BootstrapMainStarter.findLauncherJar( engineHome ) == null )
		{
			return InstallCheck.failure( String.format( "Engine distribution '%s' does not appear to contain a Engine distribution.", distributionDescription ) );
		}
		return InstallCheck.success( engineHome );
	}

	private void verifyDownloadChecksum( String sourceUrl, Path localZipFile, String expectedSum ) throws Exception
	{
		if ( expectedSum != null )
		{
			// if a SHA-256 hash sum has been defined in engine-wrapper.properties, verify it here
			String actualSum = calculateSha256Sum( localZipFile );
			if ( !expectedSum.equals( actualSum ) )
			{
				Files.delete( localZipFile );
				String message = String.format( "Verification of Engine distribution failed!%n" + "%n" + "Your Engine distribution may have been tampered with.%n" + "Confirm that the 'distributionSha256Sum' property in your engine-wrapper.properties file is correct and you are downloading the wrapper from a trusted source.%n" + "%n" + " Distribution Url: %s%n" + "Download Location: %s%n" + "Expected checksum: '%s'%n" + "  Actual checksum: '%s'%n", sourceUrl, localZipFile.toAbsolutePath(), expectedSum, actualSum );
				throw new RuntimeException( message );
			}
		}
	}

	private static class InstallCheck
	{
		private static InstallCheck failure( String message )
		{
			return new InstallCheck( null, message );
		}

		private static InstallCheck success( Path engineHome )
		{
			return new InstallCheck( engineHome, null );
		}
		private final Path engineHome;
		private final String failureMessage;

		private InstallCheck( Path engineHome, String failureMessage )
		{
			this.engineHome = engineHome;
			this.failureMessage = failureMessage;
		}

		private boolean isVerified()
		{
			return engineHome != null;
		}
	}

}
