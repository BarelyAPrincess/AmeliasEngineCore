/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * <p>
 * Copyright (c) 2019 Amelia Sara Greene <barelyaprincess@gmail.com>
 * Copyright (c) 2019 Penoaks Publishing LLC <development@penoaks.com>
 * <p>
 * All Rights Reserved.
 */
package io.amelia.lang;

import javax.annotation.Nonnull;

/**
 * Implemented as an exception container for general exceptions used by, but not limited to, the EngineCore.
 */
public final class ApplicationException
{
	private ApplicationException()
	{
		// Static
	}

	/*
	 * INTERNAL USE ONLY
	 * This should be muted within the main() method.
	 */
	public static class Crash extends Runtime
	{
		private static final long serialVersionUID = -4937198089020390887L;

		public Crash( Throwable cause )
		{
			super( cause );
		}

		public Crash()
		{
			super();
		}
	}

	public static class Error extends Exception implements ExceptionContext
	{
		protected final ReportingLevel level;

		public Error()
		{
			this( ReportingLevel.E_ERROR );
		}

		public Error( String message )
		{
			this( ReportingLevel.E_ERROR, message );
		}

		public Error( String message, Throwable cause )
		{
			this( ReportingLevel.E_ERROR, message, cause );
		}

		public Error( Throwable cause )
		{
			this( ReportingLevel.E_ERROR, cause );
		}

		public Error( ReportingLevel level )
		{
			this.level = level;
		}

		public Error( ReportingLevel level, String message )
		{
			super( message );
			this.level = level;
		}

		public Error( ReportingLevel level, String message, Throwable cause )
		{
			super( message, cause );
			this.level = level;

			if ( cause != null && cause.getClass().isAssignableFrom( getClass() ) )
				throw new IllegalArgumentException( "The cause argument can't be same class. {cause: " + cause.getClass() + ", this: " + getClass() + "}" );
		}

		public Error( ReportingLevel level, Throwable cause )
		{
			super( cause );
			this.level = level;

			if ( cause != null && cause.getClass().isAssignableFrom( getClass() ) )
				throw new IllegalArgumentException( "The cause argument can't be same class. {cause: " + cause.getClass() + ", this: " + getClass() + "}" );
		}

		@Nonnull
		public ExceptionReport getExceptionReport()
		{
			return new ExceptionReport().addException( this );
		}

		@Override
		public String getMessage()
		{
			return super.getMessage();
			// return String.format( "Exception %s thrown in file '%s' at line %s: '%s'", getClass().getProductName(), getStackTrace()[0].getFileName(), getStackTrace()[0].getLineNumber(), super.getMessage() );
		}

		@Override
		public ReportingLevel getReportingLevel()
		{
			return level;
		}

		@Nonnull
		@Override
		public Throwable getThrowable()
		{
			return this;
		}

		@Override
		public ReportingLevel handle( ExceptionReport exceptionReport, ExceptionContext exceptionContext )
		{
			return null;
		}

		public boolean hasCause()
		{
			return getCause() != null;
		}

		@Override
		public boolean isIgnorable()
		{
			return level.isIgnorable();
		}
	}

	public static class Ignorable extends Runtime
	{
		public Ignorable()
		{
			super( ReportingLevel.E_IGNORABLE );
		}

		public Ignorable( String message )
		{
			super( ReportingLevel.E_IGNORABLE, message );
		}

		public Ignorable( String message, Throwable cause )
		{
			super( ReportingLevel.E_IGNORABLE, message, cause );
		}

		public Ignorable( Throwable cause )
		{
			super( ReportingLevel.E_IGNORABLE, cause );
		}
	}

	public static class Notice extends Error
	{
		public Notice()
		{
			super( ReportingLevel.E_NOTICE );
		}

		public Notice( String message )
		{
			super( ReportingLevel.E_NOTICE, message );
		}

		public Notice( String message, Throwable cause )
		{
			super( ReportingLevel.E_NOTICE, message, cause );
		}

		public Notice( Throwable cause )
		{
			super( ReportingLevel.E_NOTICE, cause );
		}
	}

	public static class Runtime extends RuntimeException implements ExceptionContext
	{
		protected final ReportingLevel level;

		public Runtime()
		{
			this( ReportingLevel.E_USER_ERROR );
		}

		public Runtime( String message )
		{
			this( ReportingLevel.E_USER_ERROR, message );
		}

		public Runtime( String message, Throwable cause )
		{
			this( ReportingLevel.E_USER_ERROR, message, cause );
		}

		public Runtime( Throwable cause )
		{
			this( ReportingLevel.E_USER_ERROR, cause );
		}

		public Runtime( ReportingLevel level )
		{
			this.level = level;
		}

