package com.aldaviva.feeds.instagram_rss.config;

import java.io.IOException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status.Family;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath(JerseyConfig.ROOT_PATH)
public class JerseyConfig extends ResourceConfig {

	public static final String ROOT_PATH = "api";

	public JerseyConfig() {
		register(JacksonFeature.class);
		packages(SpringConfig.PACKAGE_SCAN);
		register(CachingFilter.class);
	}

	private static final class CachingFilter implements ContainerResponseFilter {

		@Override
		public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
			if(Family.SUCCESSFUL.equals(responseContext.getStatusInfo().getFamily())) {
				if(!responseContext.getHeaders().containsKey(HttpHeaders.CACHE_CONTROL)) {
					responseContext.getHeaders().add(HttpHeaders.CACHE_CONTROL, "max-age=3600");
				}
			}
		}

	}
}
