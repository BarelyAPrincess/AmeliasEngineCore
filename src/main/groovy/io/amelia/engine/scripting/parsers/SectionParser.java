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
 * Using the {@link HTMLCommentParser} we attempt to parse the source for yield methods, i.e., {@literal <!-- section(sectionKey) -->} or {@literal <!-- sectionEnd() -->}
 */
public class SectionParser extends HTMLCommentParser
{
	ScriptingFactory factory;
	int stackLevel = -1;
	String sectionKey;

	public SectionParser()
	{
		super( "section", "sectionEnd" );
	}

	@Override
	public String resolveMethod( String... args ) throws Exception
	{
		if ( args.length > 2 )
			LogBuilder.get( factory ).warning( "Method only accepts one argument, ignored." );

		if ( args[0].contains( "sectionEnd" ) )
		{
			if ( stackLevel == -1 )
				throw new IllegalStateException( "section() must be called first." );
			factory.getYieldBuffer().set( sectionKey, factory.obEnd( stackLevel ) );
		}
		else
		{
			stackLevel = factory.obStart();
			sectionKey = args[1];
		}

		return factory.getYieldBuffer().get( args[1] );
	}

	public String runParser( String source, ScriptingFactory factory ) throws Exception
	{
		this.factory = factory;

		return runParser( source );
	}
}
