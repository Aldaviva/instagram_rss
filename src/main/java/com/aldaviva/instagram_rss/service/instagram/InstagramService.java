package com.aldaviva.instagram_rss.service.instagram;

import com.aldaviva.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.instagram_rss.data.InstagramUser;

import java.net.URI;

public interface InstagramService {

	static final URI BASE_URI = URI.create("https://www.instagram.com");

	InstagramUser getUser(String username) throws InstagramException;

}
