/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.permissions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import io.amelia.data.TypeBase;
import io.amelia.engine.config.ConfigKeys;
import io.amelia.engine.config.ConfigRegistry;
import io.amelia.engine.events.EventDispatcher;
import io.amelia.engine.permissions.backend.file.FileBackend;
import io.amelia.engine.permissions.backend.memory.MemoryBackend;
import io.amelia.engine.permissions.backend.sql.SQLBackend;
import io.amelia.engine.permissions.event.PermissibleEvent;
import io.amelia.engine.permissions.event.PermissibleSystemEvent;
import io.amelia.engine.permissions.lang.PermissionBackendException;
import io.amelia.extra.UtilityObjects;
import io.amelia.lang.ConfigException;
import io.amelia.support.EnumColor;
import io.amelia.support.Voluntary;
import io.amelia.support.VoluntaryBoolean;

public abstract class PermissionRegistry
{
	private static final Set<Permission> permissions = new HashSet<>();
	protected static boolean allowOps = true;
	protected static boolean hasWhitelist = false;
	protected static boolean isDebugEnabled = false;
	private static PermissionBackend backend = null;
	private static volatile Set<PermissionBackend> backends = new CopyOnWriteArraySet<>();
	private static Map<UUID, PermissibleGroup> defaultGroups = new HashMap<>();
	private static Map<UUID, PermissibleEntity> entities = new HashMap<>();
	private static Map<UUID, PermissibleGroup> groups = new HashMap<>();
	private static RegExpMatcher matcher = null;
	private static Map<String, Set<String>> refInheritance = new ConcurrentHashMap<>();

	static
	{
		addBackend( new MemoryBackend( false ) );
		addBackend( new SQLBackend( false ) );
		addBackend( new FileBackend( true ) );

		hasWhitelist = ConfigRegistry.config.getBoolean( ConfigKeys.WHITELIST_ENABLED );
		isDebugEnabled = ConfigRegistry.config.getBoolean( ConfigKeys.DEBUG_MODE_ENABLED );
		allowOps = ConfigRegistry.config.getBoolean( ConfigKeys.ALLOW_OPS );

		// WritableBinding binding = Bindings.getSystemNamespace( HoneyPermissions.class );
		// binding.registerFacadeBinding( WhitelistService.class, FacadePriority.LOWEST, WhitelistService::new );
		// binding.registerFacadeBinding( PermissionService.class, FacadePriority.LOWEST, PermissionService::new );

		// ServiceDispatcher.registerService( Permission.class, modular, modular, ServicePriority.Lowest );
		// ServiceDispatcher.registerService( PermissibleGroup.class, modular, modular, ServicePriority.Lowest );
		// ServiceDispatcher.registerService( PermissibleEntity.class, modular, modular, ServicePriority.Lowest );
	}

	protected static void addBackend( PermissionBackend backend );

	protected static void addBackend( PermissionBackend backend )
	{
		backends.add( backend );
	}

	public static boolean allowOps()
	{
		return allowOps;
	}

	protected static void callEvent( PermissibleSystemEvent.Action action )
	{
		callEvent( new PermissibleSystemEvent( action ) );
	}

	protected static void callEvent( PermissibleEvent event )
	{
		EventDispatcher.callEvent( event );
	}

	public static PermissibleState checkPermissibleState( UUID uuid )
	{
		PermissibleEntity entity = getPermissibleEntity( uuid );

		if ( entity != null )
			if ( hasWhitelist() && !entity.isWhitelisted() )
				return PermissibleState.NOT_WHITELISTED;
			else if ( entity.isBanned() )
				return PermissibleState.BANNED;

		return PermissibleState.PERMITTED;
	}

	/**
	 * Check if entity with name has permission in ref
	 *
	 * @param uuid       entity uuid
	 * @param permission permission as string to check against
	 * @param refs       References
	 *
	 * @return true on success false otherwise
	 */

