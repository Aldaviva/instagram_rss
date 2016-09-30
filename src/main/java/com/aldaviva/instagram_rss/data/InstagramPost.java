package com.aldaviva.instagram_rss.data;

import com.aldaviva.instagram_rss.service.instagram.InstagramService;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;
import org.joda.time.DateTime;

@XmlRootElement
public class InstagramPost {

	private String caption;
	private String code;
	private URI displaySource;
	private int height;
	private long id;
	private long ownerId;
	private DateTime datePosted;
	private URI thumbnailSource;
	private int width;

	public String getCaption() {
		return caption;
	}

	public void setCaption(final String caption) {
		this.caption = caption;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public URI getDisplaySource() {
		return displaySource;
	}

	public void setDisplaySource(final URI displaySource) {
		this.displaySource = displaySource;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(final long ownerId) {
		this.ownerId = ownerId;
	}

	public DateTime getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(final DateTime datePosted) {
		this.datePosted = datePosted;
	}

	public URI getThumbnailSource() {
		return thumbnailSource;
	}

	public void setThumbnailSource(final URI thumbnailSource) {
		this.thumbnailSource = thumbnailSource;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	public URI getPostUri() {
		return InstagramService.BASE_URI.resolve("/p/" + code);
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
		final InstagramPost other = (InstagramPost) obj;
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