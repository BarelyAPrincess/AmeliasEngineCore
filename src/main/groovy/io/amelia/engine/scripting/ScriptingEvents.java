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

/**
 * Provides an interface for which the Scripting Engine to notify Scripts of events, such as exception or before execution.
 */
public interface ScriptingEvents
{
	void onBeforeExecute( com.chiorichan.factory.ScriptingContext context );

	void onAfterExecute( com.chiorichan.factory.ScriptingContext context );

	void onException( com.chiorichan.factory.ScriptingContext context, Throwable throwable );
}
