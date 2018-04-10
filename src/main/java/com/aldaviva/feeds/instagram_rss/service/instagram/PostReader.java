package com.aldaviva.feeds.instagram_rss.service.instagram;

import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.feeds.instagram_rss.data.InstagramPost;
import com.aldaviva.feeds.instagram_rss.data.InstagramUser;

import com.fasterxml.jackson.databind.JsonNode;

public interface PostReader<T extends InstagramPost> {

	public T populatePostDetails(final JsonNode rawPost, final InstagramUser user) throws InstagramException;
}
