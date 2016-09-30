package com.aldaviva.instagram_rss.service.instagram;

import com.aldaviva.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.instagram_rss.data.InstagramPost;
import com.aldaviva.instagram_rss.data.InstagramUser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Provider;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstagramServiceImpl implements InstagramService {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InstagramServiceImpl.class);

	@Autowired private Provider<Client> httpClient;
	@Autowired private ObjectMapper objectMapper;

	@Override
	public InstagramUser getUser(final String username) throws InstagramException {
		LOGGER.trace("Requesting user profile for {}...", username);
		try {
			final String response = httpClient.get().target(BASE_URI)
			    .path(username)
			    .request()
			    .get(String.class);

			LOGGER.trace("Downloaded profile, extracting...");

			final Pattern pattern = Pattern.compile(".*window\\._sharedData = (.*);.*", Pattern.DOTALL);
			final Matcher matcher = pattern.matcher(response);
			if(matcher.matches()) {
				final String sharedDataText = matcher.group(1);
				LOGGER.trace("Extracted profile, parsing...");
				final JsonNode sharedData = objectMapper.readTree(sharedDataText);
				LOGGER.trace("Got profile for user {}.", username);
				return convertSharedDataToUser(sharedData);
			} else {
				throw new InstagramException("No sharedData returned for user " + username);
			}

		} catch (WebApplicationException | ProcessingException e) {
			throw new InstagramException("Could not get shared data for user " + username, e);
		} catch (final IOException e) {
			throw new InstagramException("Failed to parse shared data user " + username, e);
		}
	}

	protected InstagramUser convertSharedDataToUser(final JsonNode sharedData) throws InstagramException {
		final InstagramUser user = new InstagramUser();
		final JsonNode rawUser = sharedData.path("entry_data").path("ProfilePage").get(0).path("user");

		user.setBiography(rawUser.path("biography").textValue());
		user.setCsrfToken(sharedData.path("config").path("csrf_token").textValue());
		user.setFullName(rawUser.path("full_name").textValue());
		user.setUserId(rawUser.path("id").asLong());
		user.setUsername(rawUser.path("username").textValue());

		try {
			user.setProfilePicture(new URI(rawUser.path("profile_pic_url_hd").textValue()));
		} catch (final URISyntaxException e) {
			try {
				user.setProfilePicture(new URI(rawUser.path("profile_pic_url").textValue()));
			} catch (final URISyntaxException e1) {
				LOGGER.warn("Invalid profile picture URL for user {}: {}", user.getUsername(), rawUser.path("profile_pic_url_hd").textValue());
			}
		}

		final JsonNode mediaNodes = rawUser.path("media").path("nodes");
		for(final JsonNode mediaNode : mediaNodes) {
			user.getPosts().add(convertMediaNodeToPost(mediaNode, user));
		}

		return user;
	}

	protected InstagramPost convertMediaNodeToPost(final JsonNode rawPost, final InstagramUser user) throws InstagramException {
		final InstagramPost post = new InstagramPost();

		post.setCaption(rawPost.path("caption").textValue());
		post.setDatePosted(new DateTime(rawPost.path("date").asLong() * 1000));
		post.setHeight(rawPost.path("dimensions").path("height").intValue());
		post.setWidth(rawPost.path("dimensions").path("width").intValue());
		post.setId(rawPost.path("id").asLong());
		post.setOwnerId(rawPost.path("owner").path("id").asLong());
		post.setCode(rawPost.path("code").textValue());

		try {
			post.setDisplaySource(new URI(rawPost.path("display_src").textValue()));
			post.setThumbnailSource(new URI(rawPost.path("thumbnail_src").textValue()));
		} catch (final URISyntaxException e) {
			throw new InstagramException("Illegal display or thumbnail URL for " + user.getUsername() + "'s post " + post.getPostUri(), e);
		}

		return post;
	}

}
