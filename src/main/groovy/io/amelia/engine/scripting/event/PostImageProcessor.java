/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2017 Joel Greene <joel.greene@penoaks.com>
 * Copyright (c) 2017 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.engine.scripting.event;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import io.amelia.engine.config.ConfigRegistry;
import io.amelia.engine.scripting.ScriptingContext;
import io.amelia.extra.UtilityEncrypt;
import io.amelia.extra.UtilityIO;
import io.amelia.extra.UtilityObjects;
import io.amelia.support.EnumColor;
import io.netty.buffer.ByteBufInputStream;

/**
 * Applies special builtin image filters post {@link com.chiorichan.factory.ScriptingFactory} via {@link PostEvalEvent}
 *
 * @author Chiori Greene, a.k.a. Chiori-chan {@literal <me@chiorichan.com>}
 */
public class PostImageProcessor implements Listener
{
	@EventHandler()
	public void onEvent( PostEvalEvent event )
	{
		try
		{
			if ( event.context().getContentType() == null || !event.context().getContentType().toLowerCase().startsWith( "image" ) )
				return;

			float x = -1;
			float y = -1;

			boolean cacheEnabled = ConfigRegistry.config.getBoolean( "advanced.processors.imageProcessorCache", true );
			boolean grayscale = false;

			ScriptingContext context = event.context();
			HttpRequestWrapper request = context.request();

			if ( !UtilityObjects.isNull( request.getArgument( "width" ) ) )
				x = request.getArgumentInt( "width" );

			if ( !UtilityObjects.isNull( request.getArgument( "height" ) ) )
				y = request.getArgumentInt( "height" );

			if ( !UtilityObjects.isNull( request.getArgument( "x" ) ) )
				x = request.getArgumentInt( "x" );

			if ( !UtilityObjects.isNull( request.getArgument( "y" ) ) )
				y = request.getArgumentInt( "y" );

			if ( !UtilityObjects.isNull( request.getArgument( "w" ) ) )
				x = request.getArgumentInt( "w" );

			if ( !UtilityObjects.isNull( request.getArgument( "h" ) ) )
				y = request.getArgumentInt( "h" );

			if ( request.hasArgument( "thumb" ) )
			{
				x = 150;
				y = 0;
			}

			if ( request.hasArgument( "bw" ) || request.hasArgument( "grayscale" ) )
				grayscale = true;

			// Tests if our Post Processor can process the current image.
			List<String> readerFormats = Arrays.asList( ImageIO.getReaderFormatNames() );
			List<String> writerFormats = Arrays.asList( ImageIO.getWriterFormatNames() );
			if ( context.getContentType() != null && !readerFormats.contains( context.getContentType().split( "/" )[1].toLowerCase() ) )
				return;

			int inx = event.context().getBuffer().readerIndex();
			BufferedImage img = ImageIO.read( new ByteBufInputStream( event.context().getBuffer() ) );
			event.context().getBuffer().readerIndex( inx );

			if ( img == null )
				return;

			float w = img.getWidth();
			float h = img.getHeight();
			float w1 = w;
			float h1 = h;

			if ( x < 1 && y < 1 )
			{
				x = w;
				y = h;
			}
			else if ( x > 0 && y < 1 )
			{
				w1 = x;
				h1 = x * ( h / w );
			}
			else if ( y > 0 && x < 1 )
			{
				w1 = y * ( w / h );
				h1 = y;
			}
			else if ( x > 0 && y > 0 )
			{
				w1 = x;
				h1 = y;
			}

			boolean resize = w1 > 0 && h1 > 0 && w1 != w && h1 != h;
			boolean argb = request.hasArgument( "argb" ) && request.getArgument( "argb" ).length() == 8;

			if ( !resize && !argb && !grayscale )
				return;

			// Produce a unique encapsulated id based on this image processing request
			String encapId = UtilityEncrypt.md5( context.getFileName() + w1 + h1 + request.getArgument( "argb" ) + grayscale );
			File tmp = context.getSite() == null ? ConfigRegistry.config.getDirectoryCache() : context.site().directoryTemp();
			File file = new File( tmp, encapId + "_" + new File( context.getFileName() ).getName() );

			if ( cacheEnabled && file.exists() )
			{
				event.context().resetAndWrite( UtilityIO.readFileToBytes( file ) );
				return;
			}

			Image image = resize ? img.getScaledInstance( Math.round( w1 ), Math.round( h1 ), ConfigRegistry.config.getBoolean( "advanced.processors.useFastGraphics", true ) ? Image.SCALE_FAST : Image.SCALE_SMOOTH ) : img;

			// TODO Report malformed parameters to user

			if ( argb )
			{
				FilteredImageSource filteredSrc = new FilteredImageSource( image.getSource(), new RGBColorFilter( ( int ) Long.parseLong( request.getArgument( "argb" ), 16 ) ) );
				image = Toolkit.getDefaultToolkit().createImage( filteredSrc );
			}

			BufferedImage rtn = new BufferedImage( Math.round( w1 ), Math.round( h1 ), img.getType() );
			Graphics2D graphics = rtn.createGraphics();
			graphics.drawImage( image, 0, 0, null );
			graphics.dispose();

			if ( grayscale )
			{
				ColorConvertOp op = new ColorConvertOp( ColorSpace.getInstance( ColorSpace.CS_GRAY ), null );
				op.filter( rtn, rtn );
			}

			if ( resize )
				L.info( EnumColor.GRAY + "Resized image from " + Math.round( w ) + "px by " + Math.round( h ) + "px to " + Math.round( w1 ) + "px by " + Math.round( h1 ) + "px" );

			if ( rtn != null )
			{
				ByteArrayOutputStream bs = new ByteArrayOutputStream();

				if ( context.getContentType() != null && writerFormats.contains( context.contentType().split( "/" )[1].toLowerCase() ) )
					ImageIO.write( rtn, context.getContentType().split( "/" )[1].toLowerCase(), bs );
				else
					ImageIO.write( rtn, "png", bs );

				if ( cacheEnabled && !file.exists() )
					UtilityIO.writeBytesToFile( file, bs.toByteArray() );

				event.context().resetAndWrite( bs.toByteArray() );
			}
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		return;
	}

	static class RGBColorFilter extends RGBImageFilter
	{
		private final int filter;

		RGBColorFilter( int filter )
		{
			this.filter = filter;
			canFilterIndexColorModel = true;
		}

		@Override
		public int filterRGB( int x, int y, int rgb )
		{
			return rgb & filter;
		}
	}
}
