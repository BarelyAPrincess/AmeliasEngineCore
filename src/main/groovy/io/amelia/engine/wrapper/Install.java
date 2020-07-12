package io.amelia.engine.wrapper;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.security.MessageDigest;
import java.util.zip.ZipInputStream;

import io.amelia.data.parcel.Parcel;
import io.amelia.engine.subsystem.log.L;
import io.amelia.extra.UtilityIO;

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
					InstallCheck installCheck = verifyDistributionRoot( distDir, distDir.toAbsolutePath() );
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
					L.info( "Deleting directory " + dir.getAbsolutePath() );
					deleteDir( dir );
				}

				verifyDownloadChecksum( configuration.getValue( WrapperExecutor.DISTRIBUTION_URL_PROPERTY ).map( o -> ( ( URI ) o ).toString() ).orElse( null ), localZipFile, distributionSha256Sum );

				try
				{
					unzip( localZipFile, distDir );
				}
				catch ( IOException e )
				{
					L.severe( "Could not unzip " + localZipFile.getAbsolutePath() + " to " + distDir.getAbsolutePath() + "." );
					L.severe( "Reason: " + e.getMessage() );
					throw e;
				}

				InstallCheck installCheck = verifyDistributionRoot( distDir, safeDistributionUrl.toString() );
				if ( installCheck.isVerified() )
				{
					setExecutablePermissions( installCheck.engineHome );
					markerFile.createNewFile();
					return installCheck.engineHome;
				}
				// Distribution couldn't be installed.
				throw new RuntimeException( installCheck.failureMessage );
			}
		} );
	}

	private boolean deleteDir( Path dir )
	{
		if ( dir.isDirectory() )
		{
			String[] children = dir.list();
			for ( int i = 0; i < children.length; i++ )
			{
				boolean success = deleteDir( new File( dir, children[i] ) );
				if ( !success )
				{
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	private boolean isWindows()
	{
		String osName = System.getProperty( "os.name" ).toLowerCase( Locale.US );
		if ( osName.indexOf( "windows" ) > -1 )
		{
			return true;
		}
		return false;
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
		if ( isWindows() )
		{
			return;
		}
		Path engineCommand = engineHome.resolve( "bin/engine" );
		String errorMessage = null;
		try
		{
			UtilityIO.setGroupReadWritePermissions( engineCommand );

			Set<PosixFilePermission> perms = new HashSet<>();
			//add owners permission
			perms.add( PosixFilePermission.OWNER_READ );
			perms.add( PosixFilePermission.OWNER_WRITE );
			perms.add( PosixFilePermission.OWNER_EXECUTE );
			//add group permissions
			perms.add( PosixFilePermission.GROUP_READ );
			perms.add( PosixFilePermission.GROUP_WRITE );
			perms.add( PosixFilePermission.GROUP_EXECUTE );
			//add others permissions
			perms.add( PosixFilePermission.OTHERS_READ );
			perms.add( PosixFilePermission.OTHERS_WRITE );
			perms.add( PosixFilePermission.OTHERS_EXECUTE );

			Files.setPosixFilePermissions( engineCommand, perms );



			Set<PosixFilePermission> perms = Files.getPosixFilePermissions( engineCommand );

			ProcessBuilder pb = new ProcessBuilder( "chmod", "755", engineCommand.toAbsolutePath().toString() );
			Process p = pb.start();
			if ( p.waitFor() != 0 )
			{
				BufferedReader is = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
				Formatter stdout = new Formatter();
				String line;
				while ( ( line = is.readLine() ) != null )
				{
					stdout.format( "%s%n", line );
				}
				errorMessage = stdout.toString();
			}
		}
		catch ( IOException e )
		{
			errorMessage = e.getMessage();
		}
		catch ( InterruptedException e )
		{
			Thread.currentThread().interrupt();
			errorMessage = e.getMessage();
		}
		if ( errorMessage != null )
			L.info( "Could not set executable permissions for: " + engineCommand.toAbsolutePath() );
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

		private final String failureMessage;
		private final Path engineHome;

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
