package com.aldaviva.feeds.instagram_rss.data;

import java.net.URI;
import org.jdom2.Element;
import org.joda.time.DateTime;

public interface InstagramPost {

	String getCaption();

	void setCaption(String caption);

	String getCode();

	void setCode(String code);

	URI getDisplaySource();

	void setDisplaySource(URI displaySource);

	int getHeight();

	void setHeight(int height);

	long getId();

	void setId(long id);

	long getOwnerId();

	void setOwnerId(long ownerId);

	DateTime getDatePosted();

	void setDatePosted(DateTime datePosted);

	URI getThumbnailSource();

	void setThumbnailSource(URI thumbnailSource);

	int getWidth();

	void setWidth(int width);

	URI getPostUri();

	Element toHtmlElement();

}