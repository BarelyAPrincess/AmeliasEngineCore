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

import com.google.common.base.Joiner;

public abstract class HTMLCommentParser extends BasicParser
{
	// TODO Check method names are a-z, A-Z, and 0-9.

	public HTMLCommentParser( String... methods )
	{
		this( Joiner.on( "|" ).join( methods ) );
	}

	public HTMLCommentParser( String methods )
	{
		super( "<!--[\\t ]*(?:" + methods + ")\\((.*)\\);*[\\t ]*-->", "(<!--[\\t ]*(?:" + methods + ")\\((.*)\\);*[\\t ]*-->)" );
		// super( "<!-- *" + argumentName + "\\((.*)\\) *-->", "(<!-- *" + argumentName + "\\(.*\\) *-->)" );
	}
}
