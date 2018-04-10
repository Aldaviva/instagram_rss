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
import org.joda.time.format.ISODateTimeFormat;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InstagramServiceTest {

	private InstagramServiceImpl instagramService;
	private MockClientProvider clientProvider;
	private ObjectMapper objectMapper;
	private PhotoPostReaderImpl photoPostReader;
	private VideoPostReaderImpl videoPostReader;

	@BeforeMethod
	private void init() {
		instagramService = new InstagramServiceImpl();
		clientProvider = new MockClientProvider();
		Whitebox.setInternalState(instagramService, clientProvider);
		objectMapper = new ApplicationConfig().objectMapper();
		Whitebox.setInternalState(instagramService, objectMapper);

		photoPostReader = new PhotoPostReaderImpl();
		videoPostReader = new VideoPostReaderImpl();
		Whitebox.setInternalState(instagramService, photoPostReader);
		Whitebox.setInternalState(instagramService, videoPostReader);
		Whitebox.setInternalState(videoPostReader, photoPostReader);
		Whitebox.setInternalState(videoPostReader, clientProvider);
	}

	@Test
	public void convertUser() throws InstagramException, IOException {
		final JsonNode sharedData = objectMapper.readTree(Resources.getResource("json/user_maddangerous.json"));

		final InstagramUser actual = instagramService.convertSharedDataToUser(sharedData);

		assertEquals(actual.getUsername(), "mad.dangerous", "username");
		assertEquals(actual.getUserId(), 2925394782L, "user id");
		assertEquals(actual.getBiography(), "nyc rap producer ⚠️ 1/2 of @bird.language", "bio");
		assertEquals(actual.getCsrfToken(), "0rBt12s5q4VSvhJEoRi1CnImmAZQ0BP9", "csrf token");
		assertEquals(actual.getFullName(), "MAD DANGEROUS", "full name");
		assertEquals(actual.getProfilePicture(), URI.create(
		    "https://instagram.fsnc1-1.fna.fbcdn.net/vp/05e46b12bc2f5538ca06153fdbc3b1d0/5B7115B5/t51.2885-19/s320x320/14709426_1199680913426955_8086617300251181056_a.jpg"),
		    "profile picture");
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
		assertEquals(first.getCode(), "BWfMUzWD22k", "code");
		assertEquals(first.getCaption(), "go follow @bird.language friends", "caption");
		assertEquals(first.getDatePosted(), ISODateTimeFormat.dateTimeParser().parseDateTime("2017-07-13T12:51:04.000Z"), "date posted");
		assertEquals(first.getDisplaySource(), URI.create(
		    "https://instagram.fsnc1-1.fna.fbcdn.net/vp/2e68ebec918ea8bf925540f41b66e892/5B63A3D7/t51.2885-15/e35/19933284_319906195119640_2685433404245147648_n.jpg"),
		    "display src");
		assertEquals(first.getThumbnailSource(), URI.create(
		    "https://instagram.fsnc1-1.fna.fbcdn.net/vp/550f14cbe3a26ef095354007480e05d0/5B527FB6/t51.2885-15/s640x640/sh0.08/e35/19933284_319906195119640_2685433404245147648_n.jpg"),
		    "display src");
		assertEquals(first.getHeight(), 1080, "height");
		assertEquals(first.getWidth(), 1080, "width");
		assertEquals(first.getId(), 1558018202172091812L, "post id");
		assertEquals(first.getOwnerId(), 2925394782L, "owner id");
		assertEquals(first.getPostUri(), URI.create("https://www.instagram.com/p/BWfMUzWD22k"), "post uri");
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
		final InstagramVideoPost video = (InstagramVideoPost) first;
		assertNotNull(video.getVideoUri());
		assertEquals(video.getVideoUri(), URI.create(
		    "https://instagram.fsnc1-1.fna.fbcdn.net/vp/63ac9db3d121bd47d6b1b23f77f2253a/5ACEA71C/t50.2886-16/20001563_113093012661033_5531882267674148864_n.mp4"),
		    "video uri");
		assertEquals(video.getCode(), "BWXmc58DI21", "code");
		assertEquals(video.getCaption(), "in-n-out. #foodporn #animalstyle #toothpix\n🍔❤️🍟: @digitaltwigs", "caption");
		assertEquals(video.getDatePosted(), new DateTime(1499695527L * 1000), "date posted");
		assertEquals(video.getDisplaySource(), URI.create(
		    "https://instagram.fsnc1-1.fna.fbcdn.net/vp/50f52d411b965c6492e3cedc8a6009f1/5ACF2AB3/t51.2885-15/e15/19984860_1392940224133705_3295579576461164544_n.jpg"),
		    "display src");
		assertEquals(video.getThumbnailSource(), URI.create(
		    "https://instagram.fsnc1-1.fna.fbcdn.net/vp/fb3a5c0370e6a00d84d072152462ba95/5ACF05C4/t51.2885-15/s640x640/e15/19984860_1392940224133705_3295579576461164544_n.jpg"),
		    "display src");
		assertEquals(video.getHeight(), 750, "height");
		assertEquals(video.getWidth(), 750, "width");
		assertEquals(video.getId(), 1555881308403305909L, "post id");
		assertEquals(video.getOwnerId(), 3172580342L, "owner id");
		assertEquals(video.getPostUri(), URI.create("https://www.instagram.com/p/BWXmc58DI21"), "post uri");
	}
}
