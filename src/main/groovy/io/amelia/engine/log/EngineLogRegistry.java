package io.amelia.engine.log;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import io.amelia.engine.storage.StorageBus;
import io.amelia.extra.UtilityIO;
import io.amelia.support.Streams;

public class EngineLogRegistry
{
	public static final SimpleDateFormat DEFAULT_TIMESTAMP = new SimpleDateFormat( "HH:mm:ss.SSS" );
	public static final Logger ROOT = Logger.getLogger( "" );
	public static final String GLOBAL_LOGGER_NAMESPACE = "io.amelia.engine";
	public static final EngineLogger GLOBAL;
	public static final String UNKNOWN_LOGGER_NAMESPACE = "";
	public static final EngineLogger UNKNOWN;
	public static final EngineLogger logger;
	public static final PrintStream FAILOVER_OUTPUT_STREAM = new PrintStream( new FileOutputStream( FileDescriptor.out ) );
	private static final ConsoleHandler consoleHandler = new ConsoleHandler();

	static
	{
		logger = EngineLogger.root();

		GLOBAL = logger.getChild( GLOBAL_LOGGER_NAMESPACE );
		UNKNOWN = logger.getChild( UNKNOWN_LOGGER_NAMESPACE );

		consoleHandler.setFormatter( new SimpleLogFormatter() );
		GLOBAL.subscribeHandler( consoleHandler );

		System.setOut( new PrintStream( new LoggerOutputStream( logger, Level.INFO ), true ) );
		System.setErr( new PrintStream( new LoggerOutputStream( logger, Level.SEVERE ), true ) );

		try
		{
			UtilityIO.forceCreateDirectory( StorageBus.getPath( StorageBus.PATH_LOGS ) );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	public static void addFileHandler( String filename, boolean useColor, int archiveLimit, Level level )
	{
		Path logPath = StorageBus.getPath( StorageBus.PATH_LOGS ).resolve( filename + ".log" );
		try
		{
			if ( Files.exists( logPath ) )
			{
				if ( archiveLimit > 0 )
					UtilityIO.gzFile( logPath, StorageBus.getPath( StorageBus.PATH_LOGS ).resolve( new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" ).format( new Date() ) + "-" + filename + ".log.gz" ) );
				Files.delete( logPath );
			}

			cleanupLogs( "-" + filename + ".log.gz", archiveLimit );

			FileHandler fileHandler = new FileHandler( logPath.toString() );
			fileHandler.setLevel( level );
			fileHandler.setFormatter( new DefaultLogFormatter( useColor ) );

			GLOBAL.subscribeHandler( fileHandler );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			GLOBAL.severe( "Failed to log to \"" + logPath.toString() + "\" for reason \"" + e.getMessage() + "\".", e );
		}
	}

	private static void cleanupLogs( final String suffix, int limit ) throws IOException
	{
		Stream<Path> result = Files.list( StorageBus.getPath( StorageBus.PATH_LOGS ) ).filter( path -> path.toString().toLowerCase().endsWith( suffix.toLowerCase() ) );

		// Delete all logs, no archiving!
		if ( limit < 1 )
			Streams.forEachWithException( result, UtilityIO::deleteIfExists );
		else
			Streams.forEachWithException( result.sorted( new UtilityIO.PathComparatorByCreated() ).limit( limit ), UtilityIO::deleteIfExists );
	}

	public static void setConsoleFormatter( Formatter formatter )
	{
		consoleHandler.setFormatter( formatter );
	}

	/**
	 * Checks if the currently set Log Formatter, supports colored logs.
	 *
	 * @return true if it does
	 */
	public static boolean useColor()
	{
		return consoleHandler.getFormatter() instanceof DefaultLogFormatter && ( ( DefaultLogFormatter ) consoleHandler.getFormatter() ).useColor();
	}

	private EngineLogRegistry()
	{
		// Static Class
	}
}
