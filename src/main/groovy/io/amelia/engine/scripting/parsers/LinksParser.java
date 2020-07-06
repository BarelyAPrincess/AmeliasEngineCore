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

import com.chiorichan.net.http.HttpRequestWrapper;
import com.chiorichan.site.Site;

public class LinksParser extends HTMLCommentParser
{
	HttpRequestWrapper request;
	Site site;

	public LinksParser()
	{
		super( "url_to" );
	}

	/**
	 * args: [] = http://example.com/
	 * args: [subdomain] = http://subdomain.example.com/
	 * TODO: Expand this function.
	 */
	@Override
	public String resolveMethod( String... args ) throws Exception
	{
		String url = request.isSecure() ? "https://" : "http://";

		if ( args.length >= 1 && !args[1].isEmpty() )
			url += args[1] + ".";

		url += request.getRootDomain() + "/";

		return url;
	}

	public String runParser( String source, HttpRequestWrapper request, Site site ) throws Exception
	{
		if ( request == null || site == null )
			return source;

		this.request = request;
		this.site = site;

		if ( source.isEmpty() )
			return "";

		return super.runParser( source );
	}

}
