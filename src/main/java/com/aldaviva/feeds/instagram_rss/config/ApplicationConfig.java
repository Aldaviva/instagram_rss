package com.aldaviva.feeds.instagram_rss.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class ApplicationConfig {

	@Bean
	public Client httpClient() {
		final ClientConfig config = new ClientConfig();
		config.register(JacksonFeature.class);
		config.property(ClientProperties.CONNECT_TIMEOUT, 5000);
		config.property(ClientProperties.READ_TIMEOUT, 5000);
		config.property(ClientProperties.FOLLOW_REDIRECTS, true);
		config.connectorProvider(new ApacheConnectorProvider());
		return ClientBuilder.newClient(config);
	}

	@Bean
	public ObjectMapper objectMapper() {
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JodaModule());
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
		return objectMapper;
	}

	/**
	 * Configuration comes from
	 * 
	 * <ol>
	 * <li><code>instagram_rss_conf/*.properties</code> on the classpath, like in Jetty's <code>conf</code> directory (highest priority)</li>
	 * <li><code>META-INF/dev/*.properties</code>, if the servlet container is launched with <code>-Denv=dev</code></li>
	 * <li><code>META-INF/test/*.properties</code>, if launched by Surefire/TestNG</li>
	 * <li><code>META-INF/prod/*.properties</code> (lowest priority)</li>
	 * </ol>
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
		final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		final List<Resource> locations = new ArrayList<>();
		final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

		locations.addAll(Arrays.asList(pathMatchingResourcePatternResolver.getResources("META-INF/prod/*.properties")));

		final String environment = System.getProperty(SpringConfig.ENV);
		if(environment != null) {
			try {
				locations.addAll(Arrays.asList(pathMatchingResourcePatternResolver.getResources("META-INF/" + environment + "/*.properties")));
			} catch (final FileNotFoundException e) {
				// skip missing dev/test properties files, prod values will be used for these files.
			}
		}

		try {
			locations.addAll(Arrays.asList(pathMatchingResourcePatternResolver.getResources("classpath:instagram_rss_conf/*.properties")));
		} catch (final FileNotFoundException e) {
			// skip missing classpath properties files, compiled values will be used for these files.
		}

		propertySourcesPlaceholderConfigurer.setLocations(locations.toArray(new Resource[] {}));
		return propertySourcesPlaceholderConfigurer;
	}

}
