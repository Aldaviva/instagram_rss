package com.aldaviva.feeds.instagram_rss.common.exceptions;

import static org.testng.Assert.*;

import java.util.Map;
import javax.ws.rs.core.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ApiExceptionMapperTest {

	private ApiExceptionMapper apiExceptionMapper;

	@BeforeMethod
	private void init() {
		apiExceptionMapper = new ApiExceptionMapper();
	}

	@Test
	public void noSuchUser() {
		final Response response = apiExceptionMapper.toResponse(new InstagramException.NoSuchUser("username", "message", null));
		assertEquals(response.getStatus(), 404, "status");
		final Map<?, ?> responseBody = (Map<?, ?>) response.getEntity();
		assertEquals(responseBody.get("class"), "NoSuchUser", "class");
		assertEquals(responseBody.get("message"), "message", "message");
		assertEquals(responseBody.get("username"), "username", "username");
	}

	@Test
	public void privateProfile() {
		final Response response = apiExceptionMapper.toResponse(new InstagramException.PrivateProfile("username", "message"));
		assertEquals(response.getStatus(), 403, "status");
		final Map<?, ?> responseBody = (Map<?, ?>) response.getEntity();
		assertEquals(responseBody.get("class"), "PrivateProfile", "class");
		assertEquals(responseBody.get("message"), "message", "message");
		assertEquals(responseBody.get("username"), "username", "username");
	}

	@Test
	public void instagramException() {
		final Response response = apiExceptionMapper.toResponse(new InstagramException("message"));
		assertEquals(response.getStatus(), 502, "status");
		final Map<?, ?> responseBody = (Map<?, ?>) response.getEntity();
		assertEquals(responseBody.get("class"), "InstagramException", "class");
		assertEquals(responseBody.get("message"), "message", "message");
	}
}
