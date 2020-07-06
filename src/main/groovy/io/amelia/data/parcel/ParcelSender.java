/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.data.parcel;

import javax.annotation.Nullable;

public interface ParcelSender
{
	/**
	 * Used to reply to a parcel sent from this {@link ParcelSender}
	 * However, it's not uncommon for the implementation to lack the ability to receive parcels, i.e., can only send parcels.
	 */
	@Nullable
	default ParcelReceiver getReplyTo()
	{
		return null;
	}
}
