package io.amelia.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import io.amelia.data.ContainerBase;
import io.amelia.data.parcel.ParcelCarrier;
import io.amelia.data.parcel.ParcelInterface;
import io.amelia.data.parcel.ParcelReceiver;
import io.amelia.engine.config.ConfigData;
import io.amelia.engine.config.ConfigRegistry;
import io.amelia.engine.log.L;
import io.amelia.engine.looper.LooperRouter;
import io.amelia.engine.storage.StorageBus;
import io.amelia.extra.UtilityEncrypt;
import io.amelia.extra.UtilityObjects;
import io.amelia.extra.UtilityStrings;
import io.amelia.lang.ApplicationException;
import io.amelia.lang.ExceptionRegistrar;
import io.amelia.lang.ExceptionReport;
import io.amelia.lang.ParcelException;
import io.amelia.lang.ReportingLevel;
import io.amelia.support.Env;
import io.amelia.support.EnumColor;
import io.amelia.support.Runlevel;
import io.amelia.support.Sys;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public final class EngineApplication implements ParcelReceiver, ParcelInterface, ExceptionRegistrar
{
	static
	{
		System.setProperty( "file.encoding", "utf-8" );
	}

	public final Thread primaryThread = Thread.currentThread();
	private final OptionParser optionParser = new OptionParser();
	private Env env = null;
	private OptionSet optionSet = null;

	public EngineApplication()
	{
		if ( EngineCore.isDevelopment() )
			L.info( "%s%sApp is running in development mode.", EnumColor.DARK_RED, EnumColor.NEGATIVE );

		optionParser.acceptsAll( Arrays.asList( "?", "h", "help" ), "Show the help" );
		optionParser.acceptsAll( Arrays.asList( "v", "version" ), "Show the version" );

		optionParser.accepts( "env-file", "The env file" ).withRequiredArg().ofType( String.class ).defaultsTo( ".env" );
		optionParser.accepts( "env", "Override env values" ).withRequiredArg().ofType( String.class );
		optionParser.accepts( "no-banner", "Disables the banner" );

		for ( String pathKey : StorageBus.getPathSlugs() )
			optionParser.accepts( "dir-" + pathKey, "Sets the " + pathKey + " directory path." ).withRequiredArg().ofType( String.class );

		// CommandDispatch.handleCommands();
	}

	public void addArgument( String arg, String desc )
	{
		optionParser.accepts( arg, desc );
	}

	public void addIntegerArgument( String arg, String desc )
	{
		optionParser.accepts( arg, desc ).withRequiredArg().ofType( Integer.class );
	}

	public void addStringArgument( String arg, String desc )
	{
		optionParser.accepts( arg, desc ).withRequiredArg().ofType( String.class );
	}

	public void checkOptionSet()
	{
		if ( optionSet == null )
			throw new ApplicationException.Runtime( ReportingLevel.E_ERROR, "Method parse( String[] ) was never called." );
	}

	void dispose()
	{
		LooperRouter.dispose();
	}

	@Override
	public void fatalError( ExceptionReport report, boolean crashOnError )
	{
		if ( !UtilityObjects.stackTraceAntiLoop( getClass(), "fatalError" ) )
			return;
		if ( report.hasErrored() && crashOnError )
			EngineCore.setRunlevel( Runlevel.CRASHED );
	}

	public Env getEnv()
	{
		checkOptionSet();
		return env;
	}

	public Optional<Integer> getIntegerArgument( String arg )
	{
		return Optional.ofNullable( optionSet.valuesOf( arg ) ).filter( l -> l.size() > 0 ).map( l -> ( Integer ) l.get( 0 ) );
	}

	public OptionParser getOptionParser()
	{
		return optionParser;
	}

	public OptionSet getOptionSet()
	{
		checkOptionSet();
		return optionSet;
	}

	public Optional<String> getStringArgument( String arg )
	{
		return Optional.ofNullable( optionSet.valuesOf( arg ) ).filter( l -> l.size() > 0 ).map( l -> ( String ) l.get( 0 ) );
	}

	public Optional<List<String>> getStringListArgument( String arg )
	{
		return Optional.ofNullable( ( List<String> ) optionSet.valuesOf( arg ) );
	}

	@Override
	public void handleParcel( ParcelCarrier parcelCarrier ) throws ParcelException.Error
	{

	}

	public boolean hasArgument( String arg )
	{
		return optionSet.hasArgument( arg );
	}

	public boolean isPrimaryThread()
	{
		return primaryThread == Thread.currentThread();
	}

	@Override
	public final boolean isRemote()
	{
		return false;
	}

	public void onRunlevelChange( Runlevel previousRunlevel, Runlevel currentRunlevel ) throws ApplicationException.Error
	{
		//if ( currentRunlevel == Runlevel.MAINLOOP )
		// LooperRouter.getMainLooper().postTaskRepeatingLater( entry -> Tasks.heartbeat( LooperRouter.getMainLooper().getLastPolledMillis() ), 50L, 50L );
		if ( currentRunlevel == Runlevel.SHUTDOWN )
		{
			L.info( "Saving Configuration..." );
			ConfigRegistry.save();

			try
			{
				L.info( "Clearing Excess Cache..." );
				long keepHistory = ConfigRegistry.config.getLong( "advanced.execute.keepHistory" ).orElse( 30L );
				ConfigRegistry.clearCache( keepHistory );
			}
			catch ( IllegalArgumentException e )
			{
				L.warning( "Cache directory is invalid!" );
			}
		}
	}

	/**
	 * Handles internal argument options and triggers, such as
	 *
	 * @throws ApplicationException.StartupInterrupt
	 */
	public final void parse( String[] args ) throws ApplicationException.StartupInterrupt
	{
		optionSet = optionParser.parse( args );

		if ( optionSet.has( "help" ) )
		{
			try
			{
				optionParser.printHelpOn( System.out );
			}
			catch ( IOException e )
			{
				throw new ApplicationException.Startup( e );
			}
			throw new ApplicationException.StartupInterrupt();
		}

		if ( optionSet.has( "version" ) )
		{
			L.info( EngineCore.getDeveloperMeta().getProductDescribed() );
			throw new ApplicationException.StartupInterrupt();
		}

		try
		{
			/* Load env file -- Can be set with arg `--env-file=.env` */
			Path envFile = Paths.get( ( String ) optionSet.valueOf( "env-file" ) );
			env = new Env( envFile );

			/* Override defaults and env with command args */
			for ( OptionSpec<?> optionSpec : optionSet.specs() )
				for ( String optionKey : optionSpec.options() )
					if ( !UtilityObjects.isNull( optionSpec.value( optionSet ) ) )
					{
						if ( optionKey.startsWith( "dir-" ) )
							StorageBus.setPath( optionKey.substring( 4 ), ( String ) optionSpec.value( optionSet ) );
						else if ( env.isValueSet( optionKey ) )
							env.set( optionKey, optionSpec.value( optionSet ), false );
					}

			// XXX Use UtilityEncrypt::hash as an alternative to UtilityEncrypt::uuid
			env.computeValue( "instance-id", UtilityEncrypt::uuid, true );

			StorageBus.setAppPath( env.getString( "app-dir" ).map( Paths::get ).orElse( Sys.getAppPath().orElseThrow( () -> new ApplicationException.Startup( "The app path is not specified nor could we resolve it." ) ) ) );
			env.getStringsMap().filter( e -> e.getKey().endsWith( "-dir" ) ).forEach( e -> StorageBus.setPath( e.getKey().substring( 0, e.getKey().length() - 4 ), UtilityStrings.split( e.getValue(), "/" ).toArray( String[]::new ) ) );

			ConfigRegistry.config.setEnvironmentVariables( env.map() );

			ConfigData envNode = ConfigRegistry.config.getChildOrCreate( "env" );
			for ( Map.Entry<String, Object> entry : env.map().entrySet() )
				envNode.setValue( entry.getKey().replace( '-', '_' ), entry.getValue() );
			envNode.addFlag( ContainerBase.Flags.READ_ONLY, ContainerBase.Flags.NO_SAVE );

			StorageBus.init();

			// ConfigRegistry should load here!
			// Foundation.invokeHook( Foundation.class, Foundation.HOOK_ACTION_PARSE );
			if ( !ConfigRegistry.isLoaded() )
				throw new ApplicationException.Error( "ConfigRegistry did not initialize as expected. Is the `HOOK_ACTION_PARSE` implemented to load data into the ConfigRegistry?" );

			parse();
		}
		catch ( ApplicationException.Startup e )
		{
			throw e;
		}
		catch ( Exception e )
		{
			throw new ApplicationException.Startup( e );
		}
	}

	/**
	 * Called to perform some additional tasks during the parse phase of loading.
	 *
	 * @throws Exception
	 */
	protected void parse() throws Exception
	{

	}

	void quitSafely()
	{
		LooperRouter.quitSafely();
	}

	void quitUnsafe()
	{
		LooperRouter.quitUnsafely();
	}

	@Override
	public void sendToAll( ParcelCarrier parcel )
	{

	}

	@Nonnull
	public UUID uuid()
	{
		return env.getString( "uuid" ).map( UUID::fromString ).orElseGet( UUID::randomUUID );
	}
}
