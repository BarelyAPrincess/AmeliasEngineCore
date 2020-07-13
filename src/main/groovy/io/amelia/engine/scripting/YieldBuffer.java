/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.scripting;

import java.util.HashMap;
import java.util.Map;

public class YieldBuffer
{
	// TODO Expand for more practical uses

	private Map<String, String> yields = new HashMap<>();

	public String get( String key )
	{
		return yields.get( key );
	}

	public void set( String key, String value )
	{
		yields.put( key, value );
	}
}