	public static PermissionResult checkPermission( UUID uuid, PermissionNamespace permission, String... refs )
	{
		PermissibleEntity entity = getPermissibleEntity( uuid );

		if ( entity == null )
			throw new RuntimeException( "Entity returned null! This is a bug and needs to be reported to the developers." );

		return entity.checkPermission( permission, References.format( refs ) );
	}

	/**
	 * Check if specified entity has specified permission
	 *
	 * @param entity    entity object
	 * @param namespace permission string to check against
	 *
	 * @return true on success false otherwise
	 */

	public static PermissionResult checkPermission( Permissible entity, PermissionNamespace namespace )
	{
		return checkPermission( entity.uuid(), namespace, "" );
	}

	public static Permission createNode( String namespace )
	{
		return createNode( namespace, VoluntaryBoolean.empty() );
	}

	public static Permission createNode( String namespace, VoluntaryBoolean valueDefault )
	{
		return createNode( PermissionNamespace.of( namespace ), valueDefault );
	}

	public static Permission createNode( PermissionNamespace namespace )
	{
		return createNode( namespace, VoluntaryBoolean.empty() );
	}

	/**
	 * Finds a registered permission node in the stack by crawling.
	 *
	 * @param namespace    The full name space we need to crawl for.
	 * @param valueDefault What PermissionType should the final node be
	 *
	 * @return The child node based on the namespace. Will return NULL if non-existent and createChildren is false.
	 */
	public static Permission createNode( PermissionNamespace namespace, VoluntaryBoolean valueDefault )
	{
		if ( isDebugEnabled() )
			L.info( EnumColor.YELLOW + "Created permission " + namespace + " with default value " + valueDefault.getString() );

		String first = namespace.dropFirstString();

		Permission current = getRootNode( first );

		if ( current == null )
			current = Permission.of( first );

		boolean created = true;

		for ( String node : namespace.getNames() )
		{
			if ( current.hasChild( node ) )
			{
				current = current.getChild( node );
				created = false;
			}
			else
			{
				current = current.getChildOrCreate( node );
				created = true;
			}
		}

		if ( created )
			current.getPermissionMeta().setValueDefault( valueDefault );

		return current;
	}

	/**
	 * Return default group object
	 *
	 * @return default group object. null if not specified
	 */
	public static PermissibleGroup getDefaultGroup( References refs )
	{
		return backend.getDefaultGroup( refs );
		/*
		TODO
		String refIndex = ""; // refs != null ? refs : "";

		if ( !defaultGroups.containsKey( refIndex ) )
			defaultGroups.put( refIndex, getDefaultGroup( refs, getDefaultGroup( null, null ) ) );

		return defaultGroups.get( refIndex );*/
	}

	protected static PermissibleGroup getDefaultGroup( References refs, PermissibleGroup fallback )
	{
		PermissibleGroup defaultGroup = backend.getDefaultGroup( refs );

		if ( defaultGroup == null && refs == null )
		{
			L.warning( "No default group defined. Use \"perm set default group <group> [ref]\" to define default group." );
			return fallback;
		}

		if ( defaultGroup != null )
			return defaultGroup;

		return fallback;
	}

	/**
	 * Return all registered entity objects
	 *
	 * @return PermissibleEntity array
	 */
	public static Stream<PermissibleEntity> getEntities()
	{
		return entities.values().stream();
	}

	/**
	 * Finds entities assigned provided permission. WARNING: Will not return a complete list if permissions.preloadEntities config is false.
	 *
	 * @param perm The permission to check for.
	 *
	 * @return a list of permissibles that have that permission assigned to them.
	 */
	public static Stream<PermissibleEntity> getEntitiesWithPermission( Permission perm )
	{
		return entities.values().stream().filter( p -> p.checkPermission( perm ).isAssigned() );
	}

	/**
	 * Finds entities assigned provided permission.
	 *
	 * @param namespace The permission to check for.
	 *
	 * @return a list of permissibles that have that permission assigned to them.
	 *
	 * @see PermissionRegistry#getEntitiesWithPermission(Permission)
	 */
	public static Stream<PermissibleEntity> getEntitiesWithPermission( @Nonnull PermissionNamespace namespace )
	{
		return getNode( namespace ).map( this::getEntitiesWithPermission ).orElse( Stream.empty() );
	}

