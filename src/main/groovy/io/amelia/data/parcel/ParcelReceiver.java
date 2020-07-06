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

import io.amelia.lang.ParcelException;

/**
 * When you have a parcel or signal to be transmitted to another part of the application,
 * you first need to find the {@link ParcelReceiver} intended for handling the parcel.
 * Receivers are a dime-a-dozen, they are instigated on their own or are automatically
 * made available by sub-systems and plugins.
 */
public interface ParcelReceiver
{
	void handleParcel( ParcelCarrier parcelCarrier ) throws ParcelException.Error;

	/**
	 * Indicates if async loopers and tasks can execute this receiver asynchronously.
	 * Trumps both {@link io.amelia.looper.AbstractLooper#isAsync()} and {@link io.amelia.looper.queue.EntryAbstract#isAsync()}.
	 *
	 * @return True if so, false otherwise.
	 */
	default boolean isAsyncAllowed()
	{
		return true;
	}
}
