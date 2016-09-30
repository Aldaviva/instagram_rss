package com.aldaviva.instagram_rss.config;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath(JerseyConfig.ROOT_PATH)
public class JerseyConfig extends ResourceConfig {

	public static final String ROOT_PATH = "api";

	public JerseyConfig() {
		register(JacksonFeature.class);
		packages(SpringConfig.PACKAGE_SCAN);
	}

}
