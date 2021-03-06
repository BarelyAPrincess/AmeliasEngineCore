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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class VoluntaryBoolean
{
	/**
	 * Common instance for {@code empty()}.
	 */
	private static final io.amelia.support.VoluntaryBoolean EMPTY = new io.amelia.support.VoluntaryBoolean();

	/**
	 * Returns an empty {@code OptionalInt} instance.  No value is present for this
	 * OptionalInt.
	 *
	 * @return an empty {@code OptionalInt}
	 *
	 * @apiNote Though it may be tempting to do so, avoid testing if an object
	 * is empty by comparing with {@code ==} against instances returned by
	 * {@code Option.empty()}. There is no guarantee that it is a singleton.
	 * Instead, use {@link #isPresent()}.
	 */
	public static io.amelia.support.VoluntaryBoolean empty()
	{
		return EMPTY;
	}

	/**
	 * Return an {@code OptionalInt} with the specified value present.
	 *
	 * @param value the value to be present
	 *
	 * @return an {@code OptionalInt} with the value present
	 */
	public static io.amelia.support.VoluntaryBoolean of( Boolean value )
	{
		return new io.amelia.support.VoluntaryBoolean( value );
	}

	/**
	 * Returns an {@code Optional} describing the specified value, if non-null,
	 * otherwise returns an empty {@code Optional}.
	 *
	 * @param value the possibly-null value to describe
	 *
	 * @return an {@code Optional} with a present value if the specified value
	 * is non-null, otherwise an empty {@code Optional}
	 */
	public static io.amelia.support.VoluntaryBoolean ofNullable( Boolean value )
	{
		return value == null ? empty() : of( value );
	}

	private final Boolean value;

	/**
	 * Constructs an instance with the value present.
	 *
	 * @param value the non-null value to be present
	 *
	 * @throws NullPointerException if value is null
	 */
	private VoluntaryBoolean( Boolean value )
	{
		this.value = Objects.requireNonNull( value );
	}

	/**
	 * Construct an empty instance.
	 *
	 * @implNote Generally only one empty instance, {@link io.amelia.support.VoluntaryBoolean#EMPTY},
	 * should exist per VM.
	 */
	private VoluntaryBoolean()
	{
		this.value = null;
	}

	/**
	 * Indicates whether some other object is "equal to" this OptionalInt. The
	 * other object is considered equal if:
	 * <ul>
	 * <li>it is also an {@code OptionalInt} and;
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
			return true;

		if ( !( obj instanceof io.amelia.support.VoluntaryBoolean ) )
			return false;

		io.amelia.support.VoluntaryBoolean other = ( io.amelia.support.VoluntaryBoolean ) obj;
		return ( isPresent() && other.isPresent() ) ? value == other.value : isPresent() == other.isPresent();
	}

	/**
	 * If a value is present, and the value matches the given predicate,
	 * return an {@code Optional} describing the value, otherwise return an
	 * empty {@code Optional}.
	 *
	 * @param predicate a predicate to apply to the value, if present
	 *
	 * @return an {@code Optional} describing the value of this {@code Optional}
	 * if a value is present and the value matches the given predicate,
	 * otherwise an empty {@code Optional}
	 *
	 * @throws NullPointerException if the predicate is null
	 */
	public io.amelia.support.VoluntaryBoolean filter( Predicate<Boolean> predicate )
	{
		Objects.requireNonNull( predicate );
		if ( !isPresent() )
			return this;
		else
			return predicate.test( value ) ? this : empty();
	}

	/**
	 * If a value is present, apply the provided {@code Optional}-bearing
	 * mapping function to it, return that result, otherwise return an empty
	 * {@code Optional}.  This method is similar to {@link #map(Function)},
	 * but the provided mapper is one whose result is already an {@code Optional},
	 * and if invoked, {@code flatMap} does not wrap it with an additional
	 * {@code Optional}.
	 *
	 * @param <U>    The type parameter to the {@code Optional} returned by
	 * @param mapper a mapping function to apply to the value, if present
	 *               the mapping function
	 *
	 * @return the result of applying an {@code Optional}-bearing mapping
	 * function to the value of this {@code Optional}, if a value is present,
	 * otherwise an empty {@code Optional}
	 *
	 * @throws NullPointerException if the mapping function is null or returns
	 *                              a null result
	 */
	public <U> Optional<U> flatMap( Function<Boolean, Optional<U>> mapper )
	{
		Objects.requireNonNull( mapper );
		if ( !isPresent() )
			return Optional.empty();
		else
			return Objects.requireNonNull( mapper.apply( value ) );
	}

	/**
	 * If a value is present in this {@code OptionalInt}, returns the value,
	 * otherwise throws {@code NoSuchElementException}.
	 *
	 * @return the value held by this {@code OptionalInt}
	 *
	 * @throws NoSuchElementException if there is no value present
	 * @see io.amelia.support.VoluntaryBoolean#isPresent()
	 */
	public Boolean get()
	{
		if ( !isPresent() )
			throw new NoSuchElementException( "No value present" );
		return value;
	}

	public String getString()
	{
		return value == null ? "empty" : value ? "true" : "false";
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
		return Objects.hashCode( value );
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
	public void ifPresent( Consumer<Boolean> consumer )
	{
		if ( isPresent() )
			consumer.accept( value );
	}

	/**
	 * Return {@code true} if there is a value present, otherwise {@code false}.
	 *
	 * @return {@code true} if there is a value present, otherwise {@code false}
	 */
	public boolean isPresent()
	{
		return value != null;
	}

	/**
	 * If a value is present, apply the provided mapping function to it,
	 * and if the result is non-null, return an {@code Optional} describing the
	 * result.  Otherwise return an empty {@code Optional}.
	 *
	 * @param <U>    The type of the result of the mapping function
	 * @param mapper a mapping function to apply to the value, if present
	 *
	 * @return an {@code Optional} describing the result of applying a mapping
	 * function to the value of this {@code Optional}, if a value is present,
	 * otherwise an empty {@code Optional}
	 *
	 * @throws NullPointerException if the mapping function is null
	 * @apiNote This method supports post-processing on optional values, without
	 * the need to explicitly check for a return status.  For example, the
	 * following code traverses a stream of file names, selects one that has
	 * not yet been processed, and then opens that file, returning an
	 * {@code Optional<FileInputStream>}:
	 * <p>
	 * <pre>{@code
	 *     Optional<FileInputStream> fis =
	 *         names.stream().filter(name -> !isProcessedYet(name))
	 *                       .findFirst()
	 *                       .map(name -> new FileInputStream(name));
	 * }</pre>
	 * <p>
	 * Here, {@code findFirst} returns an {@code Optional<String>}, and then
	 * {@code map} returns an {@code Optional<FileInputStream>} for the desired
	 * file if one exists.
	 */
	public <U> Optional<U> map( Function<Boolean, ? extends U> mapper )
	{
		Objects.requireNonNull( mapper );
		if ( !isPresent() )
			return Optional.empty();
		else
		{
			return Optional.ofNullable( mapper.apply( value ) );
		}
	}

	/**
	 * Return the value if present, otherwise return {@code other}.
	 *
	 * @param other the value to be returned if there is no value present
	 *
	 * @return the value, if present, otherwise {@code other}
	 */
	public Boolean orElse( Boolean other )
	{
		return isPresent() ? value : other;
	}

	/**
	 * Return the value if present, otherwise invoke {@code other} and return
	 * the result of that invocation.
	 *
	 * @param other a {@code IntSupplier} whose result is returned if no value
	 *              is present
	 *
	 * @return the value if present otherwise the result of {@code other.getAsInt()}
	 *
	 * @throws NullPointerException if value is not present and {@code other} is
	 *                              null
	 */
	public Boolean orElseGet( Supplier<Boolean> other )
	{
		return isPresent() ? value : other.get();
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
	public <X extends Throwable> Boolean orElseThrow( Supplier<X> exceptionSupplier ) throws X
	{
		if ( isPresent() )
			return value;
		else
			throw exceptionSupplier.get();
	}

	/**
	 * {@inheritDoc}
	 * <p>
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
		return isPresent() ? String.format( "VoluntaryBoolean[%s]", value ) : "VoluntaryBoolean.empty";
	}
}