	/**
	 * Return object for specified group
	 *
	 * @param uuid the group id
	 *
	 * @return PermissibleGroup object
	 */
	public static PermissibleGroup getGroup( @Nonnull UUID uuid )
	{
		return getGroup( uuid, true );
	}

	public static PermissibleGroup getGroup( @Nonnull UUID uuid, boolean create )
	{
		if ( groups.containsKey( uuid ) )
			return groups.get( uuid );
		else if ( create )
		{
			PermissibleGroup group = backend.getGroup( uuid );
			groups.put( uuid, group );
			return group;
		}
		else
			return null;
	}

	/**
	 * Return all groups
	 *
	 * @return PermissibleGroup array
	 */
	public static Stream<PermissibleGroup> getGroups()
	{
		return groups.values().stream();
	}

	public static Stream<PermissibleGroup> getGroups( String query )
	{
		return groups.values().stream().filter( g -> g.getName().startsWith( query.toLowerCase() ) );
	}

	public static RegExpMatcher getMatcher()
	{
		if ( matcher == null )
			matcher = new RegExpMatcher();
		return matcher;
	}

	/**
	 * Attempts to find a Permission Node. Will not create the node if non-existent.
	 *
	 * @param namespace The namespace to find, e.g., io.amelia.user
	 *
	 * @return The found permission, null if non-existent
	 */
	public static Voluntary<Permission> getNode( PermissionNamespace namespace )
	{
		String[] nodes = namespace.getNames();

		if ( nodes.length < 1 )
			return Voluntary.empty();

		Permission curr = getRootNode( nodes[0] );

		if ( curr == null )
			return Voluntary.empty();

		if ( nodes.length == 1 )
			return Voluntary.of( curr );

		for ( String node : Arrays.copyOfRange( nodes, 1, nodes.length ) )
		{
			Permission child = curr.getChild( node.toLowerCase() );
			if ( child == null )
				return null;
			else
				curr = child;
		}

		return Voluntary.of( curr );
	}

	protected static Voluntary<Permission> getNodeByLocalName( @Nonnull String name )
	{
		return Voluntary.of( permissions.stream().filter( perm -> name.equalsIgnoreCase( perm.getLocalName() ) ).findAny() );
	}

	/**
	 * Finds registered permission nodes.
	 *
	 * @param ns The full name space we need to crawl for.
	 *
	 * @return A list of permissions that matched the namespace. Will return more then one if namespace contained asterisk.
	 */
	public static Stream<Permission> getNodes( PermissionNamespace ns )
	{
		if ( ns == null || ns.getNodeCount() < 1 )
			return Stream.empty();

		return permissions.stream().filter( perm -> perm.getPermissionNamespace().matches( ns ) );
	}

	public static Stream<Permission> getNodes( String ns )
	{
		if ( UtilityObjects.isEmpty( ns ) )
			return Stream.empty();

		return permissions.stream().filter( perm -> perm.getPermissionNamespace().matches( ns ) );
	}

	public static PermissibleEntity getPermissibleEntity( @Nonnull UUID uuid )
	{
		return getPermissibleEntity( uuid, true );
	}

	public static PermissibleEntity getPermissibleEntity( @Nonnull UUID uuid, boolean create )
	{
		if ( entities.containsKey( uuid ) )
			return entities.get( uuid );
		else if ( create )
		{
			PermissibleEntity entity = backend.getEntity( uuid );
			entities.put( uuid, entity );
			return entity;
		}
		else
			return null;
	}

	public static Voluntary<Permission> getPermission( PermissionNamespace namespace )
	{
		return Voluntary.of( permissions.stream().filter( perm -> namespace.equals( perm.getPermissionNamespace() ) ).findAny() );
	}

