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

import com.chiorichan.factory.localization.LocalizationException;
import com.chiorichan.site.Site;

import io.amelia.logging.LogBuilder;

/**
 * Using the {@link HTMLCommentParser} we attempt to parse the source for langTrans methods, i.e., {@literal <!-- langTrans(general.welcomeText) -->}
 */
public class LocaleParser extends HTMLCommentParser
{
	Site site;

	public LocaleParser()
	{
		super( "localeTrans" );
	}

	@Override
	public String resolveMethod( String... args ) throws Exception
	{
		if ( args.length > 2 )
			L.warning( "localeTrans() method only accepts one argument, ignored." );

		try
		{
			return site.getLocalization().localeTrans( args[1] );
		}
		catch ( LocalizationException e )
		{
			return args[1];
		}
	}

	public String runParser( String source, Site site ) throws Exception
	{
		this.site = site;

		return runParser( source );
	}
}
