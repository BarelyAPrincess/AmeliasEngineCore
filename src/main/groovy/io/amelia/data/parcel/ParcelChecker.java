/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.data.parcel;

import java.util.Arrays;
import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.amelia.data.ContainerBase;
import io.amelia.data.ContainerWithValue;
import io.amelia.data.ValueTypesTrait;
import io.amelia.lang.ParcelException;
import io.amelia.support.Streams;
import io.amelia.support.Voluntary;
import io.amelia.support.VoluntaryWithCause;

public class ParcelChecker
{
	public static <ContainerClass extends ContainerWithValue<ContainerClass, Object, ?>> void enforce( @Nonnull ParcelChecker parcelChecker, @Nonnull ContainerClass container, CheckerFlag... flags ) throws ParcelException.Error
	{
		if ( container.hasParent() )
			throw new ParcelException.Error( "The ParcelChecker must be called on the root, the current path is " + container.getCurrentPath() + "." );

		enforce( parcelChecker.root, container, EnumSet.copyOf( Arrays.asList( flags ) ) );
		enforce( container, parcelChecker.root, EnumSet.copyOf( Arrays.asList( flags ) ) );
	}

	private static <ContainerClass extends ContainerWithValue<ContainerClass, Object, ?>> void enforce( @Nonnull ContainerClass container, @Nonnull Node node, @Nonnull EnumSet<CheckerFlag> flags ) throws ParcelException.Error
	{
		if ( flags.contains( CheckerFlag.IGNORE_OVERFLOW ) )
			return;

		Streams.forEachWithException( container.getChildren(), child -> {
			Node otherNode = node.getChild( child.getLocalName() );
			if ( otherNode == null )
				throw new ParcelException.Error( "The path " + child.getCurrentPath() + " is not permitted!" );
			enforce( otherNode, child, flags );
		} );
	}

	private static <ContainerClass extends ContainerWithValue<ContainerClass, Object, ?>> void enforce( @Nonnull Node node, @Nonnull ContainerClass container, @Nonnull EnumSet<CheckerFlag> flags ) throws ParcelException.Error
	{
		Voluntary value = container.getValue();
		if ( node.getValueFlag() == ValueFlag.DENIED && value.isPresent() )
			throw new ParcelException.Error( "The path " + node.getCurrentPath() + " had a value, however, this is NOT PERMITTED!" );
		if ( node.getValueFlag() == ValueFlag.REQUIRED && !value.isPresent() )
			if ( node.hasDefault() )
				container.setValue( node.getDefault() );
			else
				throw new ParcelException.Error( "The path " + node.getCurrentPath() + " has no value and one is REQUIRED!" );
		if ( !flags.contains( CheckerFlag.IGNORE_TYPE_MISMATCH ) && !node.getValueType().isType( value.orElse( null ) ) )
			throw new ParcelException.Error( "The path " + node.getCurrentPath() + " was expected to be type " + node.getValueType().name() + " but found {" + container.getValue().orElse( "null" ) + "} instead." );

		Streams.forEachWithException( node.getChildren(), child -> {
			ContainerWithValue otherChild = container.getChild( child.getLocalName() );
			if ( otherChild == null && !flags.contains( CheckerFlag.IGNORE_MISSING ) )
				throw new ParcelException.Error( "The path " + child.getCurrentPath() + " was missing!" );
			else if ( otherChild != null )
				enforce( child, otherChild, flags );
		} );
	}

	private final Node root = new Node();

	public ParcelChecker()
	{

	}

	public void setValueType( @Nonnull String path, @Nonnull ValueType valueType, @Nullable Object def ) throws ParcelException.Error
	{
		root.getChildOrCreate( path ).setValueType( valueType, def );
	}

	public enum CheckerFlag
	{
		/**
		 * Do not throw an exception for additional unspecified keys and values.
		 */
		IGNORE_OVERFLOW,
		/**
		 * Do not throw an exception for missing keys and values.
		 */
		IGNORE_MISSING,
		/**
		 * Do not throw an exception for value type mismatch.
		 */
		IGNORE_TYPE_MISMATCH,
	}

	public enum ValueFlag
	{
		/**
		 * Require a value to be set
		 */
		REQUIRED,
		/**
		 * Allow it but don't complain if missing
		 */
		ALLOW,
		/**
		 * Deny any value from being set
		 */
		DENIED,
	}

	public enum ValueType
	{
		BOOLEAN,
		COLOR,
		DOUBLE,
		INTEGER,
		STRING_LIST,
		INTEGER_LIST,
		LONG,
		STRING,
		NULL;

		public boolean isType( @Nullable Object def )
		{
			ValueTypesTrait tester = () -> VoluntaryWithCause.ofWithCause( def );
			Voluntary result = VoluntaryWithCause.emptyWithCause();

			if ( this == BOOLEAN )
				return tester.getBoolean().isPresent();
			else if ( this == COLOR )
				result = tester.getColor();
			else if ( this == DOUBLE )
				return tester.getDouble().isPresent();
			else if ( this == INTEGER )
				return tester.getInteger().isPresent();
			else if ( this == STRING_LIST )
				result = tester.getList( String.class );
			else if ( this == INTEGER_LIST )
				result = tester.getList( Integer.class );
			else if ( this == LONG )
				return tester.getLong().isPresent();
			else if ( this == STRING )
				result = tester.getString();
			else if ( this == NULL )
				return def == null;

			return result.hasSucceeded();
		}
	}

	protected class Node extends ContainerBase<Node, ParcelException.Error>
	{
		private Object def = null;
		private ValueFlag valueFlag = ValueFlag.DENIED;
		private ValueType valueType = null;

		protected Node()
		{
			super( Node::new );
		}

		protected Node( @Nonnull String localName ) throws ParcelException.Error
		{
			super( Node::new, localName );
		}

		protected Node( @Nullable Node parent, @Nonnull String localName ) throws ParcelException.Error
		{
			super( Node::new, parent, localName );
		}

		public Object getDefault()
		{
			if ( valueType == ValueType.NULL || valueFlag == ValueFlag.DENIED )
				return null;
			return def;
		}

		@Override
		protected ParcelException.Error getException( @Nonnull String message, @Nullable Exception exception )
		{
			return new ParcelException.Error( message, exception );
		}

		public ValueFlag getValueFlag()
		{
			return valueFlag;
		}

		public void setValueFlag( ValueFlag valueFlag )
		{
			if ( valueFlag == ValueFlag.DENIED )
			{
				valueType = null;
				def = null;
			}

			this.valueFlag = valueFlag;
		}

		public ValueType getValueType()
		{
			return valueType;
		}

		public boolean hasDefault()
		{
			return getDefault() != null;
		}

		@Override
		protected boolean isTrimmable0()
		{
			return false;
		}

		public void setValueType( @Nonnull ValueType valueType, @Nullable Object def ) throws ParcelException.Error
		{
			if ( !valueType.isType( def ) )
				throw getException( "Default value \"" + def + "\" does not match the ValueType \"" + valueType.name() + "\"", null );

			this.valueType = valueType;
			this.def = def;
			if ( valueFlag == ValueFlag.DENIED )
				valueFlag = ValueFlag.ALLOW;
		}
	}
}
