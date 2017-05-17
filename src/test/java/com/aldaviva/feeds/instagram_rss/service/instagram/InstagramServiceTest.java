package com.aldaviva.feeds.instagram_rss.service.instagram;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.aldaviva.feeds.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.feeds.instagram_rss.config.ApplicationConfig;
import com.aldaviva.feeds.instagram_rss.data.InstagramPost;
import com.aldaviva.feeds.instagram_rss.data.InstagramUser;
import com.aldaviva.feeds.instagram_rss.data.InstagramVideoPost;
import vc.bjn.catalyst.test.jersey.client.MockClientProvider;
import vc.bjn.catalyst.test.jersey.client.TestBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import org.joda.time.DateTime;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InstagramServiceTest {

	private InstagramServiceImpl instagramService;
	private MockClientProvider clientProvider;
	private ObjectMapper objectMapper;

	@BeforeMethod
	private void init() {
		instagramService = new InstagramServiceImpl();
		clientProvider = new MockClientProvider();
		Whitebox.setInternalState(instagramService, clientProvider);
		objectMapper = new ApplicationConfig().objectMapper();
		Whitebox.setInternalState(instagramService, objectMapper);
	}

	@Test
	public void converUser() throws InstagramException, IOException {
		final JsonNode sharedData = objectMapper.readTree(Resources.getResource("json/user_maddangerous.json"));

		final InstagramUser actual = instagramService.convertSharedDataToUser(sharedData);

		assertEquals(actual.getUsername(), "mad.dangerous", "username");
		assertEquals(actual.getUserId(), 2925394782L, "user id");
		assertEquals(actual.getBiography(), "nyc rap production duo ⚠️ dm for inquiries", "bio");
		assertEquals(actual.getCsrfToken(), "8bbc786357f425025e85462cb0050b4c", "csrf token");
		assertEquals(actual.getFullName(), "MAD DANGEROUS", "full name");
		assertEquals(actual.getProfilePicture(), URI.create("https://instagram.fsnc1-2.fna.fbcdn.net"
		    + "/t51.2885-19/s320x320/12716662_429716247234099_1880047741_a.jpg"), "profile picture");
		assertEquals(actual.getProfileUri(), URI.create("https://www.instagram.com/mad.dangerous"), "profile uri");
	}

	@Test
	public void convertPosts() throws InstagramException, IOException {
		final JsonNode sharedData = objectMapper.readTree(Resources.getResource("json/user_maddangerous.json"));

		final InstagramUser user = instagramService.convertSharedDataToUser(sharedData);
		assertNotNull(user, "converted user");

		final List<InstagramPost> actual = user.getPosts();
		assertEquals(actual.size(), 12, "number of posts");

		final InstagramPost first = actual.get(0);
		assertEquals(first.getCode(), "BKbmpipAIOs", "code");
		assertEquals(first.getCaption(), "last night was very dangerous ⚠️", "caption");
		assertEquals(first.getDatePosted(), new DateTime(1474060044000L), "date posted");
		assertEquals(first.getDisplaySource(), URI.create("https://instagram.fsnc1-2.fna.fbcdn.net"
		    + "/l/t51.2885-15/e35/14280374_796333310509012_122090974_n.jpg?ig_cache_key=MTM0MDgzNTI5NDUzMzk0NDIzNg%3D%3D.2"), "display src");
		assertEquals(first.getThumbnailSource(), URI.create("https://instagram.fsnc1-2.fna.fbcdn.net"
		    + "/l/t51.2885-15/s640x640/sh0.08/e35/c107.0.866.866/14280374_796333310509012_122090974_n.jpg?ig_cache_key=MTM0MDgzNTI5NDUzMzk0NDIzNg%3D%3D.2.c"),
		    "display src");
		assertEquals(first.getHeight(), 866, "height");
		assertEquals(first.getWidth(), 1080, "width");
		assertEquals(first.getId(), 1340835294533944236L, "post id");
		assertEquals(first.getOwnerId(), 2925394782L, "owner id");
		assertEquals(first.getPostUri(), URI.create("https://www.instagram.com/p/BKbmpipAIOs"), "post uri");
	}

	@Test
	public void downloadProfile() throws ProcessingException, WebApplicationException, IOException, InstagramException {
		final TestBuilder request = clientProvider.addNewMockBuilder();
		when(request.get(String.class)).thenReturn(Resources.toString(Resources.getResource("html/profile_maddangerous.html"), StandardCharsets.UTF_8));

		final InstagramUser actual = instagramService.getUser("mad.dangerous");
		assertEquals(actual.getFullName(), "MAD DANGEROUS", "full name");

		verify(request).get(String.class);
		verify(request).requested("https://www.instagram.com/mad.dangerous");
	}

	@Test
	public void convertVideoPost() throws IOException, InstagramException {
		final JsonNode sharedData = objectMapper.readTree(Resources.getResource("json/user_toothpix.json"));
		final JsonNode videoData = objectMapper.readTree(Resources.getResource("json/video_toothpix0.json"));

		final TestBuilder request = mock(TestBuilder.class);
		when(request.get(JsonNode.class)).thenReturn(videoData);
		for(int i = 0; i < 12; i++) {
			//return the same JSON response for all 12 requests
			clientProvider.enqueueMockBuilder(request);
		}

		final InstagramUser user = instagramService.convertSharedDataToUser(sharedData);
		assertNotNull(user, "converted user");

		final List<InstagramPost> actual = user.getPosts();
		assertEquals(actual.size(), 12, "number of posts");

		final InstagramPost first = actual.get(0);
		assertThat(first, instanceOf(InstagramVideoPost.class));
		assertNotNull(((InstagramVideoPost) first).getVideoUri());
	}
}
