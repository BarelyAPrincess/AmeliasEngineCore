/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.subsystem.looper;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import io.amelia.engine.subsystem.EngineCore;
import io.amelia.engine.subsystem.looper.queue.EntryAbstract;
import io.amelia.extra.UtilityObjects;

/**
 * Provides a runnable that blocks until the wrapped runnable is executed.
 * Be sure to pass this Runnable to the executor before calling {@link #postAndWait(long)} or else it will hang.
 */
public final class BlockingTask<E extends Exception> implements LooperTask<E>
{
	private final LooperTask<E> mTask;
	private boolean mDone;

	public BlockingTask( @Nonnull LooperTask<E> task )
	{
		mTask = task;
	}

	@Override
	public void execute( EntryAbstract entry ) throws E
	{
		try
		{
			mTask.execute( entry );
		}
		finally
		{
			synchronized ( this )
			{
				mDone = true;
				notifyAll();
			}
		}
	}

	public boolean postAndWait( @Nonnegative long timeout )
	{
		UtilityObjects.notNegative( timeout );

		synchronized ( this )
		{
			if ( timeout > 0 )
			{
				final long expirationTime = EngineCore.uptime() + timeout;
				while ( !mDone )
				{
					long delay = expirationTime - EngineCore.uptime();
					if ( delay <= 0 )
					{
						return false; // timeout
					}
					try
					{
						wait( delay );
					}
					catch ( InterruptedException ex )
					{
						// Ignore
					}
				}
			}
			else
			{
				while ( !mDone )
				{
					try
					{
						wait();
					}
					catch ( InterruptedException ex )
					{
						// Ignore
					}
				}
			}
		}
		return true;
	}

	public boolean postAndWait()
	{
		return postAndWait( 0L );
	}
}
