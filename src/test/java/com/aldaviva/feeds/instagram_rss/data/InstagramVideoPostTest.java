package com.aldaviva.feeds.instagram_rss.data;

import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.*;

import java.net.URI;
import org.jdom2.Element;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InstagramVideoPostTest {

    private InstagramVideoPost post;

    @BeforeMethod
    private void init(){
        post = new InstagramVideoPost();
        post.setDisplaySource(URI.create("http://instagram.com/display_source"));
        post.setVideoUri(URI.create("http://instagram.com/video"));
        post.setDatePosted(new DateTime());
        post.setCaption("caption");
        post.setCode("code");
        post.setHeight(100);
        post.setId(12345);
        post.setOwnerId(67890);
        post.setThumbnailSource(URI.create("http://instagram.com/thumbnail"));
        post.setWidth(100);
    }

    @Test
    public void toHtmlElement(){
        Element actual = post.toHtmlElement();
        assertEquals(actual.getChild("video").getAttributeValue("src"), "http://instagram.com/video", "src");
        assertEquals(actual.getChild("video").getAttributeValue("poster"), "http://instagram.com/display_source", "poster");
        assertEquals(actual.getChild("video").getAttributeValue("loop"), "true", "loop");
        assertEquals(actual.getChild("video").getAttributeValue("type"), "video/mp4", "type");
        assertEquals(actual.getChild("p").getText(), "caption", "caption");
    }

    @Test
    public void toString_(){
        assertThat(post.toString(), isA(String.class));
    }

    @Test
    public void hashCode_(){
        assertThat(post.hashCode(), isA(Integer.class));
    }
}
