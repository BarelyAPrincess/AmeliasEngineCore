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

public class ByteStack
{
	private byte[] bytes;

	public ByteStack( byte b )
	{
		bytes = new byte[] {b};
	}

	public void push( byte b )
	{
		bytes = io.amelia.support.Arrs.push( bytes, b );
	}

	public void pop()
	{
		bytes = io.amelia.support.Arrs.pop( bytes );
	}

	public byte peek()
	{
		return bytes[bytes.length - 1];
	}

	public byte[] getBytes()
	{
		return bytes;
	}
}
