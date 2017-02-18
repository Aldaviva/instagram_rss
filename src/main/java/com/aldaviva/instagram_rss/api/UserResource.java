package com.aldaviva.instagram_rss.api;

import com.aldaviva.instagram_rss.common.exceptions.InstagramException;
import com.aldaviva.instagram_rss.data.InstagramPost;
import com.aldaviva.instagram_rss.data.InstagramUser;
import com.aldaviva.instagram_rss.service.instagram.InstagramService;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Ordering;
import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.module.DCModuleImpl;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Autowired private InstagramService instagramService;

    private static final XMLOutputter XML_OUTPUTTER = new XMLOutputter();

    @GET
    @Path("{username}")
    public InstagramUser getUserProfile(@PathParam("username") final String username) throws InstagramException {
        return instagramService.getUser(username);
    }

    @GET
    @Path("{username}/rss")
    @Produces("application/rss+xml")
    public Channel getUserProfileRssChannel(@PathParam("username") final String username) throws InstagramException {
        final Channel channel = new Channel("rss_2.0");
        channel.setEncoding(StandardCharsets.UTF_8.name());

        final InstagramUser user = getUserProfile(username);
        final String fullName =  MoreObjects.firstNonNull(user.getFullName(), user.getUsername());
        channel.setDescription(MoreObjects.firstNonNull(user.getBiography(), fullName + " on Instagram"));

        channel.setLink(user.getProfileUri().toString());
        channel.setTitle(fullName);
        channel.setWebMaster("ben@aldaviva.com (Ben Hutchison)");

        final Image channelImage = new Image();
        channelImage.setUrl(user.getProfilePicture().toString());
        channelImage.setTitle(channel.getTitle());
        channelImage.setWidth(null);
        channelImage.setHeight(null);
        channelImage.setLink(channel.getLink());
        channel.setImage(channelImage);

        DateTime latestPostDate = null;

        for (final InstagramPost post : user.getPosts()) {
            final Item item = new Item();
            channel.getItems().add(item);

            item.setTitle(post.getCaption());
            item.setLink(post.getPostUri().toString());
            item.setPubDate(post.getDatePosted().toDate());

            final DCModule dc = new DCModuleImpl();
            dc.setCreator(user.getFullName());
            item.getModules().add(dc);

            final Description description = new Description();
            description.setValue(XML_OUTPUTTER.outputString(post.toHtmlElement()));
            item.setDescription(description);

            final Guid guid = new Guid();
            guid.setValue(post.getPostUri().toString());
            guid.setPermaLink(true);
            item.setGuid(guid);

            if (latestPostDate == null) {
                latestPostDate = post.getDatePosted();
            } else {
                latestPostDate = Ordering.natural().max(latestPostDate, post.getDatePosted());
            }
        }

        if (latestPostDate != null) {
            channel.setLastBuildDate(latestPostDate.toDate());
            channel.setPubDate(latestPostDate.toDate());
        }

        return channel;
    }

}
