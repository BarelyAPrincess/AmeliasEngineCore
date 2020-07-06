/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.scripting.parsers;

import com.chiorichan.utils.UtilIO;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.amelia.logging.LogBuilder;

public class LessImportParser extends BasicParser
{
	File rootDir = null;

	public LessImportParser()
	{
		super( "@import[: ]*(.*);", "(@import[: ]*.*;)" );
	}

	@Override
	public String resolveMethod( String... args ) throws Exception
	{
		File imp = UtilIO.isAbsolute( args[0] ) || args[0].startsWith( "\\" ) || rootDir == null ? new File( args[0] ) : new File( rootDir, args[0] );

		try
		{
			return FileUtils.readFileToString( imp );
		}
		catch ( IOException e )
		{
			LogBuilder.get( "ScriptFactory" ).warning( "Attempted to import file '" + imp.getName() + "' but got error '" + e.getMessage() + "'" );
			return "/* Attempted to import file '" + imp.getName() + "' but got error '" + e.getMessage() + "' */";
		}
	}

	public String runParser( String source, File rootDir ) throws Exception
	{
		this.rootDir = rootDir;
		return runParser( source );
	}
}
