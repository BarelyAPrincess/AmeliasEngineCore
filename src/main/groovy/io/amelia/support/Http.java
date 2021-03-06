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

import com.google.common.base.Joiner;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import io.amelia.foundation.Kernel;
import io.amelia.extra.UtilityIO;
import io.amelia.extra.UtilityStrings;

/**
 * Provides Network Utilities
 */
public class Http
{
	private static final Pattern DNS_WILDCARD_PATTERN = Pattern.compile( "^\\*\\..*" );

	public static void downloadFile( String url, File dest ) throws IOException
	{
		downloadFile( url, dest, null );
	}

	public static void downloadFile( String url, File dest, BiConsumer<Long, Integer> progressConsumer ) throws IOException
	{
		downloadFile( url, dest.toPath(), progressConsumer );
	}

	public static void downloadFile( String url, Path destPath ) throws IOException
	{
		downloadFile( url, destPath, null );
	}

	public static void downloadFile( String url, Path destPath, BiConsumer<Long, Integer> progressConsumer ) throws IOException
	{
		InputStream is = null;
		OutputStream os = null;

		try
		{
			URLConnection conn = new URL( url ).openConnection();

			int length = conn.getContentLength();
			is = conn.getInputStream();
			os = Files.newOutputStream( destPath );

			UtilityIO.copyLarge( is, os, progressConsumer == null ? null : ( progress, count ) -> {
				progressConsumer.accept( progress, length < 0 ? count : length );
			} );
		}
		finally
		{
			UtilityIO.closeQuietly( is );
			UtilityIO.closeQuietly( os );
		}
	}

	public static String getUserAgent()
	{
		return Kernel.getDevMeta().getProductName() + "/" + Kernel.getDevMeta().getVersionString() + "/" + Sys.getJavaVersion();
	}

	public static boolean hostnameNeedsNormalization( String str )
	{
		final int length = str.length();
		for ( int i = 0; i < length; i++ )
		{
			int c = str.charAt( i );
			if ( c > 0x7F )
				return true;
		}
		return false;
	}

	public static String hostnameNormalize( String str )
	{
		if ( str == null )
			return null;
		if ( hostnameNeedsNormalization( str ) )
			str = UtilityStrings.toAscii( str );
		// str = IDN.toASCII( str, IDN.ALLOW_UNASSIGNED );
		return str.toLowerCase( Locale.US );
	}

	public static boolean matches( String strTemplate, String str )
	{
		if ( DNS_WILDCARD_PATTERN.matcher( strTemplate ).matches() )
			return strTemplate.substring( 2 ).equals( str ) || str.endsWith( strTemplate.substring( 1 ) );
		else
			return strTemplate.equals( str );
	}

	/**
	 * Establishes an HttpURLConnection from a URL, with the correct configuration to receive content from the given URL.
	 *
	 * @param url The URL to set up and receive content from
	 *
	 * @return A valid HttpURLConnection
	 *
	 * @throws IOException The openConnection() method throws an IOException and the calling method is responsible for handling it.
	 */
	public static HttpURLConnection openHttpConnection( URL url ) throws IOException
	{
		HttpURLConnection conn = ( HttpURLConnection ) url.openConnection();
		conn.setDoInput( true );
		conn.setDoOutput( false );
		System.setProperty( "http.agent", getUserAgent() );
		conn.setRequestProperty( "User-Agent", getUserAgent() );
		HttpURLConnection.setFollowRedirects( true );
		conn.setUseCaches( false );
		conn.setInstanceFollowRedirects( true );
		return conn;
	}

