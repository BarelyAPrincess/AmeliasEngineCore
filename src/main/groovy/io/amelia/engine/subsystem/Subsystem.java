package io.amelia.engine.subsystem;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.amelia.engine.subsystem.log.EngineLogger;

/**
 * A class for maintaining build-in subsystems such as file storage
 */
public class Subsystem
{
	public static final EngineLogger L = EngineLogger.getLogger( EngineLogger.GLOBAL_LOGGER_NAME );

	public static final String PATH_APP = "__app";
	public static final String PATH_CACHE = "__cache";
	public static final String PATH_LOGS = "__logs";
	public static final String PATH_LIBS = "__libs";
	public static final String PATH_CONFIG = "__config";
	public static final String PATH_PLUGINS = "__plugins";
	public static final String PATH_UPDATES = "__updates";
	public static final String PATH_STORAGE = "__storage";
	private static Path appPath;

	private static final Map<String, List<String>> APP_PATHS = new ConcurrentHashMap<>();

	static
	{
		setPath( PATH_CACHE, PATH_STORAGE, "cache" );
		setPath( PATH_LOGS, PATH_STORAGE, "logs" );
		setPath( PATH_LIBS, PATH_APP, "libs" );
		setPath( PATH_CONFIG, PATH_APP, "config" );
		setPath( PATH_PLUGINS, PATH_APP, "plugins" );
		setPath( PATH_UPDATES, PATH_APP, "updates" );
		setPath( PATH_STORAGE, PATH_APP, "storage" );
	}
}
