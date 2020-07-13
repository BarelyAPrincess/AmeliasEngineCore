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

import com.chiorichan.ContentTypes;
import com.chiorichan.ShellOverrides;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.Charsets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import io.amelia.engine.config.ConfigRegistry;
import io.amelia.foundation.ConfigRegistry;
import io.amelia.logging.LogBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import joptsimple.internal.Strings;

public class FileInterpreter
{
	public static String determineShellFromName( String fileName )
	{
		fileName = fileName.toLowerCase();

		String shell = ShellOverrides.getShellForExt( ShellOverrides.getFileExtension( fileName ) );

		if ( shell == null || shell.isEmpty() )
			return ShellOverrides.getFileExtension( fileName );

		return shell;
	}

	public static String readLine( ByteBuf buf )
	{
		if ( !buf.isReadable() || buf.readableBytes() < 1 )
			return null;

		String op = "";
		while ( buf.isReadable() && buf.readableBytes() > 0 )
		{
			byte bb = buf.readByte();
			if ( bb == '\n' )
				break;
			op += ( char ) bb;
		}
		return op;
	}

	protected Charset encoding = null;
	protected final Map<String, String> annotations = new TreeMap<>();
	protected ByteBuf data = Unpooled.buffer();
	protected File cachedFile = null;

	public FileInterpreter()
	{
		encoding = Charsets.toCharset( ConfigRegistry.config.getString( "server.defaultBinaryEncoding" ).orElse( "ISO-8859-1" ) );

		// All param keys are lower case. No such thing as a non-lowercase param keys because keys are forced to lowercase.
		annotations.put( "title", null );
		annotations.put( "reqlogin", null );
		annotations.put( "reqperm", "-1" );

		// Shell Options (groovy,text,html)
		annotations.put( "shell", null );
		annotations.put( "encoding", encoding.name() );
	}

	public FileInterpreter( File file ) throws IOException
	{
		this();
		interpretParamsFromFile( file );
	}

	public byte[] consumeBytes()
	{
		byte[] bytes = new byte[data.readableBytes()];
		int inx = data.readerIndex();
		data.readBytes( bytes );
		data.readerIndex( inx );
		return bytes;
	}

	public String consumeString()
	{
		return new String( consumeBytes(), encoding );
	}

	public String get( String key )
	{
		if ( !annotations.containsKey( key.toLowerCase() ) )
			return null;

		return annotations.get( key.toLowerCase() );
	}

	public Map<String, String> getAnnotations()
	{
		return annotations;
	}

	public String getContentType()
	{
		if ( cachedFile == null )
			return "text/html";

		String type = get( "contenttype" );

		if ( type == null || type.isEmpty() )
			type = ContentTypes.getContentType( cachedFile.getAbsoluteFile() );

		if ( type.startsWith( "text" ) )
			setEncoding( Charsets.toCharset( ConfigRegistry.config.getString( "server.defaultTextEncoding" ).orElse( "UTF-8" ) ) );
		else
			setEncoding( Charsets.toCharset( ConfigRegistry.config.getString( "server.defaultBinaryEncoding" ).orElse( "ISO-8859-1" ) ) );

		return type;
	}

	public Charset getEncoding()
	{
		return encoding;
	}

	public String getEncodingName()
	{
		return encoding.name();
	}

	public File getFile()
	{
		return cachedFile;
	}

	public String getFilePath()
	{
		if ( cachedFile == null )
			return null;

		return cachedFile.getAbsolutePath();
	}

	public boolean hasFile()
	{
		return cachedFile != null;
	}

	public final void interpretParamsFromFile( File file ) throws IOException
	{
		if ( file == null )
			throw new FileNotFoundException( "File path was null" );

		FileInputStream is = null;
		try
		{
			cachedFile = file;

			annotations.put( "file", file.getAbsolutePath() );

			if ( file.isDirectory() )
				annotations.put( "shell", "embedded" );
			else
			{
				if ( !annotations.containsKey( "shell" ) || annotations.get( "shell" ) == null )
				{
					String shell = determineShellFromName( file.getName() );
					if ( shell != null && !shell.isEmpty() )
						annotations.put( "shell", shell );
				}

				is = new FileInputStream( file );

				ByteBuf buf = Unpooled.wrappedBuffer( IOUtils.toByteArray( is ) );
				boolean beginContent = false;
				int lastInx;
				int lineCnt = 0;

				data = Unpooled.buffer();

				do
				{
					lastInx = buf.readerIndex();
					String l = readLine( buf );
					if ( l == null )
						break;

					if ( l.trim().startsWith( "@" ) )
						try
						{
							lineCnt++;

							/* Only solution I could think of for CSS files since they use @annotations too, so we share them. */
							if ( "text/css".equalsIgnoreCase( ContentTypes.getContentType( file ) ) )
								data.writeBytes( ( l + "\n" ).getBytes() );

							String key;
							String val = Strings.EMPTY;

							if ( l.contains( " " ) )
							{
								key = l.trim().substring( 1, l.trim().indexOf( " " ) );
								val = l.trim().substring( l.trim().indexOf( " " ) + 1 );
							}
							else
								key = l;

							if ( val.endsWith( ";" ) )
								val = val.substring( 0, val.length() - 1 );

							if ( val.startsWith( "'" ) && val.endsWith( "'" ) )
								val = val.substring( 1, val.length() - 1 );

							annotations.put( key.toLowerCase(), val );
							L.finer( "Setting param '" + key + "' to '" + val + "'" );

							if ( key.equals( "encoding" ) )
								if ( Charset.isSupported( val ) )
									setEncoding( Charsets.toCharset( val ) );
								else
									L.severe( "The file '" + file.getAbsolutePath() + "' requested encoding '" + val + "' but it's not supported by the JVM!" );
						}
						catch ( NullPointerException | ArrayIndexOutOfBoundsException e )
						{
							// Ignore
						}
					else if ( l.trim().isEmpty() )
						lineCnt++;
						// Continue reading, this line is empty.
					else
					{
						// We encountered the beginning of the file content.
						beginContent = true;
						buf.readerIndex( lastInx ); // This rewinds the buffer to the last reader index
					}
				}
				while ( !beginContent );

				data.writeBytes( Strings.repeat( '\n', lineCnt ).getBytes() );

				data.writeBytes( buf );
			}
		}
		finally
		{
			if ( is != null )
				is.close();
		}
	}

	public void put( String key, String value )
	{
		annotations.put( key.toLowerCase(), value );
	}

	public void setEncoding( Charset encoding )
	{
		this.encoding = encoding;
		annotations.put( "encoding", encoding.name() );
	}

	@Override
	public String toString()
	{
		String overrides = "";

		for ( Entry<String, String> o : annotations.entrySet() )
			overrides += "," + o.getKey() + "=" + o.getValue();

		if ( overrides.length() > 1 )
			overrides = overrides.substring( 1 );

		String cachedFileStr = cachedFile == null ? "N/A" : cachedFile.getAbsolutePath();

		return "FileInterpreter{content=" + data.writerIndex() + " bytes,file=" + cachedFileStr + ",overrides={" + overrides + "}}";
	}
}
