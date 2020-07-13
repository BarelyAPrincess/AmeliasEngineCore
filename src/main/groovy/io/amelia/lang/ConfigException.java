/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.lang;

import io.amelia.engine.config.ConfigData;

public class ConfigException
{
	private ConfigException()
	{
		// Static Access
	}

	public static class Error extends ParcelableException.Error
	{
		public Error( ConfigData node )
		{
			super( node );
		}

		public Error( ConfigData node, String message )
		{
			super( node, message );
		}

		public Error( ConfigData node, String message, Throwable cause )
		{
			super( node, message, cause );
		}

		public Error( ConfigData node, Throwable cause )
		{
			super( node, cause );
		}

		public ConfigData getConfigNode()
		{
			return ( ConfigData ) node;
		}
	}

	public static class Ignorable extends ParcelableException.Ignorable
	{
		public Ignorable( ConfigData node )
		{
			super( node );
		}

		public Ignorable( ConfigData node, String message )
		{
			super( node, message );
		}

		public Ignorable( ConfigData node, String message, Throwable cause )
		{
			super( node, message, cause );
		}

		public Ignorable( ConfigData node, Throwable cause )
		{
			super( node, cause );
		}

		public ConfigData getConfigNode()
		{
			return ( ConfigData ) node;
		}
	}
}