	public static Voluntary<Permission> getPermission( String path )
	{
		return getPermission( PermissionNamespace.of( path ) );
	}

	public static Set<String> getRefInheritance( String ref )
	{
		return refInheritance.containsKey( ref ) ? refInheritance.get( ref ) : new HashSet<>();
	}

	public static Set<String> getReferences()
	{
		return refInheritance.keySet();
	}

	protected static Permission getRootNode( String name )
	{
		for ( Permission perm : permissions )
			if ( !perm.hasParent() && perm.getLocalName().equalsIgnoreCase( name ) )
				return perm;
		return null;
	}

	public static Stream<Permission> getRootNodes()
	{
		return getRootNodes( true );
	}

	public static Stream<Permission> getRootNodes( boolean ignoreSysNode )
	{
		return permissions.stream().filter( perm -> !perm.hasParent() && ( !perm.getPermissionNamespace().startsWith( "sys" ) || ignoreSysNode ) );
	}

	/**
	 * Loads all groups and entities from the backend data source.
	 *
	 * @throws PermissionBackendException
	 */
	public static void loadData() throws PermissionBackendException
	{
		if ( isDebugEnabled() )
			L.warning( EnumColor.YELLOW + "Permission debug is enabled!" );

		groups.clear();
		entities.clear();

		if ( isDebugEnabled() )
			L.info( EnumColor.YELLOW + "Loading permissions from backend!" );

		backend.loadPermissions();
		PermissionDefault.initNodes();

		if ( isDebugEnabled() )
			L.info( EnumColor.YELLOW + "Loading groups from backend!" );
		backend.loadGroups();

		if ( isDebugEnabled() )
			L.info( EnumColor.YELLOW + "Loading entities from backend!" );
		backend.loadEntities();

		/*if ( debug() )
		{
			L.info( EnumColor.YELLOW + "Dumping loaded permissions:" );
			for ( Permission root : getRootNodes( false ) )
				root.debugPermissionStack( 0 );
		}*/

	}

	/**
	 * Attempts to move a permission from one namespace to another. e.g., io.amelia.old.same.oldname -> io.amelia.new.same.newname.
	 *
	 * @param newNamespace    The new namespace you wish to use.
	 * @param appendLocalName Pass true if you wish the method to append the LocalName to the new namespace. If the local name of the new namespace is different then this permission will be renamed.
	 *
	 * @return true if move/rename was successful.
	 */
	public static boolean refactorNamespace( String newNamespace, boolean appendLocalName )
	{
		// PermissionNamespace ns = getPermissionNamespace();
		// TODO THIS!
		return false;
	}

	/**
	 * Register new timer task
	 *
	 * @param task  TimerTask object
	 * @param delay delay in seconds
	 */
	protected static void registerTask( LooperTask task, int delay )
	{
		LooperRouter.getMainLooper().postTaskRepeating( task, delay * 50, true );
	}

	public static void reload() throws PermissionBackendException
	{
		reset();
		backend.reloadBackend();

		backend.loadEntities();
		backend.loadGroups();

		hasWhitelist = ConfigRegistry.config.getBoolean( ConfigKeys.WHITELIST_ENABLED );
	}

	/**
	 * Reset all in-memory groups and entities, clean up runtime stuff, reloads backend
	 */
	public static void reset() throws PermissionBackendException
	{
		defaultGroups.clear();
		entities.clear();
		groups.clear();

		callEvent( PermissibleSystemEvent.Action.RELOADED );
	}

	/**
	 * Reset in-memory object of specified entity
	 *
	 * @param entity the entity
	 */
	public static void resetEntity( Permissible entity )
	{
		entities.remove( entity.uuid() );
	}

	/**
	 * Reset in-memory object for groupName
	 *
	 * @param groupName group's name
	 */
	public static void resetGroup( String groupName )
	{
		groups.remove( groupName );
	}

