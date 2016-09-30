package com.aldaviva.instagram_rss.data;

import com.aldaviva.instagram_rss.service.instagram.InstagramService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstagramUser {

	private String biography;
	private String csrfToken;
	private String fullName;
	private URI profilePicture;
	private long userId;
	private String username;
	private List<InstagramPost> posts = new ArrayList<>();

	public String getBiography() {
		return biography;
	}

	public void setBiography(final String biography) {
		this.biography = biography;
	}

	public String getCsrfToken() {
		return csrfToken;
	}

	public void setCsrfToken(final String csrfToken) {
		this.csrfToken = csrfToken;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	public URI getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(final URI profilePicture) {
		this.profilePicture = profilePicture;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(final long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public List<InstagramPost> getPosts() {
		return posts;
	}

	public void setPosts(final List<InstagramPost> posts) {
		this.posts = posts;
	}

	public URI getProfileUri() {
		return InstagramService.BASE_URI.resolve("/" + username);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((biography == null) ? 0 : biography.hashCode());
		result = prime * result + ((csrfToken == null) ? 0 : csrfToken.hashCode());
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((posts == null) ? 0 : posts.hashCode());
		result = prime * result + ((profilePicture == null) ? 0 : profilePicture.hashCode());
		result = prime * result + (int) (userId ^ (userId >>> 32));
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		final InstagramUser other = (InstagramUser) obj;
		if(biography == null) {
			if(other.biography != null)
				return false;
		} else if(!biography.equals(other.biography))
			return false;
		if(csrfToken == null) {
			if(other.csrfToken != null)
				return false;
		} else if(!csrfToken.equals(other.csrfToken))
			return false;
		if(fullName == null) {
			if(other.fullName != null)
				return false;
		} else if(!fullName.equals(other.fullName))
			return false;
		if(posts == null) {
			if(other.posts != null)
				return false;
		} else if(!posts.equals(other.posts))
			return false;
		if(profilePicture == null) {
			if(other.profilePicture != null)
				return false;
		} else if(!profilePicture.equals(other.profilePicture))
			return false;
		if(userId != other.userId)
			return false;
		if(username == null) {
			if(other.username != null)
				return false;
		} else if(!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InstagramUser [biography=" + biography + ", csrfToken=" + csrfToken + ", fullName=" + fullName + ", profilePicture=" + profilePicture
		    + ", userId=" + userId + ", username=" + username + "]";
	}

}
