/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.permissions.backend.file;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import io.amelia.data.parcel.Parcel;
import io.amelia.foundation.Foundation;
import io.amelia.lang.ParcelableException;
import io.amelia.permissions.ChildPermission;
import io.amelia.permissions.PermissibleEntity;
import io.amelia.permissions.PermissibleGroup;
import io.amelia.permissions.Permission;
import io.amelia.permissions.PermissionNamespace;
import io.amelia.permissions.Permissions;
import io.amelia.permissions.References;
import io.amelia.permissions.lang.PermissionBackendException;
import io.amelia.support.EnumColor;
import io.amelia.support.Streams;

public class FileEntity extends PermissibleEntity
{
	private final FileBackend backend;

	public FileEntity( FileBackend backend, UUID uuid, String name )
	{
		super( uuid, name );
		this.backend = backend;
	}

	@Override
	public void reloadGroups()
	{
		if ( isDebug() )
			Foundation.L.info( EnumColor.YELLOW + "Groups being loaded for entity " + uuid() );

		clearGroups();
		clearTimedGroups();

		Parcel groups = backend.getParcel().getChildOrCreate( "entities." + uuid().toString() + ".groups" );
		groups.getChildren().forEach( child -> addGroup0( backend.getPermissions().getGroup( UUID.fromString( child.getLocalName() ) ), References.format( child.getString( "refs" ).orElse( "" ) ) ) );
	}

	@Override
	public void reloadPermissions()
	{
		if ( isDebug() )
			Permissions.L.info( EnumColor.YELLOW + "Permissions being loaded for entity " + uuid() );

		Parcel permissions = backend.getParcel().getChildOrCreate( "entities." + uuid().toString() + ".permissions" );

		clearPermissions();
		clearTimedPermissions();

		permissions.getChildren().forEach( node -> {
			PermissionNamespace ns = PermissionNamespace.of( node.getLocalName().replace( '_', '.' ) );

			Stream<Permission> perms = ns.containsRegex() ? Foundation.getPermissions().getNodes( ns ) : Stream.of( ns.createPermission() );

			perms.forEach( perm -> addPermission( new ChildPermission( this, perm, node.getBoolean(), -1 ), References.format( node.getString( "refs" ).orElse( "" ) ) ) );
		} );
	}

	@Override
	public void remove()
	{
		backend.getParcel().getChildOrCreate( "entities" ).childDestroy( uuid().toString() );
	}

	@Override
	public void save()
	{
		try
		{
			if ( isVirtual() )
				return;

			if ( isDebug() )
				Permissions.L.info( EnumColor.YELLOW + "Entity " + getStringId() + " being saved to backend" );

			Parcel root = backend.getParcel().getChildOrCreate( "entities." + uuid().toString() );

			Streams.forEachWithException( getChildPermissions( null ), child -> {
				Permission perm = child.getPermission();
				Parcel sub = root.getChildOrCreate( "permissions." + perm.getPermissionNamespace().getString( "_" ) );
				sub.setValue( "value", child.getValue().orElse( null ) );
				sub.setValue( "refs", child.getReferences().map( References::join ).orElse( null ) );
			} );

			for ( Map.Entry<PermissibleGroup, References> entry : getGroupEntries( null ).entrySet() )
				root.setValue( "groups." + entry.getKey().uuid().toString(), entry.getValue().join() );
		}
		catch ( ParcelableException.Error e )
		{
			throw new PermissionBackendException( e );
		}
	}
}
