package com.addressBook.model;

import java.io.Serializable;

public class FacebookUserInfoBean implements Serializable {
	/**
	 * Krishna M
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String userID;
	private String userProfileName;
	private String userImageURL;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID
	 *            the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * @return the userProfileName
	 */
	public String getUserProfileName() {
		return userProfileName;
	}

	/**
	 * @param userProfileName
	 *            the userProfileName to set
	 */
	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	/**
	 * @return the userImageURL
	 */
	public String getUserImageURL() {
		return userImageURL;
	}

	/**
	 * @param userImageURL
	 *            the userImageURL to set
	 */
	public void setUserImageURL(String userImageURL) {
		this.userImageURL = userImageURL;
	}
}
