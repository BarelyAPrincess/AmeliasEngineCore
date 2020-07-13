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

import io.amelia.data.parcel.ParcelReceiver;

public interface UserAttachment extends EntitySubject, ParcelReceiver //, CommandSender
{
	/**
	 * TODO This is specific to network connections but we should probably tweak it to support all sorts of connection types.
	 */
	String getIpAddress();
}
