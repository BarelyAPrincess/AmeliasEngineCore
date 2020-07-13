/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.events;

import javax.annotation.Nonnull;

import io.amelia.support.RegistrarContext;

public class AuthorNagEvent extends AbstractEvent
{
	private final String message;
	private final RegistrarContext registrarContext;

	public AuthorNagEvent( @Nonnull RegistrarContext registrar, @Nonnull String message )
	{
		this.registrarContext = registrar;
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	public RegistrarContext getRegistrarContext()
	{
		return registrarContext;
	}
}
