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

import java.util.NoSuchElementException;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.amelia.extra.UtilityObjects;

/**
 * A container object which may or may not contain a {@code long} value.
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code getAsLong()} will return the value.
 *
 * <p>Additional methods that depend on the presence or absence of a contained
 * value are provided, such as {@link #orElse(long) orElse()}
 * (return a default value if value not present) and
 * {@link #ifPresent(LongConsumer) ifPresent()} (execute a block
 * of code if the value is present).
 *
 * <p>This is a <a href="../lang/doc-files/ValueBased.html">value-based</a>
 * class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of
 * {@code VoluntaryLong} may have unpredictable results and should be avoided.
 */
public final class VoluntaryLong
{
	/**
	 * Common instance for {@code empty()}.
	 */
	private static final io.amelia.support.VoluntaryLong EMPTY = new io.amelia.support.VoluntaryLong();

	/**
	 * Returns an empty {@code VoluntaryLong} instance.  No value is present for this
	 * VoluntaryLong.
	 *
	 * @return an empty {@code VoluntaryLong}.
	 *
	 * @apiNote Though it may be tempting to do so, avoid testing if an object
	 * is empty by comparing with {@code ==} against instances returned by
	 * {@code Option.empty()}. There is no guarantee that it is a singleton.
	 * Instead, use {@link #isPresent()}.
	 */
	public static io.amelia.support.VoluntaryLong empty()
	{
		return EMPTY;
	}

	/**
	 * Return an {@code VoluntaryLong} with the specified value present.
	 *
	 * @param value the value to be present
	 *
	 * @return an {@code VoluntaryLong} with the value present
	 */
	public static io.amelia.support.VoluntaryLong of( long value )
	{
		return new io.amelia.support.VoluntaryLong( value );
	}

	public static io.amelia.support.VoluntaryLong of( OptionalLong value )
	{
		return value.isPresent() ? of( value.getAsLong() ) : empty();
	}

	/**
	 * If true then the value is present, otherwise indicates no value is present
	 */
	private final boolean isPresent;
	private final long value;

	/**
	 * Construct an empty instance.
	 *
	 * @implNote generally only one empty instance, {@link io.amelia.support.VoluntaryLong#EMPTY},
	 * should exist per VM.
	 */
	private VoluntaryLong()
	{
		this.isPresent = false;
		this.value = 0;
	}

	/**
	 * Construct an instance with the value present.
	 *
	 * @param value the long value to be present
	 */
	private VoluntaryLong( long value )
	{
		this.isPresent = true;
		this.value = value;
	}

	/**
	 * Indicates whether some other object is "equal to" this VoluntaryLong. The
	 * other object is considered equal if:
	 * <ul>
	 * <li>it is also an {@code VoluntaryLong} and;
	 * <li>both instances have no value present or;
	 * <li>the present values are "equal to" each other via {@code ==}.
	 * </ul>
	 *
	 * @param obj an object to be tested for equality
	 *
	 * @return {code true} if the other object is "equal to" this object
	 * otherwise {@code false}
	 */
	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
		{
			return true;
		}

		if ( !( obj instanceof io.amelia.support.VoluntaryLong ) )
		{
			return false;
		}

		io.amelia.support.VoluntaryLong other = ( io.amelia.support.VoluntaryLong ) obj;
		return ( isPresent && other.isPresent ) ? value == other.value : isPresent == other.isPresent;
	}

	public io.amelia.support.VoluntaryLong filter( Predicate<Long> predicate )
	{
		UtilityObjects.notNull( predicate );
		if ( !isPresent() )
			return this;
		else
			return predicate.test( value ) ? this : empty();
	}

	public io.amelia.support.VoluntaryLong flatMap( Function<Long, OptionalLong> function )
	{
		UtilityObjects.notNull( function );
		return !this.isPresent() ? empty() : of( UtilityObjects.notNull( function.apply( value ) ) );
	}

	/**
	 * If a value is present in this {@code VoluntaryLong}, returns the value,
	 * otherwise throws {@code NoSuchElementException}.
	 *
	 * @return the value held by this {@code VoluntaryLong}
	 *
	 * @throws NoSuchElementException if there is no value present
	 * @see io.amelia.support.VoluntaryLong#isPresent()
	 */
	public long getAsLong()
	{
		if ( !isPresent )
			throw new NoSuchElementException( "No value present" );
		return value;
	}

	/**
	 * Returns the hash code value of the present value, if any, or 0 (zero) if
	 * no value is present.
	 *
	 * @return hash code value of the present value or 0 if no value is present
	 */
	@Override
	public int hashCode()
	{
		return isPresent ? Long.hashCode( value ) : 0;
	}

	/**
	 * Have the specified consumer accept the value if a value is present,
	 * otherwise do nothing.
	 *
	 * @param consumer block to be executed if a value is present
	 *
	 * @throws NullPointerException if value is present and {@code consumer} is
	 *                              null
	 */
	public void ifPresent( LongConsumer consumer )
	{
		if ( isPresent )
			consumer.accept( value );
	}

	/**
	 * Return {@code true} if there is a value present, otherwise {@code false}.
	 *
	 * @return {@code true} if there is a value present, otherwise {@code false}
	 */
	public boolean isPresent()
	{
		return isPresent;
	}

	public io.amelia.support.VoluntaryLong map( Function<Long, Long> function )
	{
		UtilityObjects.notNull( function );
		return !this.isPresent() ? empty() : of( function.apply( value ) );
	}

	public OptionalLong optionalLong()
	{
		return OptionalLong.of( value );
	}

	/**
	 * Return the value if present, otherwise return {@code other}.
	 *
	 * @param other the value to be returned if there is no value present
	 *
	 * @return the value, if present, otherwise {@code other}
	 */
	public long orElse( long other )
	{
		return isPresent ? value : other;
	}

	/**
	 * Return the value if present, otherwise invoke {@code other} and return
	 * the result of that invocation.
	 *
	 * @param other a {@code LongSupplier} whose result is returned if no value
	 *              is present
	 *
	 * @return the value if present otherwise the result of {@code other.getAsLong()}
	 *
	 * @throws NullPointerException if value is not present and {@code other} is
	 *                              null
	 */
	public long orElseGet( LongSupplier other )
	{
		return isPresent ? value : other.getAsLong();
	}

	/**
	 * Return the contained value, if present, otherwise throw an exception
	 * to be created by the provided supplier.
	 *
	 * @param <X>               Type of the exception to be thrown
	 * @param exceptionSupplier The supplier which will return the exception to
	 *                          be thrown
	 *
	 * @return the present value
	 *
	 * @throws X                    if there is no value present
	 * @throws NullPointerException if no value is present and
	 *                              {@code exceptionSupplier} is null
	 * @apiNote A method reference to the exception constructor with an empty
	 * argument list can be used as the supplier. For example,
	 * {@code IllegalStateException::new}
	 */
	public <X extends Throwable> long orElseThrow( Supplier<X> exceptionSupplier ) throws X
	{
		if ( isPresent )
		{
			return value;
		}
		else
		{
			throw exceptionSupplier.get();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns a non-empty string representation of this object suitable for
	 * debugging. The exact presentation format is unspecified and may vary
	 * between implementations and versions.
	 *
	 * @return the string representation of this instance
	 *
	 * @implSpec If a value is present the result must include its string
	 * representation in the result. Empty and present instances must be
	 * unambiguously differentiable.
	 */
	@Override
	public String toString()
	{
		return isPresent ? String.format( "VoluntaryLong[%s]", value ) : "VoluntaryLong.empty";
	}
}
