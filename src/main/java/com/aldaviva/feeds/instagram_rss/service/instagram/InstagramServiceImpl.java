package com.aldaviva.feeds.instagram_rss.service.instagram;

import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.feeds.instagram_rss.data.InstagramPhotoPost;
import com.aldaviva.feeds.instagram_rss.data.InstagramPost;
import com.aldaviva.feeds.instagram_rss.data.InstagramUser;
import com.aldaviva.feeds.instagram_rss.data.InstagramVideoPost;

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
		final JsonNode rawUser = sharedData.path("entry_data").path("ProfilePage").get(0).path("user");

		user.setBiography(rawUser.path("biography").textValue());
		user.setCsrfToken(sharedData.path("config").path("csrf_token").textValue());
		user.setFullName(rawUser.path("full_name").textValue());
		user.setUserId(rawUser.path("id").asLong());
		user.setUsername(rawUser.path("username").textValue());

		if(rawUser.path("is_private").booleanValue()) {
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

		final JsonNode mediaNodes = rawUser.path("media").path("nodes");
		for(final JsonNode mediaNode : mediaNodes) {
			user.getPosts().add(convertMediaNodeToPost(mediaNode, user));
		}

		return user;
	}

	protected InstagramPost convertMediaNodeToPost(final JsonNode rawPost, final InstagramUser user) throws InstagramException {
		final InstagramPost post;
		if(rawPost.path("is_video").booleanValue()) {
			post = populatePostDetails(rawPost, new InstagramVideoPost(), user);
		} else {
			post = populatePostDetails(rawPost, new InstagramPhotoPost(), user);
		}

		return post;
	}

	protected InstagramPhotoPost populatePostDetails(final JsonNode rawPost, final InstagramPhotoPost post, final InstagramUser user)
	    throws InstagramException {
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

	protected InstagramVideoPost populatePostDetails(final JsonNode rawPost, final InstagramVideoPost post, final InstagramUser user)
	    throws InstagramException {
		populatePostDetails(rawPost, (InstagramPhotoPost) post, user);

		try {
			final JsonNode videoDetails = httpClient.get().target(BASE_URI)
			    .path("p")
			    .path(post.getCode())
			    .queryParam("__a", 1) //otherwise Instagram returns HTML
			    .request()
			    .get(JsonNode.class);

			post.setVideoUri(new URI(videoDetails.path("graphql").path("shortcode_media").path("video_url").textValue()));
		} catch (final URISyntaxException e) {
			throw new InstagramException("Illegal video URL for " + user.getUsername() + "'s post " + post.getPostUri(), e);
		} catch (WebApplicationException | ProcessingException e) {
			throw new InstagramException("Could not get video info for user " + user.getUsername(), e);
		}

		return post;
	}

}
