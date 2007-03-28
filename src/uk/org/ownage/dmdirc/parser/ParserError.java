/*
 * Copyright (c) 2006-2007 Chris Smith, Shane Mc Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * SVN: $Id$
 */

package uk.org.ownage.dmdirc.parser;

/**
 * IRC Parser Error.
 *
 * @author            Shane Mc Cormack
 * @version           $Id$
 */
public final class ParserError {
	/** Error is potentially Fatal, Desync 99% Guaranteed! */
	public static final int ERROR_FATAL = 1;
	/** Error is not fatal, but is more severe than a warning. */
	public static final int ERROR_ERROR = 2;
	/** Error was an unexpected occurance, but shouldn't be anything to worry about. */
	public static final int ERROR_WARNING = 4;
	/** Error was an exception from elsewhere. */
	public static final int ERROR_EXCEPTION = 8;
	
	/** Store the Error level. */
	private int errorLevel;
	/** Store the Error Information. */
	private String errorData;
	/** Store the Exception object. */
	private Exception exceptionInfo;

	/**
	 * Create a new Error.
	 *
	 * @param level Set the error level.
	 * @param data String containing information about the error.
	 */
	public ParserError(final int level, final String data) {
		errorData = data;
		errorLevel = level;
	}
	
	/**
	 * Check if this error is considered Fatal.
	 *
	 * @return Returns true for a fatal error, false for a non-fatal error
	 */
	public boolean isFatal() {
		return (errorLevel & ERROR_FATAL) == ERROR_FATAL;
	}
	
	/**
	 * Check if this error is considered an error (less severe than fatal, worse than warning).
	 *
	 * @return Returns true for an "Error" level error, else false.
	 */
	public boolean isError() {
		return (errorLevel & ERROR_ERROR) == ERROR_ERROR;
	}
	
	/**
	 * Check if this error is considered a warning.
	 *
	 * @return Returns true for a warning, else false.
	 */
	public boolean isWarning() {
		return (errorLevel & ERROR_WARNING) == ERROR_WARNING;
	}
	
	/**
	 * Check if this error was generated from an exception.
	 *
	 * @return Returns true if getException will return an exception.
	 */
	public boolean isException() {
		return (errorLevel & ERROR_EXCEPTION) == ERROR_EXCEPTION;
	}
	
	/**
	 * Set the Exception object.
	 *
	 * @param newException The exception object to store
	 */
	public void setException(final Exception newException) {
		exceptionInfo = newException;
		if (!this.isException()) {
			this.errorLevel = this.errorLevel + ERROR_EXCEPTION;
		}
	}
	
	/**
	 * Get the Exception object.
	 *
	 * @return Returns the exception object
	 */
	public Exception getException() {
		return exceptionInfo;
	}
	
	/**
	 * Get the full ErrorLevel.
	 *
	 * @return Returns the error level
	 */
	public int getLevel() {
		return errorLevel;
	}
	
	/**
	 * Get the Error information.
	 *
	 * @return Returns the error data
	 */
	public String getData() {
		return errorData;
	}
	
	
	/**
	 * Get SVN Version information.
	 *
	 * @return SVN Version String
	 */
	public static String getSvnInfo() { return "$Id$"; }	
}
