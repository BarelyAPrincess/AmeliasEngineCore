/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine;

import io.amelia.data.parcel.ParcelReceiver;
import io.amelia.engine.looper.MainLooper;
import io.amelia.engine.looper.queue.EntryAbstract;
import io.amelia.lang.ApplicationException;
import io.amelia.support.Runlevel;

public class EngineLooper extends MainLooper
{
	protected boolean canQuit()
	{
		return EngineCore.getRunlevel().intValue() <= 100;
	}

	@Override
	public ParcelReceiver getParcelReceiver()
	{
		return EngineCore.getApplication();
	}

	public boolean isDisposed()
	{
		return EngineCore.isRunlevel( Runlevel.DISPOSED );
	}

	@Override
	public boolean isPermitted( EntryAbstract entry )
	{
		if ( EngineCore.getRunlevel().intValue() < Runlevel.MAINLOOP.intValue() && entry instanceof TaskEntry )
			throw new ApplicationException.Runtime( entry.getClass().getSimpleName() + " can only be posted to the FoundationLooper at runlevel MAINLOOP and above. Current runlevel is " + EngineCore.getRunlevel() );

		// TODO Check known built-in AbstractEntry sub-classes.
		return true;
	}

	@Override
	protected void quit( boolean removePendingMessages )
	{
		if ( !canQuit() )
			throw new ApplicationException.Runtime( "FoundationLooper is not permitted to quit." );

		super.quit( removePendingMessages );
	}

	@Override
	protected void quitFinal()
	{
		// Nothing
	}

	@Override
	protected void signalInfallibleStartup()
	{
		super.signalInfallibleStartup();

		// As soon as the looper gets started, we set the runlevel appropriately.
		EngineCore.setRunlevel( Runlevel.MAINLOOP );
	}
}
