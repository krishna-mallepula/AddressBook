package com.addressBook.model;

public class PhotoItem {
	private String pictureUrl;
	private String sourceUrl;

	public PhotoItem(String pictureUrl, String sourceUrl) {
		this.pictureUrl = pictureUrl;
		this.sourceUrl = sourceUrl;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
}
