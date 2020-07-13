package io.amelia.engine.wrapper;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

import io.amelia.extra.UtilityIO;

public class ExclusiveFileAccessManager
{
	public static final String LOCK_FILE_SUFFIX = ".lck";

	private final int pollIntervalMs;
	private final int timeoutMs;

	public ExclusiveFileAccessManager( int timeoutMs, int pollIntervalMs )
	{
		this.timeoutMs = timeoutMs;
		this.pollIntervalMs = pollIntervalMs;
	}

	public <T> T access( Path exclusiveFile, Callable<T> task ) throws Exception
	{
		final Path lockFile = exclusiveFile.getParent().resolve( exclusiveFile.getFileName() + LOCK_FILE_SUFFIX );
		Path lockFileDirectory = lockFile.getParent();
		try
		{
			Files.createDirectories( lockFileDirectory );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( "Could not create parent directory for lock file " + UtilityIO.relPath( lockFile ) );
		}

		FileChannel channel = null;
		try
		{

			long expiry = getTimeMillis() + timeoutMs;
			FileLock lock = null;

			while ( lock == null && getTimeMillis() < expiry )
			{
				channel = FileChannel.open( lockFile, StandardOpenOption.READ, StandardOpenOption.WRITE );
				lock = channel.tryLock();

				if ( lock == null )
				{
					UtilityIO.closeQuietly( channel );
					Thread.sleep( pollIntervalMs );
				}
			}

			if ( lock == null )
			{
				throw new RuntimeException( "Timeout of " + timeoutMs + " reached waiting for exclusive access to file: " + UtilityIO.relPath( exclusiveFile ) );
			}

			try
			{
				return task.call();
			}
			finally
			{
				lock.release();

				UtilityIO.closeQuietly( channel );
				channel = null;
			}
		}
		finally
		{
			UtilityIO.closeQuietly( channel );
		}
	}

	private long getTimeMillis()
	{
		return System.nanoTime() / ( 1000L * 1000L );
	}
}