package com.aldaviva.feeds.instagram_rss.common.exceptions;

public class InstagramException extends Exception {

	private static final long serialVersionUID = 1L;

	public InstagramException(final String message) {
		super(message);
	}

	public InstagramException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public static class NoSuchUser extends InstagramException {

		private static final long serialVersionUID = 1L;

		public String username;

		public NoSuchUser(final String username, final String message, final Throwable cause) {
			super(message, cause);
			this.username = username;
		}
	}

	public static class PrivateProfile extends InstagramException {

		private static final long serialVersionUID = 1L;

		public String username;

		public PrivateProfile(final String username, final String message) {
			super(message);
			this.username = username;
		}

	}

}
