package com.aldaviva.feeds.instagram_rss.data;

import com.aldaviva.feeds.instagram_rss.service.instagram.InstagramService;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdom2.Element;
import org.joda.time.DateTime;

@XmlRootElement
public class InstagramPhotoPost implements InstagramPost {

	private String caption;
	private String code;
	private URI displaySource;
	private int height;
	private long id;
	private long ownerId;
	private DateTime datePosted;
	private URI thumbnailSource;
	private int width;

	@Override
	public String getCaption() {
		return caption;
	}

	@Override
	public void setCaption(final String caption) {
		this.caption = caption;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(final String code) {
		this.code = code;
	}

	@Override
	public URI getDisplaySource() {
		return displaySource;
	}

	@Override
	public void setDisplaySource(final URI displaySource) {
		this.displaySource = displaySource;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(final int height) {
		this.height = height;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(final long id) {
		this.id = id;
	}

	@Override
	public long getOwnerId() {
		return ownerId;
	}

	@Override
	public void setOwnerId(final long ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public DateTime getDatePosted() {
		return datePosted;
	}

	@Override
	public void setDatePosted(final DateTime datePosted) {
		this.datePosted = datePosted;
	}

	@Override
	public URI getThumbnailSource() {
		return thumbnailSource;
	}

	@Override
	public void setThumbnailSource(final URI thumbnailSource) {
		this.thumbnailSource = thumbnailSource;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(final int width) {
		this.width = width;
	}

	@Override
	public URI getPostUri() {
		return InstagramService.BASE_URI.resolve("/p/" + code);
	}

	@Override
	public Element toHtmlElement() {
		final Element div = new Element("div");
		final Element img = new Element("img");
		div.addContent(img);
		img.setAttribute("src", getDisplaySource().toString());
		final Element p = new Element("p");
		p.setText(getCaption());
		div.addContent(p);
		return div;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caption == null) ? 0 : caption.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((datePosted == null) ? 0 : datePosted.hashCode());
		result = prime * result + ((displaySource == null) ? 0 : displaySource.hashCode());
		result = prime * result + height;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (ownerId ^ (ownerId >>> 32));
		result = prime * result + ((thumbnailSource == null) ? 0 : thumbnailSource.hashCode());
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		final InstagramPhotoPost other = (InstagramPhotoPost) obj;
		if(caption == null) {
			if(other.caption != null)
				return false;
		} else if(!caption.equals(other.caption))
			return false;
		if(code == null) {
			if(other.code != null)
				return false;
		} else if(!code.equals(other.code))
			return false;
		if(datePosted == null) {
			if(other.datePosted != null)
				return false;
		} else if(!datePosted.equals(other.datePosted))
			return false;
		if(displaySource == null) {
			if(other.displaySource != null)
				return false;
		} else if(!displaySource.equals(other.displaySource))
			return false;
		if(height != other.height)
			return false;
		if(id != other.id)
			return false;
		if(ownerId != other.ownerId)
			return false;
		if(thumbnailSource == null) {
			if(other.thumbnailSource != null)
				return false;
		} else if(!thumbnailSource.equals(other.thumbnailSource))
			return false;
		if(width != other.width)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InstagramPost [caption=" + caption + ", code=" + code + ", displaySource=" + displaySource + ", height=" + height + ", id=" + id + ", ownerId="
		    + ownerId + ", datePosted=" + datePosted + ", thumbnailSource=" + thumbnailSource + ", width=" + width + "]";
	}

}