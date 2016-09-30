package org.glassfish.jersey.client;

public class AlwaysOpenJerseyClient extends JerseyClient {

	@Override
	void checkNotClosed() {
		//test clients are never closed
	}

}