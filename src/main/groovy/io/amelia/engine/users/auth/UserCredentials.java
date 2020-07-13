/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.users.auth;

import javax.annotation.Nonnull;

import io.amelia.foundation.EntityPrincipal;
import io.amelia.foundation.Foundation;
import io.amelia.lang.ReportingLevel;
import io.amelia.users.DescriptiveReason;
import io.amelia.users.UserException;
import io.amelia.users.UserPermissible;

/**
 * Provides login credentials to the {@link UserAuthenticator}
 */
public abstract class UserCredentials
{
	private final DescriptiveReason descriptiveReason;
	private final UserAuthenticator userAuthenticator;
	private final EntityPrincipal userPrincipal;

	UserCredentials( UserAuthenticator userAuthenticator, EntityPrincipal userPrincipal, DescriptiveReason descriptiveReason )
	{
		this.userAuthenticator = userAuthenticator;
		this.userPrincipal = userPrincipal;
		this.descriptiveReason = descriptiveReason;
	}

	public final DescriptiveReason getDescriptiveReason()
	{
		return descriptiveReason;
	}

	public final EntityPrincipal getUser()
	{
		return userPrincipal;
	}

	public final UserAuthenticator getUserAuthenticator()
	{
		return userAuthenticator;
	}

	/**
	 * Saves credentials to the session for later retrieval.
	 *
	 * @param user The UserPermissible (A wrapper class of the session) to store the credentials
	 *
	 * @throws UserException.Error If there are issues handling the account
	 */
	public void saveCredentialsToSession( @Nonnull UserPermissible user ) throws UserException.Error
	{
		if ( user.getEntity() != userPrincipal )
			throw new UserException.Error( userPrincipal, ReportingLevel.L_ERROR, "These credentials don't match the provided permissible." );

		if ( !descriptiveReason.getReportingLevel().isSuccess() )
			throw new UserException.Error( userPrincipal, ReportingLevel.L_DENIED, "Can't save credentials unless they were successful." );

		if ( Foundation.isNullEntity( userPrincipal ) )
			throw new UserException.Error( userPrincipal, ReportingLevel.L_SECURITY, "These credentials can't be saved." );

		if ( "token".equals( user.getVariable( "auth" ) ) && user.getVariable( "token" ) != null )
			UserAuthenticator.TOKEN.deleteToken( user.getVariable( "userId" ), user.getVariable( "token" ) );

		user.setVariable( "auth", "token" );
		user.setVariable( "uuid", userPrincipal.uuid().toString() );
		user.setVariable( "token", UserAuthenticator.TOKEN.issueToken( userPrincipal ) );
	}
}