	/**
	 * Forcefully saves groups and entities to the backend data source.
	 */
	public static void saveData() throws PermissionBackendException
	{
		permissions.forEach( Permission::commitToBackend );

		for ( PermissibleGroup entity : groups.values() )
			entity.save();

		for ( PermissibleEntity entity : entities.values() )
			entity.save();
	}

	public static void setAllowOp( boolean allowOp ) throws ConfigException.Error
	{
		ConfigRegistry.config.setValue( ConfigKeys.ALLOW_OPS, allowOp );
		this.allowOps = allowOp;
	}

	public static void setAllowOp( boolean allowOp ) throws ConfigException.Error
	{
		callEvent( PermissibleSystemEvent.Action.ALLOWOP_TOGGLE );
		this.allowOps = allowOp;
	}

	public static void setDebugEnabled( boolean debugEnabled ) throws ConfigException.Error
	{
		callEvent( PermissibleSystemEvent.Action.DEBUGMODE_TOGGLE );
		this.isDebugEnabled = debugEnabled;
	}

	public static void setDebugEnabled( boolean debugEnabled ) throws ConfigException.Error
	{
		ConfigRegistry.config.setValue( ConfigKeys.DEBUG_MODE_ENABLED, debugEnabled );
		super.setDebugEnabled( debugEnabled );
	}

	public static void setDefaultGroup( PermissibleGroup group, References refs );

	/**
	 * Set default group to specified group
	 *
	 * @param group PermissibleGroup group object
	 */
	public static void setDefaultGroup( PermissibleGroup group, References refs )
	{
		if ( group == null || group.equals( defaultGroups ) )
			return;

		backend.setDefaultGroup( group.uuid(), refs );

		defaultGroups.clear();

		callEvent( PermissibleSystemEvent.Action.DEFAULTGROUP_CHANGED );
		callEvent( new PermissibleEntityEvent( group, PermissibleEntityEvent.Action.DEFAULTGROUP_CHANGED ) );
	}

	public static void setHasWhitelist( boolean hasWhitelist ) throws ConfigException.Error
	{
		callEvent( PermissibleSystemEvent.Action.WHITELIST_TOGGLE );
		this.hasWhitelist = hasWhitelist;
	}

	public static void setHasWhitelist( boolean hasWhitelist ) throws ConfigException.Error
	{
		ConfigRegistry.config.setValue( ConfigKeys.WHITELIST_ENABLED, hasWhitelist );
		this.hasWhitelist = hasWhitelist;
	}

	public static void setRefInheritance( String ref, Collection<String> heir );

	public static void setRefInheritance( String ref, Collection<String> heir )
	{
		Set<String> cur = getRefInheritance( ref );
		cur.addAll( heir );
		refInheritance.put( ref, cur );
	}

	public void end()
	{
		try
		{
			reset();
		}
		catch ( PermissionBackendException ignore )
		{
			// Ignore because we're shutting down so who cares
		}
	}

	/**
	 * Return current backend
	 *
	 * @return current backend object
	 */
	public PermissionBackend getBackend()
	{
		return backend;
	}

	/**
	 * Set backend to specified backend. This would also cause backend resetting.
	 *
	 * @param backendName name of backend to set to
	 */
	public void setBackend( String backendName ) throws PermissionBackendException
	{
		synchronized ( this )
		{
			backend = backends.stream().filter( backend -> backend.getAliasName().equals( backendName ) ).findFirst().orElseThrow( () -> new IllegalArgumentException( "Backend " + backendName + " not found!" ) );
			reset();
			backend.initialize();

			loadData();
		}

		callEvent( PermissibleSystemEvent.Action.BACKEND_CHANGED );
	}

	public static void setBackend( String backendName ) throws PermissionBackendException;

	public Stream<PermissionBackend> getBackends()
	{
		return backends.stream();
	}

	public PermissibleGroup getDefaultGroup()
	{
		return getDefaultGroup( null );
	}

	public void setDefaultGroup( PermissibleGroup group )
	{
		setDefaultGroup( group, null );
	}

	public static void setDefaultGroup( PermissibleGroup group );
}
