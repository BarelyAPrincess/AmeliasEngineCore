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

/**
 * Present Usage:
 * {@link io.amelia.data.ContainerBase} Thrown for read only nodes.
 */
public class ContainerException extends ApplicationException.Runtime
{
	public ContainerException()
	{
		super();
	}

	public ContainerException( String message )
	{
		super( message );
	}

	public ContainerException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public ContainerException( Throwable cause )
	{
		super( cause );
	}
}
