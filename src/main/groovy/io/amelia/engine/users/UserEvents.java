package io.amelia.engine.users;

import io.amelia.engine.events.EventDispatcher;
import io.amelia.engine.users.events.UserLoadEvent;

public class UserEvents
{
	public static void fireUserLoad( UserMeta userMeta )
	{
		EventDispatcher.callEvent( new UserLoadEvent( userMeta ) );
	}
}