		public Runtime( ReportingLevel level, String message )
		{
			super( message );
			this.level = level;
		}

		public Runtime( ReportingLevel level, String message, Throwable cause )
		{
			super( message, cause );
			this.level = level;
		}

		public Runtime( ReportingLevel level, Throwable cause )
		{
			super( cause );
			this.level = level;
		}

		@Nonnull
		public ExceptionReport getExceptionReport()
		{
			return new ExceptionReport().addException( this );
		}

		@Override
		public ReportingLevel getReportingLevel()
		{
			return null;
		}

		@Nonnull
		@Override
		public Throwable getThrowable()
		{
			return this;
		}

		@Override
		public ReportingLevel handle( ExceptionReport exceptionReport, ExceptionContext exceptionContext )
		{
			return null;
		}

		public boolean hasCause()
		{
			return getCause() != null;
		}

		@Override
		public boolean isIgnorable()
		{
			return level.isIgnorable();
		}
	}

	/**
	 * Specifically only to be used as an exception thrown when a subset of code fails to start.
	 * e.g., Users, Permissions, etc.
	 */
	public static class Startup extends Uncaught
	{
		private static final long serialVersionUID = 1L;

		public Startup( String msg )
		{
			super( ReportingLevel.E_ERROR, msg );
		}

		public Startup( String msg, Throwable e ) throws Uncaught
		{
			super( ReportingLevel.E_ERROR, msg, e, true );
		}

		public Startup( Throwable e ) throws Uncaught
		{
			super( ReportingLevel.E_ERROR, e, true );
		}

		@Override
		public ReportingLevel handle( ExceptionReport exceptionReport, ExceptionContext exceptionContext )
		{
			exceptionReport.addException( ReportingLevel.E_ERROR, this );
			return ReportingLevel.E_ERROR;
		}
	}

	/**
	 * Used to gracefully interrupt startup.
	 * Such as if --help or --version was specified.
	 */
	public static class StartupInterrupt extends Startup
	{
		private static final long serialVersionUID = -4937198089020390887L;

		public StartupInterrupt()
		{
			super( "STARTUP INTERRUPT!" );
		}
	}

	public static class Uncaught extends RuntimeException implements ExceptionContext
	{
		private static final long serialVersionUID = 6854413013575591783L;

		private ReportingLevel level;

		public Uncaught()
		{
			this( ReportingLevel.E_UNHANDLED );
		}

		public Uncaught( ReportingLevel level )
		{
			this.level = level;
		}

		public Uncaught( ReportingLevel level, String message )
		{
			super( message );
			this.level = level;
		}

		public Uncaught( ReportingLevel level, String msg, Throwable cause )
		{
			super( msg, cause );
			this.level = level;
			if ( cause instanceof Uncaught )
				throwCauseException();
		}

		public Uncaught( ReportingLevel level, String msg, Throwable cause, boolean throwDuplicate ) throws Uncaught
		{
			super( msg, cause );
			this.level = level;
			if ( cause instanceof Uncaught )
				if ( throwDuplicate )
					throw ( Uncaught ) cause;
				else
					throwCauseException();
		}

		public Uncaught( ReportingLevel level, Throwable cause )
		{
			super( cause );
			this.level = level;
			if ( cause instanceof Uncaught )
				throwCauseException();
		}

		public Uncaught( ReportingLevel level, Throwable cause, boolean throwDuplicate ) throws Uncaught
		{
			super( cause );
			this.level = level;
			if ( cause instanceof Uncaught )
				if ( throwDuplicate )
					throw ( Uncaught ) cause;
				else
					throwCauseException();
		}

		public Uncaught( String message )
		{
			this( ReportingLevel.E_UNHANDLED, message );
		}

		public Uncaught( String msg, Throwable cause )
		{
			this( ReportingLevel.E_UNHANDLED, msg, cause );
		}

		public Uncaught( Throwable cause )
		{
			this( ReportingLevel.E_UNHANDLED, cause );
		}

		@Nonnull
		@Override
		public ExceptionReport getExceptionReport()
		{
			return new ExceptionReport().addException( this );
		}

		@Override
		public ReportingLevel getReportingLevel()
		{
			return level;
		}

		public void setReportingLevel( ReportingLevel level )
		{
			this.level = level;
		}

		@Nonnull
		@Override
		public Throwable getThrowable()
		{
			return this;
		}

		@Override
		public ReportingLevel handle( ExceptionReport exceptionReport, ExceptionContext exceptionContext )
		{
			exceptionReport.handleException( getCause(), exceptionContext );
			return level;
		}

		@Override
		public boolean isIgnorable()
		{
			return level.isIgnorable();
		}

		private void throwCauseException()
		{
			throw new IllegalArgumentException( "The cause argument can't be of it's own type." );
		}
	}
}
