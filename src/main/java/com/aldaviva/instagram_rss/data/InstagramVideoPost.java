package com.aldaviva.instagram_rss.data;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;
import org.jdom2.Element;

@XmlRootElement
public class InstagramVideoPost extends InstagramPhotoPost {

	private URI videoUri;

	public URI getVideoUri() {
		return videoUri;
	}

	public void setVideoUri(final URI videoUri) {
		this.videoUri = videoUri;
	}

	@Override
	public Element toHtmlElement() {
		final Element div = new Element("div");
		final Element video = new Element("video");
		div.addContent(video);
		video.setAttribute("src", getVideoUri().toString());
		video.setAttribute("poster", getDisplaySource().toString());
		video.setAttribute("loop", "true");
		video.setAttribute("type", "video/mp4");
		final Element p = new Element("p");
		p.setText(getCaption());
		div.addContent(p);
		return div;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((videoUri == null) ? 0 : videoUri.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		final InstagramVideoPost other = (InstagramVideoPost) obj;
		if(videoUri == null) {
			if(other.videoUri != null)
				return false;
		} else if(!videoUri.equals(other.videoUri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InstagramVideoPost [videoUri=" + videoUri + ", getCaption()=" + getCaption() + ", getCode()=" + getCode() + ", getDisplaySource()="
		    + getDisplaySource() + ", getHeight()=" + getHeight() + ", getId()=" + getId() + ", getOwnerId()=" + getOwnerId() + ", getDatePosted()="
		    + getDatePosted() + ", getThumbnailSource()=" + getThumbnailSource() + ", getWidth()=" + getWidth() + ", getPostUri()=" + getPostUri() + "]";
	}

}
