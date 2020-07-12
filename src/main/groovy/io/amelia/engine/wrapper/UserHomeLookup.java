package io.amelia.engine.wrapper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class UserHomeLookup
{
	public static final String DEFAULT_USER_HOME = System.getProperty( "user.home" ) + "/.engine";
	public static final String USER_HOME_PROPERTY_KEY = "engine.user.home";
	public static final String USER_HOME_ENV_KEY = "ENGINE_USER_HOME";

	public static Path getUserHome()
	{
		String gradleUserHome;
		if ( ( gradleUserHome = System.getProperty( USER_HOME_PROPERTY_KEY ) ) != null )
			return Paths.get( gradleUserHome );
		if ( ( gradleUserHome = System.getenv( USER_HOME_ENV_KEY ) ) != null )
			return Paths.get( gradleUserHome );
		return Paths.get( DEFAULT_USER_HOME );
	}
}
