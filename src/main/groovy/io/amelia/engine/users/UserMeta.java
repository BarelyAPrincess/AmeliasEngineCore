package io.amelia.engine.users;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import io.amelia.data.parcel.ParcelCarrier;
import io.amelia.engine.permissions.PermissibleEntity;
import io.amelia.engine.permissions.PermissionRegistry;
import io.amelia.extra.UtilityEncrypt;
import io.amelia.extra.UtilityObjects;
import io.amelia.extra.UtilityStrings;
import io.amelia.lang.ParcelException;

public class UserMeta implements User, Iterable<Map.Entry<String, Object>>
{
	public static final List<String> IGNORED_KEYS = Arrays.asList( "userId" );
	/**
	 * Provides context into our existence
	 */
	private final UserContext context;
	/**
	 * Used to store our Account Metadata besides the required builtin key names.
	 */
	private final Map<String, Object> metadata = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
	private final String userId;
	/**
	 * Used as our reference to the Account Instance.<br>
	 * We use a WeakReference so the account can be logged out automatically when no longer used.
	 */
	private WeakReference<UserInstance> account = null;
	/**
	 * Indicates if we should keep the Account Instance loaded in Memory
	 */
	private boolean keepInMemory = false;
	/**
	 * Weak references the {@link PermissibleEntity} over at the Permission Manager.<br>
	 * Again, we use the {@link WeakReference} so it can be garbage collected when unused,<br>
	 * we reload it from the Permission Manager once needed again.
	 */
	private WeakReference<PermissibleEntity> permissibleEntity = null;
	/**
	 * Used to keep the Account Instance loaded in the memory when {@link #keepInMemory} is set to true<br>
	 * This counters our weak reference for variable {@link #account}
	 */
	@SuppressWarnings( "unused" )
	private UserInstance strongReference = null;

	UserMeta( UserContext context )
	{
		UtilityObjects.notNull( context );

		context.setUser( this );

		this.context = context;
		userId = context.getUserId();
		// locId = context.getLocId();

		if ( !userId.matches( "[a-zA-Z0-9]*" ) )
			throw new IllegalStateException( "The acctId must only contain the characters [a-zA-Z0-9]." );

		// if ( !"%".equals( locId ) && !locId.matches( "[a-zA-Z0-9]*" ) )
		// throw new IllegalStateException( "The locId must only contain the characters [a-zA-Z0-9]." );

		metadata.putAll( context.getValues() );

		/**
		 * Populate the PermissibleEntity for reasons... and notify the Account Creator
		 */
		context.creator().successInit( this, getPermissibleEntity() );
	}

	public boolean containsKey( String key )
	{
		return metadata.containsKey( key );
	}

	public Boolean getBoolean( String key )
	{
		try
		{
			return UtilObjects.castToBoolWithException( metadata.get( key ) );
		}
		catch ( ClassCastException e )
		{
			return false;
		}
	}

	/**
	 * Returns the {@link AccountContext} responsible for our existence
	 *
	 * @return Instance of AccountContext
	 */
	public AccountContext getContext()
	{
		return context;
	}

	@Override
	public String getDisplayName()
	{
		String name = context.creator().getDisplayName( this );
		return name == null ? getId() : name;
	}

	@Override
	public String getId()
	{
		return userId;
	}

	public Integer getInteger( String key )
	{
		return getInteger( key, 0 );
	}

	public Integer getInteger( String key, int def )
	{
		Object obj = metadata.get( key );
		Integer val = UtilityObjects.castToInt( obj );

		return val == null ? def : val;
	}

	public Stream<String> getKeys()
	{
		return metadata.keySet().stream();
	}

	public String getLogoffMessage()
	{
		return getId() + " has logged off the server";
	}

	public Map<String, Object> getMeta()
	{
		return Collections.unmodifiableMap( metadata );
	}

	public Object getObject( String key )
	{
		return metadata.get( key );
	}

	@Override
	public PermissibleEntity getPermissibleEntity()
	{
		if ( permissibleEntity == null || permissibleEntity.get() == null )
			permissibleEntity = new WeakReference<>( PermissionRegistry.getEntity( getId() ) );
		return permissibleEntity.get();
	}

	public String getString( String key )
	{
		return getString( key, null );
	}

	public String getString( String key, String def )
	{
		String val = UtilityObjects.castToString( metadata.get( key ) );
		return val == null ? def : val;
	}

	@Override
	public void handleParcel( ParcelCarrier parcelCarrier ) throws ParcelException.Error
	{
		// TODO Implement
	}

	@Override
	public UserInstance i()
	{
		if ( !isInitialized() )
			initAccount();

		return account.get();
	}

	private UserInstance initAccount()
	{
		UserInstance account = new UserInstance( this );
		this.account = new WeakReference<UserInstance>( account );

		if ( keepInMemory )
			strongReference = account;

		UserEvents.fireUserLoad( this );

		return account;
	}

	@Override
	public boolean isInitialized()
	{
		return account != null && account.get() != null;
	}

	@Override
	public Iterator<Map.Entry<String, Object>> iterator()
	{
		return Collections.unmodifiableMap( metadata ).entrySet().iterator();
	}

	/**
	 * Returns if the Account is will be kept in memory
	 * If you want to know if the Account is currently being kept in memory, See {@link #keptInMemory()}
	 *
	 * @return Will be kept in memory?
	 */
	public boolean keepInMemory()
	{
		return isInitialized() && keepInMemory;
	}

	/**
	 * Sets if the Account should stay loaded in the VM memory
	 *
	 * @param state Stay in memory?
	 */
	public void keepInMemory( boolean state )
	{
		strongReference = state ? account.get() : null;
		keepInMemory = state;
	}

	/**
	 * Returns if the Account is being kept in memory
	 * If you want to know if the Account will be kept in memory, See {@link #keepInMemory()}
	 *
	 * @return Is being kept in memory? Will always return false if the Account is not initialized.
	 */
	public boolean keptInMemory()
	{
		return isInitialized() && keepInMemory;
	}

	public Stream<String> keySet()
	{
		return metadata.keySet().stream();
	}

	@Override
	public UserMeta meta()
	{
		return this;
	}

	public void reload() throws UserException
	{
		context.creator().reload( this );
	}

	public void requireActivation()
	{
		metadata.put( "actnum", UtilityEncrypt.randomize( "z154f98wfjascvc" ) );
	}

	public void save() throws UserException
	{
		context.creator().save( this );
	}

	public void set( String key, Object obj )
	{
		UtilityObjects.notNull( key );

		if ( obj == null )
			metadata.remove( key );
		else
			metadata.put( key, obj );
	}

	@Override
	public String toString()
	{
		return "AccountMeta{acctId=" + userId + "," + UtilityStrings.join( metadata, ",", "=" ) + "}";
	}
}