	/**
	 * Opens an HTTP connection to a web URL and tests that the response is a valid 200-level code
	 * and we can successfully open a stream to the content.
	 *
	 * @param url The HTTP URL indicating the location of the content.
	 *
	 * @return True if the content can be accessed successfully, false otherwise.
	 */
	public static boolean pingHttpURL( String url )
	{
		InputStream stream = null;
		try
		{
			final HttpURLConnection conn = openHttpConnection( new URL( url ) );
			conn.setConnectTimeout( 10000 );

			int responseCode = conn.getResponseCode();
			int responseFamily = responseCode / 100;

			if ( responseFamily == 2 )
			{
				stream = conn.getInputStream();
				UtilityIO.closeQuietly( stream );
				return true;
			}
			else
				return false;
		}
		catch ( IOException e )
		{
			return false;
		}
		finally
		{
			UtilityIO.closeQuietly( stream );
		}
	}

	public static String postUrl( String url, Map<String, String> postArgs )
	{
		try
		{
			return postUrlWithException( url, postArgs );
		}
		catch ( IOException e )
		{
			return null;
		}
	}

	public static String postUrlWithException( String url, Map<String, String> postArgs ) throws IOException
	{
		URL obj = new URL( url );
		HttpsURLConnection con = ( HttpsURLConnection ) obj.openConnection();

		con.setRequestMethod( "POST" );
		con.setRequestProperty( "User-Agent", getUserAgent() );
		con.setRequestProperty( "Accept-Language", "en-US,en;q=0.5" );

		con.setDoOutput( true );
		DataOutputStream wr = new DataOutputStream( con.getOutputStream() );

		try
		{
			wr.writeBytes( Joiner.on( "&" ).withKeyValueSeparator( "=" ).join( postArgs ) );
			wr.flush();
		}
		finally
		{
			UtilityIO.closeQuietly( wr );
		}

		BufferedReader in = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
		StringBuffer response = new StringBuffer();

		try
		{
			String inputLine;
			while ( ( inputLine = in.readLine() ) != null )
				response.append( inputLine );
			in.close();
		}
		finally
		{
			UtilityIO.closeQuietly( in );
		}

		return response.toString();
	}

	public static byte[] readUrl( String url )
	{
		try
		{
			return readUrlWithException( url );
		}
		catch ( IOException e )
		{
			// Log.get().severe( "Reading URL \"" + url + "\" failed!" );
			return null;
		}
	}

	public static byte[] readUrl( String url, boolean trustAll )
	{
		try
		{
			return readUrlWithException( url, trustAll );
		}
		catch ( IOException e )
		{
			// Log.get().severe( "Reading URL \"" + url + "\" failed!" );
			return null;
		}
	}

	public static byte[] readUrl( String url, String user, String pass )
	{
		try
		{
			return readUrlWithException( url, user, pass, false );
		}
		catch ( IOException e )
		{
			return null;
		}
	}

	public static byte[] readUrlWithException( String url ) throws IOException
	{
		return readUrlWithException( url, null, null, false );
	}

	public static byte[] readUrlWithException( String url, boolean trustAll ) throws IOException
	{
		return readUrlWithException( url, null, null, trustAll );
	}

	public static byte[] readUrlWithException( String surl, String user, String pass, boolean trustAll ) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		URL url = new URL( surl );
		URLConnection uc = url.openConnection();

		if ( user != null || pass != null )
		{
			String userpass = user + ":" + pass;
			String basicAuth = "Basic " + new String( Base64.getEncoder().encode( userpass.getBytes() ) );
			uc.setRequestProperty( "Authorization", basicAuth );
		}

		if ( uc instanceof HttpsURLConnection && trustAll )
			try
			{
				SSLContext ctx = SSLContext.getInstance( "SSL" );
				ctx.init( null, io.amelia.support.UnsafeTrustManagerFactory.getTrustManagers(), null );
				( ( HttpsURLConnection ) uc ).setSSLSocketFactory( ctx.getSocketFactory() );
			}
			catch ( KeyManagementException | NoSuchAlgorithmException e )
			{
				throw new IOException( "Failed to set the SSL Factory.", e );
			}

		InputStream is = uc.getInputStream();

		byte[] byteChunk = new byte[4096];
		int n;

		while ( ( n = is.read( byteChunk ) ) > 0 )
			out.write( byteChunk, 0, n );

		is.close();

		return out.toByteArray();
	}

	private Http()
	{

	}
}
