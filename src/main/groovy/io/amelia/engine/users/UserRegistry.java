/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.amelia.data.TypeBase;
import io.amelia.engine.EngineCore;
import io.amelia.engine.storage.StorageBus;
import io.amelia.extra.UtilityEncrypt;
import io.amelia.extra.UtilityObjects;
import io.amelia.lang.ReportingLevel;
import io.amelia.support.Streams;

public class UserRegistry
{
	public static final String PATH_USERS = "__users";
	public static final UserCreatorMemory MEMORY;
	public static final ReportingLevel[] reportingLevelSeverityArray = new ReportingLevel[] {ReportingLevel.E_ERROR, ReportingLevel.L_SECURITY, ReportingLevel.L_ERROR, ReportingLevel.L_EXPIRED, ReportingLevel.L_DENIED};
	protected static final Set<UserCreator> userCreators = new CopyOnWriteArraySet<>();
	public static String defaultUserCreator = "memory";
	protected static boolean isDebugEnabled = false;
	protected static volatile Set<UserContext> users = new CopyOnWriteArraySet<>();

	static
	{
		StorageBus.setPath( PATH_USERS, StorageBus.PATH_STORAGE, "users" );

		MEMORY = new UserCreatorMemory();
		addUserCreator( MEMORY );
	}

	public static void addUserCreator( UserCreator userCreator )
	{
		userCreators.add( userCreator );
		userCreator.load();
	}

	public static UserContext createUser( @Nonnull UUID uuid ) throws UserException.Error
	{
		return createUser( uuid, getDefaultUserCreator() );
	}

	public static UserContext createUser( @Nonnull UUID uuid, @Nonnull UserCreator userCreator ) throws UserException.Error
	{
		if ( !userCreator.isEnabled() )
			throw new UserException.Error( null, DescriptiveReason.FEATURE_DISABLED.getReportingLevel(), DescriptiveReason.FEATURE_DISABLED.getReasonMessage() );
		UserContext userContext = userCreator.create( uuid );
		users.add( userContext );
		return userContext;
	}

	public static EntitySubject createVirtualUser( UUID uuid )
	{
		return new UserContext( MEMORY, uuid, false );
	}

	public static String generateUuid()
	{
		String uuid;
		do
			uuid = UtilityEncrypt.uuid();
		while ( userExists( uuid ) );
		return uuid;
	}

	public static UserCreator getDefaultUserCreator()
	{
		return getUserCreator( defaultUserCreator ).orElse( MEMORY );
	}

	public static void setDefaultUserCreator( String defaultUserCreator )
	{
		if ( !hasUserCreator( defaultUserCreator ) )
			throw new UserException.Runtime( "The UserCreator \"" + defaultUserCreator + "\" does not exist!" );
		this.defaultUserCreator = defaultUserCreator;
	}

	public static String getDisplayNameFormat()
	{
		return ConfigRegistry.config.getValue( ConfigKeys.DISPLAY_NAME_FORMAT );
	}

	public static String getSingleSignonMessage()
	{
		return ConfigRegistry.config.getValue( ConfigKeys.SINGLE_SIGNON_MESSAGE );
	}

	public static UserResult getUser( @Nonnull UUID uuid )
	{
		UserResult userResult = new UserResult( uuid );
		getUser( userResult );
		return userResult;
	}

	/**
	 * Resolves a user using the registered user creators.
	 * Never authenticates.
	 */
	public static void getUser( @Nonnull UserResult userResult )
	{
		UUID uuid = userResult.uuid();

		if ( UtilityObjects.anyMatch( uuid, EngineCore.getNullEntity().uuid(), EngineCore.getRootEntity().uuid() ) )
		{
			userResult.setUser( ( UserContext ) ( uuid == EngineCore.getNullEntity().uuid() ? EngineCore.getNullEntity() : EngineCore.getRootEntity() ) );
			userResult.setDescriptiveReason( DescriptiveReason.LOGIN_SUCCESS );
			return;
		}

		Optional<UserContext> foundResult = getUsers().filter( user -> uuid.equals( user.uuid() ) ).findAny();
		if ( foundResult.isPresent() )
		{
			userResult.setUser( foundResult.get() );
			userResult.setDescriptiveReason( DescriptiveReason.LOGIN_SUCCESS );
			return;
		}

		List<UserResult> pendingUserResults = new ArrayList<>();

		for ( UserCreator creator : userCreators )
		{
			userResult = creator.resolve( uuid );

			if ( userResult == null )
				continue;
			if ( userResult.getReportingLevel().isSuccess() )
				return;

			if ( isDebugEnabled )
				L.info( "Failure in creator " + creator.getClass().getSimpleName() + ". {descriptionMessage=" + userResult.getDescriptiveReason().getReasonMessage() + "}" );
			if ( isDebugEnabled && userResult.hasCause() )
				userResult.getCause().printStackTrace();

			pendingUserResults.add( userResult );
		}

		// Sort ReportingLevels based on their position in the reportingLevelSeverityArray.
		pendingUserResults.sort( ( left, right ) -> {
			int leftSeverity = Arrays.binarySearch( reportingLevelSeverityArray, left.getReportingLevel() );
			int rightSeverity = Arrays.binarySearch( reportingLevelSeverityArray, right.getReportingLevel() );
			return Integer.compare( leftSeverity >= 0 ? leftSeverity : Integer.MAX_VALUE, rightSeverity >= 0 ? rightSeverity : Integer.MAX_VALUE );
		} );

		userResult = pendingUserResults.stream().findFirst().orElse( null );

		if ( userResult == null )
		{
			userResult = new UserResult( uuid );
			userResult.setDescriptiveReason( DescriptiveReason.INCORRECT_LOGIN );
			return;
		}

		return;
	}

