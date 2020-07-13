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

import java.util.UUID;

import io.amelia.data.parcel.Parcel;
import io.amelia.engine.permissions.PermissibleEntity;
import io.amelia.extra.UtilityParcels;

/**
 * Provides context to a user without actually existing within the users implementation.
 * These classes are a dime-a-dozen and are not unique. The purpose is to carry parcel data from the UserCreator to the users implementation.
 */
public class UserOffline implements EntitySubject
{
	final Parcel parcel = Parcel.empty();
	private final UserCreator userCreator;
	private final UUID uuid;
	private UserContext userContext;

	public UserOffline( UserCreator userCreator, UUID uuid )
	{
		this.uuid = uuid;
		this.userCreator = userCreator;
	}

	public UserContext getContext( boolean create )
	{
		UserContext userContext = UserRegistry.getUsers().filter( user -> uuid.equals( user.uuid() ) ).findAny().orElse( null );
		if ( create )
			try
			{
				userContext = new UserContext( this );
				UserRegistry.put( userContext );
			}
			catch ( UserException.Error error )
			{
				// Ignore
			}
		return userContext;
	}

	@Override
	public UserContext getContext()
	{
		return getContext( true );
	}

	@Override
	public UserEntity getEntity()
	{
		return getContext().getEntity();
	}

	@Override
	public PermissibleEntity getPermissibleEntity()
	{
		return getContext().getPermissibleEntity();
	}

	public UserCreator getUserCreator()
	{
		return userCreator;
	}

	@Override
	public String name()
	{
		return UtilityParcels.parseFormatString( parcel, UserRegistry.getDisplayNameFormat() ).orElse( "{error}" );
	}

	@Override
	public UUID uuid()
	{
		return uuid;
	}
}
