package com.aldaviva.feeds.instagram_rss.service.instagram;

import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.feeds.instagram_rss.data.InstagramPost;
import com.aldaviva.feeds.instagram_rss.data.InstagramUser;

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
import javax.ws.rs.core.Response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstagramServiceImpl implements InstagramService {

	private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(InstagramServiceImpl.class);

	@Autowired private Provider<Client> httpClient;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private PhotoPostReader photoPostReader; //might need concrete classes or more interfaces to inject correct type due to erasure
	@Autowired private VideoPostReader videoPostReader;

	@Override
	public InstagramUser getUser(final String username) throws InstagramException {
		LOGGER.trace("Requesting user profile for {}...", username);
		try {
			final String response = httpClient.get().target(BASE_URI)
			    .path(username)
			    .request()
			    .get(String.class);

			LOGGER.trace("Downloaded profile, extracting...");

			final Pattern pattern = Pattern.compile(".*window\\._sharedData = (.*?);</script>.*", Pattern.DOTALL);
			final Matcher matcher = pattern.matcher(response);
			if(matcher.matches()) {
				final String sharedDataText = matcher.group(1);
				LOGGER.debug("Extracted profile, parsing...");
				LOGGER.trace("Shared data text: {}", sharedDataText);
				final JsonNode sharedData = objectMapper.readTree(sharedDataText);
				LOGGER.debug("Got profile for user {}", username);
				LOGGER.trace("Parsed profile: {}", sharedData);
				return convertSharedDataToUser(sharedData);
			} else {
				throw new InstagramException("No sharedData returned for user " + username);
			}

		} catch (final WebApplicationException e) {
			if(e.getResponse().getStatus() == Status.NOT_FOUND.getStatusCode()) {
				throw new InstagramException.NoSuchUser(username, "There is no Instagram user with the username " + username, e);
			}
			throw new InstagramException("Could not get shared data for user " + username, e);
		} catch (final ProcessingException e) {
			throw new InstagramException("Could not get shared data for user " + username, e);
		} catch (final IOException e) {
			throw new InstagramException("Failed to parse shared data user " + username, e);
		}
	}

	protected InstagramUser convertSharedDataToUser(final JsonNode sharedData) throws InstagramException {
		final InstagramUser user = new InstagramUser();
		final JsonNode rawUser = sharedData.path("entry_data").path("ProfilePage").get(0).get("graphql").path("user");

		user.setBiography(rawUser.get("biography").textValue());
		user.setCsrfToken(sharedData.get("config").get("csrf_token").textValue());
		user.setFullName(rawUser.get("full_name").textValue());
		user.setUserId(rawUser.get("id").asLong());
		user.setUsername(rawUser.get("username").textValue());

		if(rawUser.get("is_private").booleanValue()) {
			throw new InstagramException.PrivateProfile(user.getUsername(), "Unable to retrieve profile of private user " + user.getUsername());
		}

		try {
			user.setProfilePicture(new URI(rawUser.path("profile_pic_url_hd").asText(":malformed_url")));
		} catch (final URISyntaxException e) {
			try {
				user.setProfilePicture(new URI(rawUser.path("profile_pic_url").asText(":malformed_url")));
			} catch (final URISyntaxException e1) {
				LOGGER.warn("Invalid profile picture URL for user {}: {}", user.getUsername(), rawUser.path("profile_pic_url_hd").textValue());
			}
		}

		final JsonNode mediaNodes = rawUser.path("edge_owner_to_timeline_media").path("edges");
		for(final JsonNode mediaNode : mediaNodes) {
			user.getPosts().add(convertMediaNodeToPost(mediaNode.get("node"), user));
		}

		return user;
	}

	protected InstagramPost convertMediaNodeToPost(final JsonNode rawPost, final InstagramUser user) throws InstagramException {
		PostReader<?> postReader;
		if(rawPost.path("is_video").booleanValue()) {
			postReader = videoPostReader;
		} else {
			postReader = photoPostReader;
		}

		return postReader.populatePostDetails(rawPost, user);
	}

}
