package com.aldaviva.feeds.instagram_rss.common.exceptions;

import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException.NoSuchUser;
import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException.PrivateProfile;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<InstagramException> {

	@Override
	public Response toResponse(final InstagramException exception) {
		ResponseBuilder response;
		final Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("class", exception.getClass().getSimpleName());
		responseBody.put("message", exception.getMessage());

		try {
			throw exception;
		} catch (final NoSuchUser e) {
			response = Response.status(Status.NOT_FOUND);
			responseBody.put("username", e.username);
		} catch (final PrivateProfile e) {
			response = Response.status(Status.FORBIDDEN);
			responseBody.put("username", e.username);
		} catch (final InstagramException e) {
			response = Response.status(Status.BAD_GATEWAY);
		}

		return response.type(MediaType.APPLICATION_JSON_TYPE).entity(responseBody).build();
	}

}
