package io.amelia.engine.users;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.amelia.data.parcel.ParcelCarrier;
import io.amelia.engine.permissions.PermissibleEntity;
import io.amelia.extra.UtilityObjects;
import io.amelia.lang.ParcelException;

public final class UserInstance implements User
{
	/**
	 * User MetaData
	 */
	private final UserMeta metadata;
	/**
	 * Tracks permissibles that are referencing this account
	 */
	private final Set<UserAttachment> permissibles = Collections.newSetFromMap( new WeakHashMap<UserAttachment, Boolean>() );

	UserInstance( @Nonnull UserMeta metadata )
	{
		UtilityObjects.notNull( metadata );
		this.metadata = metadata;
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
	public String getDisplayName()
	{
		return metadata.getDisplayName();
	}

	@Override
	public String getId()
	{
		return metadata.getId();
	}

	public Collection<String> getIpAddresses()
	{
		Set<String> ips = new HashSet<>();
		getAttachments().map( perm -> perm.getIpAddress() ).forEach( ips::add );
		return ips;
	}

	@Override
	public PermissibleEntity getPermissibleEntity()
	{
		return meta().getPermissibleEntity();
	}

	/**
	 * @param key Metadata key.
	 *
	 * @return String
	 * Returns an empty string if no result.
	 */
	public String getString( String key )
	{
		return getString( key, "" );
	}

	/**
	 * Get a string from the Metadata with a default value
	 *
	 * @param key Metadata key.
	 * @param def Default value to return if no result.
	 *
	 * @return String
	 */
	public String getString( String key, String def )
	{
		if ( !metadata.containsKey( key ) )
			return def;

		return metadata.getString( key );
	}

	@Override
	public void handleParcel( ParcelCarrier parcelCarrier ) throws ParcelException.Error
	{
		// TODO Implement
	}

	@Override
	public UserInstance i()
	{
		return this;
	}

	@Override
	public boolean isInitialized()
	{
		return true;
	}

	@Override
	public UserMeta meta()
	{
		return metadata;
	}

	public void registerAttachment( UserAttachment attachment )
	{
		if ( !permissibles.contains( attachment ) )
			permissibles.add( attachment );
	}

	@Override
	public String toString()
	{
		return "User{" + metadata.toString() + ",Attachments{" + getAttachments().map( Object::toString ).collect( Collectors.joining( "," ) ) + "}}";
	}

	public void unregisterAttachment( UserAttachment attachment )
	{
		permissibles.remove( attachment );
	}
}
