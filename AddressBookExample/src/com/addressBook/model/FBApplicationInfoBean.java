package com.addressBook.model;

public class FBApplicationInfoBean {
	private String applicationDisplayName;
	private String applicationNameSpace;
	private String applicationID;

	/**
	 * @return the applicationDisplayName
	 */
	public String getApplicationDisplayName() {
		return applicationDisplayName;
	}

	/**
	 * @param applicationDisplayName
	 *            the applicationDisplayName to set
	 */
	public void setApplicationDisplayName(String applicationDisplayName) {
		this.applicationDisplayName = applicationDisplayName;
	}

	/**
	 * @return the applicationNameSpace
	 */
	public String getApplicationNameSpace() {
		return applicationNameSpace;
	}

	/**
	 * @param applicationNameSpace
	 *            the applicationNameSpace to set
	 */
	public void setApplicationNameSpace(String applicationNameSpace) {
		this.applicationNameSpace = applicationNameSpace;
	}

	/**
	 * @return the applicationID
	 */
	public String getApplicationID() {
		return applicationID;
	}

	/**
	 * @param applicationID
	 *            the applicationID to set
	 */
	public void setApplicationID(String applicationID) {
		this.applicationID = applicationID;
	}
}
