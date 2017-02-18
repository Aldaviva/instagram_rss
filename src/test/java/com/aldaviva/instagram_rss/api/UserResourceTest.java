package com.aldaviva.instagram_rss.api;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

import com.aldaviva.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.instagram_rss.config.JerseyConfig;
import com.aldaviva.instagram_rss.config.SpringConfig;
import com.aldaviva.instagram_rss.config.TestConfig;
import com.aldaviva.instagram_rss.data.InstagramPhotoPost;
import com.aldaviva.instagram_rss.data.InstagramPost;
import com.aldaviva.instagram_rss.data.InstagramUser;
import com.aldaviva.instagram_rss.service.instagram.InstagramService;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.Collections;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.joda.time.DateTime;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

public class UserResourceTest extends JerseyTestNg.ContainerPerClassTest {

    private AnnotationConfigWebApplicationContext springContext;

    @Override
    protected ResourceConfig configure() {
        JerseyConfig jerseyConfig = new JerseyConfig();

        springContext = new SpringConfig().initSpring();
        springContext.register(TestConfig.class);
        springContext.refresh();
        jerseyConfig.property("contextConfig", springContext);
        return jerseyConfig;
    }

    @Test
    public void getUserProfile() throws InstagramException {
        InstagramService instagramService = springContext.getBean(InstagramService.class);
        InstagramUser user = new InstagramUser();
        user.setUsername("dril");
        when(instagramService.getUser(anyString())).thenReturn(user);

        JsonNode actual = target("/users/dril").request().get(JsonNode.class);
        assertEquals(actual.path("username").textValue(), "dril", "username");
    }

    @Test
    public void getUserProfileRssChannel() throws InstagramException {
        InstagramService instagramService = springContext.getBean(InstagramService.class);
        InstagramUser user = new InstagramUser();
        user.setUsername("dril");
        user.setFullName("wint");
        user.setProfilePicture(URI.create("http://test.com/userprofile/picture.jpg"));
        InstagramPost post = new InstagramPhotoPost();
        user.setPosts(Collections.singletonList(post));
        post.setDatePosted(new DateTime());
        post.setDisplaySource(URI.create("http://test.com/displaysource"));
        when(instagramService.getUser(anyString())).thenReturn(user);

        Document actual = target("/users/dril/rss").request().get(Document.class);
        assertNotNull(actual);
        //TODO import XmlTestUtils so we can assert that XPath queries return what we expect
    }

    @Test
    public void userFullNameCanBeNull() throws InstagramException {
        InstagramService instagramService = springContext.getBean(InstagramService.class);
        InstagramUser user = new InstagramUser();
        user.setUsername("dril");
        user.setFullName(null);
        user.setProfilePicture(URI.create("http://test.com/userprofile/picture.jpg"));
        InstagramPost post = new InstagramPhotoPost();
        user.setPosts(Collections.singletonList(post));
        post.setDatePosted(new DateTime());
        post.setDisplaySource(URI.create("http://test.com/displaysource"));
        when(instagramService.getUser(anyString())).thenReturn(user);

        Document actual = target("/users/dril/rss").request().get(Document.class);
        assertNotNull(actual);
    }
}
