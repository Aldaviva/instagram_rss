package com.aldaviva.feeds.instagram_rss.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.springframework.beans.factory.annotation.Autowired;

@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {

	@Autowired private ObjectMapper objectMapper;

	@Override
	public ObjectMapper getContext(final Class<?> type) {
		return objectMapper;
	}

}
