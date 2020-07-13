/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.looper.queue;

import javax.annotation.Nonnull;

import io.amelia.engine.EngineCore;
import io.amelia.engine.looper.AbstractLooper;
import io.amelia.support.DateAndTime;
import io.amelia.extra.UtilityExceptions;
import io.amelia.extra.UtilityMath;

public abstract class EntryAbstract implements Comparable<EntryAbstract>
{
	protected final boolean async;
	protected final long id = AbstractLooper.getGloballyUniqueId();
	protected final DefaultQueue queue;
	// Traceback for looper creation debug, only populated during development mode.
	protected final StackTraceElement[] stackTraceElements = EngineCore.isDevelopment() ? Thread.currentThread().getStackTrace() : new StackTraceElement[0];
	protected final long timestamp = DateAndTime.epoch();
	/**
	 * Indicates when the entry has been processed by the queue
	 * <p>
	 * This boolean is set when the message is enqueued and remains set while it
	 * is delivered and afterwards when it is recycled. The flag is only cleared
	 * once {@link #recycle()} is called and it's contents are zeroed.
	 * <p>
	 * It is an error to attempt to enqueue or recycle a message that is already finalized.
	 */
	private boolean finalized;

	public EntryAbstract( @Nonnull DefaultQueue queue )
	{
		this( queue, false );
	}

	public EntryAbstract( @Nonnull DefaultQueue queue, boolean async )
	{
		this.queue = queue;
		this.async = async;
	}

	public void cancel()
	{
		synchronized ( queue.entries )
		{
			if ( queue.getActiveEntry() == this )
			{
				queue.clearState();
				queue.wake();
			}
			else
				queue.entries.remove( this );
		}
	}

	@Override
	public int compareTo( @Nonnull EntryAbstract entryAbstract )
	{
		return UtilityMath.nonZero( Long.compare( getWhen(), entryAbstract.getWhen() ), Long.compare( getId(), entryAbstract.getId() ) ).orElse( 0 );
	}

	public String getCreationStackTrace()
	{
		return UtilityExceptions.stackTraceToString( stackTraceElements );
	}

	public long getCreationTimestamp()
	{
		return timestamp;
	}

	public long getId()
	{
		return id;
	}

	public long getLastOverloadMillis()
	{
		return queue.getLastOverloadMillis();
	}

	public long getLastPolledMillis()
	{
		return queue.getLastPolledMillis();
	}

	public long getLoopStartMillis()
	{
		return queue.getLoopStartMillis();
	}

	public int getPositionInQueue()
	{
		synchronized ( queue.entries )
		{
			int pos = 0;
			for ( EntryAbstract queueTask : queue.entries )
				if ( queueTask == this )
					return pos;
				else
					pos++;

			return -1;
		}
	}

	/**
	 * Used for sorting, indicates when the entry is scheduled for processing.
	 */
	public abstract long getWhen();

	public boolean isActive()
	{
		return queue.getActiveEntry() == this;
	}

	public boolean isAsync()
	{
		return async;
	}

	public boolean isEnqueued()
	{
		synchronized ( queue.entries )
		{
			return queue.entries.contains( this );
		}
	}

	public boolean isFinalized()
	{
		return finalized;
	}

	/**
	 * Determines that the entry can be removed from the queue without causing any bugs to the Application.
	 *
	 * @return True if removal is permitted and this task doesn't have to run.
	 */
	public abstract boolean isSafe();

	/**
	 * @hide
	 */
	public void markFinalized()
	{
		finalized = true;
	}

	/**
	 * @hide
	 */
	public void recycle()
	{
		cancel();
		finalized = false;
	}
}
