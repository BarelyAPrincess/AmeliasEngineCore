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

import io.amelia.extra.UtilityObjects;

@FunctionalInterface
public interface BiFunctionWithException<T, U, R, E extends Exception>
{
	/**
	 * Returns a composed function that first applies this function to
	 * its input, and then applies the {@code after} function to the result.
	 * If evaluation of either function throws an exception, it is relayed to
	 * the caller of the composed function.
	 *
	 * @param <V>   the type of output of the {@code after} function, and of the
	 *              composed function
	 * @param after the function to apply after this function is applied
	 *
	 * @return a composed function that first applies this function and then
	 * applies the {@code after} function
	 *
	 * @throws NullPointerException if after is null
	 */
	default <V> io.amelia.support.BiFunctionWithException<T, U, V, E> andThen( io.amelia.support.FunctionWithException<? super R, ? extends V, E> after ) throws E
	{
		UtilityObjects.isNotNull( after );
		return ( T t, U u ) -> after.apply( apply( t, u ) );
	}

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t the first function argument
	 * @param u the second function argument
	 *
	 * @return the function result
	 */
	R apply( T t, U u ) throws E;
}