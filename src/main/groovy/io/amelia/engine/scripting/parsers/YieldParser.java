/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.scripting.parsers;

import com.chiorichan.factory.ScriptingFactory;

import io.amelia.logging.LogBuilder;

/**
 * Using the {@link HTMLCommentParser} we attempt to parse the source for yield methods, i.e., {@literal <!-- yield(com.chiorichan.widget.menu) -->}
 */
public class YieldParser extends HTMLCommentParser
{
	ScriptingFactory factory;

	public YieldParser()
	{
		super( "yield" );
	}

	@Override
	public String resolveMethod( String... args ) throws Exception
	{
		if ( args.length > 2 )
			LogBuilder.get( factory ).warning( "EvalFactory: yield() method only accepts one argument, ignored." );

		return factory.getYieldBuffer().get( args[1] );
	}

	public String runParser( String source, ScriptingFactory factory ) throws Exception
	{
		this.factory = factory;

		return runParser( source );
	}
}
