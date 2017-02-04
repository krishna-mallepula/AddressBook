
package com.addressBook.model;

import com.addressBook.util.Common;
import com.facebook.android.Util;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author Krishna M
 */
public class InstagramGridBeanInfo {

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
        
        if (Common.DEBUG) {
            Log.d("FBAlbumGridInfoBean.java - getBestImageUrl() ", "srcBigUrl : " + srcBigUrl);
            Log.d("FBAlbumGridInfoBean.java - getBestImageUrl() ", "imgArrayUrl : " + imgArrayUrl);
            Log.d("FBAlbumGridInfoBean.java - getBestImageUrl() ", "srcBigHeight : " + srcBigHeight);
            Log.d("FBAlbumGridInfoBean.java - getBestImageUrl() ", "srcBigWidth : " + srcBigWidth);
        }

        if (!TextUtils.isEmpty(srcBigUrl)) {

            if (srcBigHeight == 0 || srcBigWidth == 0 || srcBigHeight >= Util.MIN_SRC_BIG_DIMENSION
                    || srcBigWidth >= Util.MIN_SRC_BIG_DIMENSION) {

                return imgArrayUrl;
            }
        }
        else{
            return imgArrayUrl;
        }

        return srcBigUrl;
    }
}
