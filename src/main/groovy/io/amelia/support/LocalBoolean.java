/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.support;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class LocalBoolean extends io.amelia.support.LocalObject<Boolean>
{
	private static Map<Class<?>, LocalBoolean> holders = new HashMap<>();

	@Nonnull
	public static io.amelia.support.LocalBoolean getHolder( Class<?> referenceClass )
	{
		io.amelia.support.LocalBoolean result = holders.get( referenceClass );
		if ( result == null )
		{
			result = new io.amelia.support.LocalBoolean();
			holders.put( referenceClass, result );
		}
		return result;
	}

	public LocalBoolean( boolean def )
	{
		super( () -> def );
	}

	public LocalBoolean()
	{
		super( () -> false );
	}
}
