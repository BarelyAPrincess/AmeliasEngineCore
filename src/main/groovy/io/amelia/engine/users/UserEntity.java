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


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.amelia.data.parcel.ParcelCarrier;
import io.amelia.data.parcel.ParcelReceiver;
import io.amelia.data.parcel.ParcelSender;
import io.amelia.engine.permissions.PermissibleEntity;
import io.amelia.engine.users.auth.UserCredentials;
import io.amelia.lang.ParcelException;
import io.amelia.support.Voluntary;

public class UserEntity implements EntitySubject, ParcelSender, ParcelReceiver
{
	/**
	 * Tracks permissibles that are referencing this account
	 */
	private final Set<UserAttachment> permissibles = Collections.newSetFromMap( new WeakHashMap<>() );
	private final UserContext userContext;
	private UserCredentials lastUsedCredentials = null;

	UserEntity( @Nonnull UserContext userContext )
	{
		this.userContext = userContext;
	}

	int countAttachments()
	{
		return permissibles.size();
	}

	public Stream<UserAttachment> getAttachments()
	{
		return permissibles.stream();
	}

	@Override
	public UserContext getContext()
	{
		return userContext;
	}

	@Override
	public UserEntity getEntity()
	{
		return this;
	}

	public Stream<String> getIpAddresses()
	{
		return getAttachments().map( UserAttachment::getIpAddress );
	}

	public UserCredentials getLastUsedCredentials()
	{
		return lastUsedCredentials;
	}

	public void setLastUsedCredentials( UserCredentials lastUsedCredentials )
	{
		this.lastUsedCredentials = lastUsedCredentials;
	}

	@Override
	public PermissibleEntity getPermissibleEntity()
	{
		return getContext().getPermissibleEntity();
	}

	@Nullable
	@Override
	public ParcelReceiver getReplyTo()
	{
		return this;
	}

	/**
	 * Get a string from the Metadata with a default value
	 *
	 * @param key Metadata key.
	 *
	 * @return String
	 */
	public Voluntary<String> getString( String key )
	{
		return userContext.getString( key );
	}

	@Override
	public void handleParcel( ParcelCarrier parcelCarrier ) throws ParcelException.Error
	{
		// TODO
	}

	@Override
	public String name()
	{
		return userContext.name();
	}

	public void registerAttachment( UserAttachment attachment )
	{
		if ( !permissibles.contains( attachment ) )
			permissibles.add( attachment );
	}

	@Override
	public String toString()
	{
		return "User{" + userContext.toString() + ",Attachments{" + getAttachments().map( Objects::toString ).collect( Collectors.joining( "," ) ) + "}}";
	}

	public void unregisterAttachment( UserAttachment attachment )
	{
		permissibles.remove( attachment );
	}

	@Nonnull
	@Override
	public UUID uuid()
	{
		return userContext.uuid();
	}
}
