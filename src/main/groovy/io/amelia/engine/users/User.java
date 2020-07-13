package io.amelia.engine.users;

import io.amelia.data.parcel.ParcelReceiver;
import io.amelia.data.parcel.ParcelSender;
import io.amelia.engine.permissions.PermissibleEntity;

public interface User extends ParcelSender, ParcelReceiver
{
	/**
	 * Compiles a human readable display name, e.g., John Smith
	 *
	 * @return A human readable display name
	 */
	String getDisplayName();

	/**
	 * Returns the UserId for this User
	 *
	 * @return The UserId
	 */
	String getId();

	/**
	 * Returns the PermissibleEntity for this User
	 *
	 * @return The PermissibleEntity
	 */
	PermissibleEntity getPermissibleEntity();

	/**
	 * Returns the exact instance of UserMeta
	 *
	 * @return {@link UserInstance} instance of this User
	 */
	UserInstance i();

	boolean isInitialized();

	/**
	 * Returns the exact instance of UserMeta
	 *
	 * @return {@link UserMeta} instance of this User
	 */
	UserMeta meta();
}
