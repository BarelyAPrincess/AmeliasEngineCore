/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.support;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.amelia.foundation.ConfigRegistry;
import io.amelia.extra.UtilityObjects;
import io.amelia.extra.UtilityStrings;

public class Namespace extends io.amelia.support.NodeStack<Namespace> implements Comparable<Namespace>
{
	public static io.amelia.support.Namespace empty( String separator )
	{
		return new io.amelia.support.Namespace( new String[0], separator );
	}

	public static io.amelia.support.Namespace empty()
	{
		return new io.amelia.support.Namespace( new String[0], "." );
	}

	public static boolean isTld( String domain )
	{
		domain = Http.hostnameNormalize( domain );
		for ( String tld : ConfigRegistry.config.getStringList( ConfigRegistry.ConfigKeys.TLDS ) )
			if ( domain.matches( tld ) )
				return true;
		return false;
	}

	public static io.amelia.support.Namespace of( String namespace )
	{
		return of( namespace, null );
	}

	public static io.amelia.support.Namespace of( String namespace, String glue )
	{
		namespace = UtilityObjects.notNullOrDef( namespace, "" );
		glue = UtilityObjects.notEmptyOrDef( glue, "." );
		return new io.amelia.support.Namespace( UtilityStrings.split( namespace, Pattern.compile( glue, Pattern.LITERAL ) ).collect( Collectors.toList() ), glue );
	}

	public static io.amelia.support.Namespace of( String[] nodes, String glue )
	{
		return new io.amelia.support.Namespace( nodes, glue );
	}

	public static io.amelia.support.Namespace of( String[] nodes )
	{
		return new io.amelia.support.Namespace( nodes );
	}

	public static io.amelia.support.Namespace of( Collection<String> nodes, String glue )
	{
		return new io.amelia.support.Namespace( nodes, glue );
	}

	public static io.amelia.support.Namespace of( Collection<String> nodes )
	{
		return new io.amelia.support.Namespace( nodes );
	}

	public static io.amelia.support.Namespace ofRegex( String namespace, String regex )
	{
		namespace = UtilityObjects.notNullOrDef( namespace, "" );
		regex = UtilityObjects.notEmptyOrDef( regex, "\\." );
		return new io.amelia.support.Namespace( UtilityStrings.split( namespace, Pattern.compile( regex ) ).collect( Collectors.toList() ) );
	}

	public static Domain parseDomain( String namespace )
	{
		namespace = Http.hostnameNormalize( namespace );

		if ( UtilityObjects.isEmpty( namespace ) )
			return new Domain( new io.amelia.support.Namespace(), new io.amelia.support.Namespace() );

		io.amelia.support.Namespace ns = of( namespace );
		int parentNodePos = -1;

		for ( int n = 0; n < ns.getNodeCount(); n++ )
		{
			String sns = ns.getSubNodes( n ).getString();
			if ( isTld( sns ) )
			{
				parentNodePos = n;
				break;
			}
		}

		return parentNodePos > 0 ? new Domain( ns.getSubNodes( parentNodePos ), ns.getSubNodes( 0, parentNodePos ) ) : new Domain( new io.amelia.support.Namespace(), ns );
	}

	private Namespace( String[] nodes, String glue )
	{
		super( io.amelia.support.Namespace::new, glue, nodes );
	}

	private Namespace( Collection<String> nodes, String glue )
	{
		super( io.amelia.support.Namespace::new, glue, nodes );
	}

	private Namespace( String glue )
	{
		super( io.amelia.support.Namespace::new, glue );
	}

	private Namespace( io.amelia.support.Namespace from, String[] nodes )
	{
		this( nodes );
	}

	private Namespace( String[] nodes )
	{
		this( nodes, "." );
	}

	private Namespace( Collection<String> nodes )
	{
		this( nodes, "." );
	}

	private Namespace()
	{
		this( "." );
	}

	public String getGlue()
	{
		return glue;
	}

	public io.amelia.support.Namespace setGlue( String glue )
	{
		this.glue = glue;
		return this;
	}

	public static class Domain
	{
		private final io.amelia.support.Namespace child;
		private final io.amelia.support.Namespace tld;

		private Domain( io.amelia.support.Namespace tld, io.amelia.support.Namespace child )
		{
			this.tld = tld;
			this.child = child;
		}

		public io.amelia.support.Namespace getChild()
		{
			return child;
		}

		public io.amelia.support.Namespace getChildDomain()
		{
			return child.getNodeCount() <= 1 ? new io.amelia.support.Namespace() : child.getSubNodes( 1 );
		}

		public io.amelia.support.Namespace getFullDomain()
		{
			return child.merge( tld );
		}

		public io.amelia.support.Namespace getRootDomain()
		{
			return of( child.getStringLast() + "." + tld.getString() );
		}

		public io.amelia.support.Namespace getTld()
		{
			return tld;
		}
	}
}
