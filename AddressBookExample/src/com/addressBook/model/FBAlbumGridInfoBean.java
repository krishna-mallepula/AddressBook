package com.addressBook.model;

import android.text.TextUtils;

public class FBAlbumGridInfoBean {
	private boolean selected;

    private boolean uploaded;

    private String printUrl; // URL used to post cart

    private String id;

    private String sourceURL; // For displaying thumbnail

    public void setPrintUrl(String url) {
        printUrl = url;
    }

    /**
     * @return the sourceUrl
     */
    public String getPrintUrl() {
        return printUrl;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return the uploaded
     */
    public boolean isUploaded() {
        return uploaded;
    }

    /**
     * @param uploaded the uploaded to set
     */
    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public static String getBestImageUrl(String srcBigUrl, String imgArrayUrl, int srcBigHeight,
            int srcBigWidth) {
        
       

        if (!TextUtils.isEmpty(srcBigUrl)) {

            if (srcBigHeight == 0 || srcBigWidth == 0 || srcBigHeight >= com.facebook.android.Util.MIN_SRC_BIG_DIMENSION
                    || srcBigWidth >= com.facebook.android.Util.MIN_SRC_BIG_DIMENSION) {

                return imgArrayUrl;
            }
        }
        else{
            return imgArrayUrl;
        }

        return srcBigUrl;
    }
}
