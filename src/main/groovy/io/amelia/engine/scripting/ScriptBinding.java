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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import groovy.lang.MissingPropertyException;

/**
 * Our own binding extended so we can better track if and when a binding variable is changed
 */
public class ScriptBinding
{
	private final Set<String> history = new HashSet<>();
	private Map<String, Object> variables;

	public ScriptBinding()
	{

	}

	public ScriptBinding( Map<String, Object> map )
	{
		variables = map;
	}

	public void clearHistory()
	{
		history.clear();
	}

	public Set<String> getUpdateHistory()
	{
		return Collections.unmodifiableSet( history );
	}

	/**
	 * @param name the name of the variable to lookup
	 *
	 * @return the variable value
	 */
	public Object getVariable( String name )
	{
		if ( variables == null )
			throw new MissingPropertyException( name, this.getClass() );

		Object result = variables.get( name );

		if ( result == null && !variables.containsKey( name ) )
			throw new MissingPropertyException( name, this.getClass() );

		return result;
	}

	public Map<String, Object> getVariables()
	{
		if ( variables == null )
			variables = new LinkedHashMap<>();
		return variables;
	}

	/**
	 * Simple check for whether the binding contains a particular variable or not.
	 *
	 * @param name the name of the variable to check for
	 */
	public boolean hasVariable( String name )
	{
		return variables != null && variables.containsKey( name );
	}

	/**
	 * Sets the value of the given variable
	 *
	 * @param name  the name of the variable to set
	 * @param value the new value for the given variable
	 */
	public void setVariable( String name, Object value )
	{
		if ( variables == null )
			variables = new LinkedHashMap<>();
		variables.put( name, value );
		history.add( name );
	}
}
