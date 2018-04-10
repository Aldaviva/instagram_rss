package com.aldaviva.feeds.instagram_rss.service.instagram;

import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.feeds.instagram_rss.data.InstagramPhotoPost;
import com.aldaviva.feeds.instagram_rss.data.InstagramUser;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.URISyntaxException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class PhotoPostReaderImpl implements PhotoPostReader {

	@Override
	public InstagramPhotoPost populatePostDetails(final JsonNode rawPost, final InstagramUser user) throws InstagramException {
		final InstagramPhotoPost post = new InstagramPhotoPost();

		post.setCaption(rawPost.path("edge_media_to_caption").path("edges").path(0).path("node").path("text").textValue());
		post.setDatePosted(new DateTime(rawPost.path("taken_at_timestamp").asLong() * 1000));
		post.setHeight(rawPost.path("dimensions").path("height").intValue());
		post.setWidth(rawPost.path("dimensions").path("width").intValue());
		post.setId(rawPost.path("id").asLong());
		post.setOwnerId(rawPost.path("owner").path("id").asLong());
		post.setCode(rawPost.path("shortcode").textValue());

		try {
			post.setDisplaySource(new URI(rawPost.path("display_url").textValue()));
			post.setThumbnailSource(new URI(rawPost.path("thumbnail_src").textValue()));
		} catch (final URISyntaxException e) {
			throw new InstagramException("Illegal display or thumbnail URL for " + user.getUsername() + "'s post " + post.getPostUri(), e);
		}

		return post;
	}

}
