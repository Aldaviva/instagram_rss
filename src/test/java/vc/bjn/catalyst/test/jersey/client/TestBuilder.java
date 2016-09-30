package vc.bjn.catalyst.test.jersey.client;

import org.glassfish.jersey.client.JerseyInvocation.Builder;

public class TestBuilder extends org.glassfish.jersey.client.JerseyInvocation.Builder {

	public TestBuilder() {
		super(null, null);
	}

	/**
	 * You may spy on this to see if you like the URI
	 */
	public void requested(final String uri) {
		//this method does nothing, but the act of calling it is important for mockito verifications 
	}

	/**
	 * If you need to watch for particular header calls, mock this.
	 */
	public Builder headerAdded(final String key, final Object value) {
		// This method does nothing by default, but you can mock it for your uses.
		return this;
	}
}