	public static Optional<UserCreator> getUserCreator( String name )
	{
		return getUserCreators().filter( userCreator -> name.equalsIgnoreCase( userCreator.name() ) ).findAny();
	}

	public static Stream<UserCreator> getUserCreators()
	{
		return userCreators.stream();
	}

	public static Stream<UserContext> getUsers()
	{
		return users.stream();
	}

	static void hasUserCreator( String userCreatorName )
	{
		return userCreators.stream().anyMatch( userCreator -> userCreatorName.equals( userCreator.name() ) );
	}

	public static boolean isDebugEnabled()
	{
		return isDebugEnabled;
	}

	public static void setDebugEnabled( boolean isDebugEnabled )
	{
		this.isDebugEnabled = isDebugEnabled;
	}

	public static boolean isSingleSignonEnabled()
	{
		return ConfigRegistry.config.getValue( ConfigKeys.SINGLE_SIGNON );
	}

	static void put( UserContext userContext ) throws UserException.Error
	{
		if ( UtilityObjects.anyMatch( userContext.uuid(), EngineCore.getNullEntity().uuid(), EngineCore.getRootEntity().uuid() ) )
			throw new UserException.Error( userContext, DescriptiveReason.INTERNAL_ERROR );
		if ( users.stream().anyMatch( user -> user.compareTo( userContext ) == 0 ) )
			return;
		userContext.validate();
		users.add( userContext );
	}

	public static void removeUserContext( UserContext userContext )
	{
		users.remove( userContext );
	}

	static void unload( @Nonnull UUID uuid ) throws UserException.Error
	{
		Streams.forEachWithException( users.stream().filter( user -> uuid.equals( user.uuid() ) ), UserContext::unload );
	}

	static void unload() throws UserException.Error
	{
		// TODO
		Streams.forEachWithException( users.stream(), UserContext::unload );
	}

	public static boolean userExists( @Nonnull String uuid )
	{
		return getUsers().anyMatch( user -> uuid.equals( user.uuid() ) );
	}

	public static class ConfigKeys
	{
		public static final TypeBase USERS_BASE = new TypeBase( "users" );
		public static final TypeBase.TypeInteger MAX_LOGINS = new TypeBase.TypeInteger( USERS_BASE, "maxLogins", -1 );
		public static final TypeBase CREATORS = new TypeBase( USERS_BASE, "userCreators" );
		public static final TypeBase.TypeString DISPLAY_NAME_FORMAT = new TypeBase.TypeString( USERS_BASE, "displayNameFormat", "${fname} ${name}" );
		public static final TypeBase.TypeBoolean DEBUG_ENABLED = new TypeBase.TypeBoolean( USERS_BASE, "debugEnabled", false );
		public static final TypeBase.TypeBoolean SINGLE_SIGNON = new TypeBase.TypeBoolean( USERS_BASE, "singleSignon", false );
		public static final TypeBase.TypeString SINGLE_SIGNON_MESSAGE = new TypeBase.TypeString( USERS_BASE, "singleSignonMessage", "You logged in from another location." );
		public static final TypeBase SESSIONS_BASE = new TypeBase( USERS_BASE, "sessions" );
		public static final TypeBase.TypeInteger SESSIONS_CLEANUP_INTERVAL = new TypeBase.TypeInteger( SESSIONS_BASE, "cleanupInterval", 5 );

		private ConfigKeys()
		{
			// Static Access
		}
	}
}
