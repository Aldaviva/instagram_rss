package com.aldaviva.feeds.instagram_rss.service.instagram;

import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.feeds.instagram_rss.data.InstagramUser;
import com.aldaviva.feeds.instagram_rss.data.InstagramVideoPost;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Provider;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VideoPostReaderImpl implements VideoPostReader {

	@Autowired private Provider<Client> httpClient;
	@Autowired private PhotoPostReader photoPostReader;

	@Override
	public InstagramVideoPost populatePostDetails(final JsonNode rawPost, final InstagramUser user) throws InstagramException {
		final InstagramVideoPost post = InstagramVideoPost.fromPhotoPost(photoPostReader.populatePostDetails(rawPost, user));

		try {
			final JsonNode videoDetails = httpClient.get().target(InstagramService.BASE_URI)
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
