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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.amelia.extra.UtilityMaps;
import io.amelia.foundation.ConfigRegistry;
import io.amelia.foundation.Kernel;
import io.amelia.extra.UtilityIO;
import io.amelia.extra.UtilityObjects;
import io.amelia.extra.UtilityStrings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FileContext
{
	public static final Charset DEFAULT_BINARY_CHARSET = Charset.forName( ConfigRegistry.config.getString( ConfigRegistry.ConfigKeys.DEFAULT_BINARY_CHARSET ) );
	public static final Charset DEFAULT_TEXT_CHARSET = Charset.forName( ConfigRegistry.config.getString( ConfigRegistry.ConfigKeys.DEFAULT_TEXT_CHARSET ) );

	public static io.amelia.support.FileContext fromFile( Path path ) throws IOException
	{
		io.amelia.support.FileContext context = new io.amelia.support.FileContext();
		context.readFromFile( path );
		return context;
	}

	protected final Map<String, String> metaValues = new TreeMap<>();
	protected ByteBuf content = Unpooled.buffer();
	private Charset charset = null;
	private String contentType = null;
	private String ext = null;
	private Path filePath = null;
	@UtilityMaps.Key( "reqlogin" )
	private boolean reqLogin = false;
	@UtilityMaps.Key( "reqperm" )
	private String reqPerm = null;
	@UtilityMaps.Key( "title" )
	private String title = null;

	public Map<String, String> getAnnotations()
	{
		return UtilityMaps.objectToStringMap( this );
	}

	public Charset getCharset()
	{
		if ( charset == null )
		{
			if ( contentType.startsWith( "text" ) )
				charset = DEFAULT_TEXT_CHARSET;
			else
				charset = DEFAULT_BINARY_CHARSET;
		}

		return charset;
	}

	@UtilityMaps.Key( "charset" )
	public String getCharsetName()
	{
		return UtilityObjects.ifPresentGet( getCharset(), Charset::name );
	}

	public byte[] getContentBytes()
	{
		byte[] bytes = new byte[content.readableBytes()];
		content.getBytes( content.readerIndex(), bytes );
		return bytes;
	}

	public String getContentString()
	{
		return new String( getContentBytes(), charset );
	}

	@UtilityMaps.Key( "contenttype" )
	public String getContentType()
	{
		if ( contentType == null && filePath != null )
			contentType = ContentTypes.getContentTypes( filePath ).findFirst().orElse( null );

		if ( contentType == null )
			contentType = "application/octet-stream";

		getCharset();

		return contentType;
	}

	@UtilityMaps.Key( "ext" )
	public String getExt()
	{
		if ( ext == null )
			ext = ExtTypes.getExtTypes( filePath ).findFirst().orElse( null );
		return ext;
	}

	public Path getFilePath()
	{
		return filePath;
	}

	@UtilityMaps.Key( "file" )
	public String getFilePathRel( Path relTo )
	{
		return UtilityIO.relPath( filePath, relTo );
	}

	public String getMetaValue( @Nonnull String key )
	{
		return metaValues.get( key.toLowerCase() );
	}

	public String getTitle()
	{
		return title;
	}

	public boolean hasFilePath()
	{
		return filePath != null && Files.exists( filePath );
	}

	public void putMetaValue( @Nonnull String key, @Nullable String value )
	{
		key = key.toLowerCase();

		if ( key.equals( "charset" ) )
			setCharset( value );
		else if ( key.equals( "contentType" ) )
			contentType = value;
		else if ( key.equals( "ext" ) )
			ext = value;
		else if ( key.equals( "reqlogin" ) )
			reqLogin = UtilityObjects.castToBoolean( value );
		else if ( key.equals( "reqperm" ) )
			reqPerm = value;
		else if ( key.equals( "title" ) )
			title = value;
		else if ( UtilityObjects.isEmpty( value ) )
			metaValues.remove( key );
		else
			metaValues.put( key, value );
	}

	public void readFromFile( Path filePath ) throws IOException
	{
		if ( !Files.exists( filePath ) )
			throw new FileNotFoundException( "File must exist." );

		this.filePath = filePath;

		if ( Files.isDirectory( filePath ) )
		{
			ext = "directory";
			return;
		}

		InputStream in = null;
		try
		{
			in = Files.newInputStream( filePath );
			ByteBuf inBuf = UtilityIO.readStreamToByteBuf( in );
			ByteBuf outBuf = Unpooled.buffer();

			int lastInx;
			int lineCount = 0;

			for ( ; ; )
			{
				lastInx = inBuf.readerIndex();
				String line = UtilityIO.readLine( inBuf );
				if ( line == null )
					break;
				line = line.trim();

				if ( line.startsWith( "@" ) )
					try
					{
						// Temporary solution for CSS files since they too use @annotations - so we'll share them for now.
						if ( ContentTypes.isContentType( filePath, "css" ) )
							outBuf.writeBytes( ( line + "\n" ).getBytes() );
						else
							lineCount++;

						String key;
						String val = "";

						if ( line.contains( " " ) )
						{
							key = line.substring( 1, line.indexOf( " " ) );
							val = line.substring( line.indexOf( " " ) + 1 );
						}
						else
							key = line;

						if ( val.endsWith( ";" ) )
							val = val.substring( 0, val.length() - 1 );

						if ( val.startsWith( "\"" ) && val.endsWith( "\"" ) )
							val = val.substring( 1, val.length() - 1 );

						if ( val.startsWith( "'" ) && val.endsWith( "'" ) )
							val = val.substring( 1, val.length() - 1 );

						Kernel.L.fine( "Reading annotation " + key + " with value " + val + " for file " + UtilityIO.relPath( filePath ) );
						putMetaValue( key, val );
					}
					catch ( NullPointerException | ArrayIndexOutOfBoundsException e )
					{
						// Ignore
					}
				else if ( line.length() == 0 )
					lineCount++;
					// Continue, empty line
				else
				{
					// We've encountered the beginning of the content, notify the look that we can quit now.
					inBuf.readerIndex( lastInx ); // This rewinds the buffer to the last reader index
					break;
				}
			}

			// Write empty line returns so script exceptions still match up to their source file
			outBuf.writeBytes( UtilityStrings.repeat( "\n", lineCount ).getBytes() );

			// Write remaining data to output
			outBuf.writeBytes( inBuf );
			this.content = outBuf;
		}
		finally
		{
			UtilityIO.closeQuietly( in );
		}
	}

	public void setCharset( String charset )
	{
		if ( Charset.isSupported( charset ) )
			this.charset = Charset.forName( charset );
		else
			Kernel.L.warning( "The charset " + charset + " was set but it's not supported by the JVM!" );
	}
}
