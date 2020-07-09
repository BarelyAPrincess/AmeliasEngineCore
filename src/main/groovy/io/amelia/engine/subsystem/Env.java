/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.subsystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.amelia.lang.UncaughtException;
import io.amelia.extra.UtilityIO;
import io.amelia.extra.UtilityObjects;
import io.amelia.support.Pair;
import io.amelia.support.VoluntaryBoolean;

public class Env
{
	private final Map<String, Object> env = new HashMap<>();
	private final Path envFile;

	public Env( @Nonnull Path envFile ) throws IOException
	{
		this.envFile = envFile;

		synchronized ( env )
		{
			Properties prop = new Properties();
			if ( Files.isRegularFile( envFile ) )
				prop.load( Files.newInputStream( envFile ) );

			for ( String key : prop.stringPropertyNames() )
				env.put( key, prop.getProperty( key ) );
		}
	}

	public <T> T computeValue( String key, Supplier<T> valueSupplier, boolean updateEnvFile )
	{
		T value = computeValue( key, valueSupplier );

		if ( updateEnvFile )
			updateEnvFile( key, value );

		return value;
	}

	public <T> T computeValue( String key, Supplier<T> valueSupplier )
	{
		return ( T ) env.computeIfAbsent( key, k -> valueSupplier.get() );
	}

	public VoluntaryBoolean getBoolean( String key )
	{
		return UtilityObjects.isTrue( getObject( key ) );
	}

	public Optional<Object> getObject( String key )
	{
		return Optional.ofNullable( env.get( key ) );
	}

	public Stream<Object> getStream()
	{
		return env.values().stream();
	}

	public Stream<Pair<String, Object>> getStreamMap()
	{
		return env.entrySet().stream().map( e -> new Pair<>( e.getKey(), e.getValue() ) );
	}

	public Optional<String> getString( String key )
	{
		return getObject( key ).map( UtilityObjects::castToStringWithException );
	}

	public Stream<String> getStrings()
	{
		return env.values().stream().filter( v -> v instanceof String ).map( v -> ( String ) v );
	}

	public Stream<Pair<String, String>> getStringsMap()
	{
		return env.entrySet().stream().filter( e -> e.getValue() instanceof String ).map( e -> new Pair<>( e.getKey(), ( String ) e.getValue() ) );
	}

	public boolean hasKey( String key )
	{
		return env.containsKey( key );
	}

	public boolean isValueSet( String key )
	{
		return env.containsKey( key ) && !UtilityObjects.isNull( env.get( key ) );
	}

	public Map<String, Object> map()
	{
		return Collections.unmodifiableMap( env );
	}

	public Env set( @Nonnull String key, @Nonnull Object value, boolean updateEnvFile )
	{
		env.put( key, value );

		if ( updateEnvFile )
			updateEnvFile( key, value );

		return this;
	}

	private void updateEnvFile( @Nonnull String key, @Nonnull Object value )
	{
		try
		{
			Properties prop = new Properties();
			if ( Files.isRegularFile( envFile ) )
				prop.load( Files.newInputStream( envFile ) );
			prop.setProperty( key, UtilityObjects.castToString( value ) );
			prop.store( Files.newOutputStream( envFile ), "" );
		}
		catch ( IOException e )
		{
			if ( e instanceof FileNotFoundException && e.getMessage().contains( "Permission denied" ) )
				throw new UncaughtException( "We attempted to save the .env file and ran into a permissions issue for directory \"" + UtilityIO.relPath( envFile ) + "\"", e );
			else
				throw new UncaughtException( e );
		}
	}
}
