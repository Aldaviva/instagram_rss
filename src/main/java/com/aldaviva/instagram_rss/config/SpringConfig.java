package com.aldaviva.instagram_rss.config;

import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@Order(0)
public class SpringConfig implements WebApplicationInitializer {

	private static Logger LOGGER;
	private static AnnotationConfigWebApplicationContext context;

	static final String PACKAGE_SCAN = "com.aldaviva.instagram_rss";

	public static final String ENV = "env";
	public static final String ENV_PROD = "prod";
	public static final String ENV_DEV = "dev";
	public static final String ENV_TEST = "test";
	public static final String PROFILE_TEST = "test";

	@Override
	public void onStartup(final ServletContext servletContext) throws ServletException {
		initLogging();

		servletContext.setInitParameter("contextConfigLocation", ""); //prevent Jersey from also initializing Spring

		context = new AnnotationConfigWebApplicationContext();

		if(ENV_TEST.equals(getEnvironment())) {
			context.getEnvironment().setActiveProfiles(PROFILE_TEST);
		}

		context.scan(PACKAGE_SCAN);
		servletContext.addListener(new ContextLoaderListener(context));

		LOGGER.debug("Spring initialized");
	}

	private void initLogging() {
		System.setProperty("log4j.defaultInitOverride", "true");

		URL resource = Loader.getResource("log4j.properties");
		PropertyConfigurator.configure(resource);
		final String envSpecificLogConfFilename = "log4j-" + getEnvironment() + ".properties";
		final URL envSpecificLogConfUrl = Loader.getResource(envSpecificLogConfFilename);
		if(envSpecificLogConfUrl != null) {
			PropertyConfigurator.configure(envSpecificLogConfUrl);
		} else {
			System.out.println("Missing Log4J configuration file " + envSpecificLogConfFilename + ", logging might be broken!");
		}

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		LOGGER = LoggerFactory.getLogger(SpringConfig.class);
	}

	private String getEnvironment() {
		return System.getProperty(ENV, ENV_PROD);
	}

}
