package io.amelia.engine.wrapper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import io.amelia.engine.log.L;
import io.amelia.extra.UtilityEncrypt;

public class Download implements IDownload
{
	public static final String UNKNOWN_VERSION = "0";

	private static final int BUFFER_SIZE = 10 * 1024;
	private static final int CONNECTION_TIMEOUT_MILLISECONDS = 10 * 1000;
	private static final int PROGRESS_CHUNK = 1024 * 1024;
	private static final int READ_TIMEOUT_MILLISECONDS = 10 * 1000;

	/**
	 * Create a safe URI from the given one by stripping out user info.
	 *
	 * @param uri Original URI
	 *
	 * @return a new URI with no user info
	 */
	static URI safeUri( URI uri )
	{
		try
		{
			return new URI( uri.getScheme(), null, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment() );
		}
		catch ( URISyntaxException e )
		{
			throw new RuntimeException( "Failed to parse URI", e );
		}
	}

	private final String appName;
	private final String appVersion;
	private final DownloadProgressListener progressListener;

	public Download( String appName, String appVersion )
	{
		this( null, appName, appVersion );
	}

	public Download( DownloadProgressListener progressListener, String appName, String appVersion )
	{
		this.appName = appName;
		this.appVersion = appVersion;
		this.progressListener = new DefaultDownloadProgressListener( progressListener );
		configureProxyAuthentication();
	}

	private void addBasicAuthentication( URI address, URLConnection connection ) throws IOException
	{
		String userInfo = calculateUserInfo( address );
		if ( userInfo == null )
		{
			return;
		}
		if ( !"https".equals( address.getScheme() ) )
		{
			L.warning( "WARNING Using HTTP Basic Authentication over an insecure connection to download the Engine distribution. Please consider using HTTPS." );
		}
		connection.setRequestProperty( "Authorization", "Basic " + UtilityEncrypt.base64Encode( userInfo ) );
	}

	private String calculateUserAgent()
	{
		String javaVendor = System.getProperty( "java.vendor" );
		String javaVersion = System.getProperty( "java.version" );
		String javaVendorVersion = System.getProperty( "java.vm.version" );
		String osName = System.getProperty( "os.name" );
		String osVersion = System.getProperty( "os.version" );
		String osArch = System.getProperty( "os.arch" );
		return String.format( "%s/%s (%s;%s;%s) (%s;%s;%s)", appName, appVersion, osName, osVersion, osArch, javaVendor, javaVersion, javaVendorVersion );
	}

	private String calculateUserInfo( URI uri )
	{
		String username = System.getProperty( "engine.wrapperUser" );
		String password = System.getProperty( "engine.wrapperPassword" );
		if ( username != null && password != null )
		{
			return username + ':' + password;
		}
		return uri.getUserInfo();
	}

	private void configureProxyAuthentication()
	{
		if ( System.getProperty( "http.proxyUser" ) != null || System.getProperty( "https.proxyUser" ) != null )
		{
			// Only an authenticator for proxies needs to be set. Basic authentication is supported by directly setting the request header field.
			Authenticator.setDefault( new ProxyAuthenticator() );
		}
	}

	public void download( URI address, File destination ) throws Exception
	{
		destination.getParentFile().mkdirs();
		downloadInternal( address, destination );
	}

	private void downloadInternal( URI address, File destination ) throws Exception
	{
		OutputStream out = null;
		URLConnection conn;
		InputStream in = null;
		URL safeUrl = safeUri( address ).toURL();
		try
		{
			out = new BufferedOutputStream( new FileOutputStream( destination ) );

			// No proxy is passed here as proxies are set globally using the HTTP(S) proxy system properties. The respective protocol handler implementation then makes use of these properties.
			conn = safeUrl.openConnection();

			addBasicAuthentication( address, conn );
			final String userAgentValue = calculateUserAgent();
			conn.setRequestProperty( "User-Agent", userAgentValue );
			conn.setConnectTimeout( CONNECTION_TIMEOUT_MILLISECONDS );
			conn.setReadTimeout( READ_TIMEOUT_MILLISECONDS );
			in = conn.getInputStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			int numRead;
			int totalLength = conn.getContentLength();
			long downloadedLength = 0;
			long progressCounter = 0;
			while ( ( numRead = in.read( buffer ) ) != -1 )
			{
				if ( Thread.currentThread().isInterrupted() )
				{
					System.out.print( "interrupted" );
					throw new IOException( "Download was interrupted." );
				}

				downloadedLength += numRead;
				progressCounter += numRead;

				if ( progressCounter / PROGRESS_CHUNK > 0 || downloadedLength == totalLength )
				{
					progressCounter = progressCounter - PROGRESS_CHUNK;
					progressListener.downloadStatusChanged( address, totalLength, downloadedLength );
				}

				out.write( buffer, 0, numRead );
			}
		}
		catch ( SocketTimeoutException e )
		{
			throw new IOException( "Downloading from " + safeUrl + " failed: timeout", e );
		}
		finally
		{
			L.info( "" );
			if ( in != null )
			{
				in.close();
			}
			if ( out != null )
			{
				out.close();
			}
		}
	}

	private static class DefaultDownloadProgressListener implements DownloadProgressListener
	{
		private final DownloadProgressListener delegate;
		private int previousDownloadPercent;

		public DefaultDownloadProgressListener( DownloadProgressListener delegate )
		{
			this.delegate = delegate;
			this.previousDownloadPercent = 0;
		}

		private void appendPercentageSoFar( long contentLength, long downloaded )
		{
			int currentDownloadPercent = 10 * ( calculateDownloadPercent( contentLength, downloaded ) / 10 );
			if ( currentDownloadPercent != 0 && previousDownloadPercent != currentDownloadPercent )
			{
				L.info( String.valueOf( currentDownloadPercent ) + "%" ); // append
				previousDownloadPercent = currentDownloadPercent;
			}
		}

		private int calculateDownloadPercent( long totalLength, long downloadedLength )
		{
			return Math.min( 100, Math.max( 0, ( int ) ( ( downloadedLength / ( double ) totalLength ) * 100 ) ) );
		}

		@Override
		public void downloadStatusChanged( URI address, long contentLength, long downloaded )
		{
			// If the total size of distribution is known, but there's no advanced progress listener, provide extra progress information
			if ( contentLength > 0 && delegate == null )
			{
				appendPercentageSoFar( contentLength, downloaded );
			}

			if ( contentLength != downloaded )
			{
				L.info( "." ); // append
			}

			if ( delegate != null )
			{
				delegate.downloadStatusChanged( address, contentLength, downloaded );
			}
		}
	}

	private static class ProxyAuthenticator extends Authenticator
	{
		@Override
		protected PasswordAuthentication getPasswordAuthentication()
		{
			if ( getRequestorType() == RequestorType.PROXY )
			{
				// Note: Do not use getRequestingProtocol() here, which is "http" even for HTTPS proxies.
				String protocol = getRequestingURL().getProtocol();
				String proxyUser = System.getProperty( protocol + ".proxyUser" );
				if ( proxyUser != null )
				{
					String proxyPassword = System.getProperty( protocol + ".proxyPassword", "" );
					return new PasswordAuthentication( proxyUser, proxyPassword.toCharArray() );
				}
			}

			return super.getPasswordAuthentication();
		}
	}
}
