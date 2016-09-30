package com.aldaviva.instagram_rss.common.exceptions;

public class InstagramException extends Exception {

	private static final long serialVersionUID = 1L;

	public InstagramException(final String message) {
		super(message);
	}

	public InstagramException(final Throwable cause) {
		super(cause);
	}

	public InstagramException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
