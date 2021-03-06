/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.data.apache;

import com.google.common.base.Splitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class ApacheSection extends ApacheDirective
{
	private static final Pattern LINE_SPLITTER_PATTERN = Pattern.compile( " (?=([^\"]*\"[^\"]*\")*[^\"]*$)" );
	private final Set<ApacheDirective> directives = new TreeSet<>( new ApacheDirectiveComparator() );

	public ApacheSection()
	{
		super();
	}

	public ApacheSection( ApacheSection parent, String key, List<String> arguments )
	{
		super( parent, key, arguments );
	}

	private void appendRaw( BufferedReader br, int lineNum, String path ) throws IOException
	{
		for ( String l; ( l = br.readLine() ) != null; )
		{
			l = l.trim();

			if ( l == null || l.isEmpty() || l.startsWith( "#" ) )
				continue;

			if ( l.startsWith( "<" ) && l.endsWith( ">" ) )
			{
				if ( ( "</" + key + ">" ).equals( l ) )
					return;
				else
				{
					String[] sections = Splitter.on( LINE_SPLITTER_PATTERN ).splitToList( l.substring( 1, l.length() - 1 ) ).toArray( new String[0] );
					ApacheSection directive = new ApacheSection( this, sections[0], Arrays.asList( sections ).subList( 1, sections.length ) );
					directive.source = path;
					directive.lineNum = lineNum;
					directive.appendRaw( br, lineNum, path );
					directives.add( directive );
				}
			}
			else
			{
				String[] sections = Splitter.on( LINE_SPLITTER_PATTERN ).splitToList( l.substring( 1, l.length() - 1 ) ).toArray( new String[0] );
				ApacheDirective kv = new ApacheDirective( this, sections[0], Arrays.asList( sections ).subList( 1, sections.length ) );
				kv.source = path;
				kv.lineNum = lineNum;
				directives.add( kv );
			}
		}
	}

	public void appendRaw( BufferedReader br, String path ) throws IOException
	{
		appendRaw( br, 0, path );
	}

	public void appendRaw( String text, String path ) throws IOException
	{
		appendRaw( new BufferedReader( new StringReader( text ) ), 0, path );
	}

	public Set<ApacheDirective> directives()
	{
		return Collections.unmodifiableSet( directives );
	}
}
