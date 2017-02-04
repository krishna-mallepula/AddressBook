package com.addressBook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.Facebook;
import com.facebook.android.Util;

public class FBConnectQueryProcessor {
	public static final String GET_PHOTO_LIST = "SELECT src,src_big,src_big_height,src_big_width,images FROM photo WHERE aid=";
	public static final String GET_ALBUM_LIST = "SELECT aid,object_id,name,photo_count,cover_object_id,type,visible,cover_pid FROM album WHERE owner=me()";

	public static final String GET_TAGGED_LIST = "SELECT object_id,pid FROM photo WHERE object_id IN (SELECT object_id FROM photo_tag WHERE subject=me())";
	public static final String GET_TAGGED_PHOTO_LIST = "SELECT src,src_big,src_big_height,src_big_width,images FROM photo WHERE object_id IN (SELECT object_id FROM photo_tag WHERE subject=me())";

	public static final String GET_APPLICATION_INFO = "SELECT display_name,namespace,app_id FROM application WHERE app_id=";

	public String requestQuery(Context context, Facebook facebook, String query)
			throws FileNotFoundException, MalformedURLException, IOException {
		Bundle parameters = new Bundle();

		if (facebook.isSessionValid()) {
			// parameters.putString("access_token",facebookClient.getAccessToken());
		}
		String url = (query != null) ? "https://graph.facebook.com/fql?&access_token="
				+ SessionStore.getAccessTocken(context)
				+ "&q="
				+ URLEncoder.encode(query)
				: "https://api.facebook.com/restserver.php";
		return Util.openUrl(url, "GET", parameters);
	}

	public String requestAlbumList(Context context, Facebook facebook)
			throws FileNotFoundException, MalformedURLException, IOException {
		Bundle parameters = new Bundle();

		if (facebook.isSessionValid()) {
			// parameters.putString("access_token",facebookClient.getAccessToken());
		}
		String url = "https://graph.facebook.com/fql?&access_token="
				+ SessionStore.getAccessTocken(context) + "&q="
				+ URLEncoder.encode(GET_ALBUM_LIST);
		String response = Util.openUrl(url, "GET", parameters);

		return response;
	}

	public String requestTaggedPhotoCount(Context context, Facebook facebook)
			throws FileNotFoundException, MalformedURLException, IOException {
		Bundle parameters = new Bundle();

		if (facebook.isSessionValid()) {
			// parameters.putString("access_token",facebookClient.getAccessToken());
		}
		String url = "https://graph.facebook.com/fql?&access_token="
				+ SessionStore.getAccessTocken(context) + "&q="
				+ URLEncoder.encode(GET_TAGGED_LIST);
		String response = Util.openUrl(url, "GET", parameters);

		return response;
	}

	public String requestPhotoList(Context context, Facebook facebook,
			String albumID) throws FileNotFoundException,
			MalformedURLException, IOException {
		Bundle parameters = new Bundle();

		if (facebook.isSessionValid()) {
			// parameters.putString("access_token",facebookClient.getAccessToken());
		}
		String url;
		if (albumID.equals(Util.TAGGED_PHOTOS_ID)) {
			url = (albumID != null) ? "https://graph.facebook.com/fql?&access_token="
					+ SessionStore.getAccessTocken(context)
					+ "&q="
					+ URLEncoder.encode(GET_TAGGED_PHOTO_LIST)
					: "https://api.facebook.com/restserver.php";
		} else {
			url = (albumID != null) ? "https://graph.facebook.com/fql?&access_token="
					+ SessionStore.getAccessTocken(context)
					+ "&q="
					+ URLEncoder.encode(GET_PHOTO_LIST + "'" + albumID + "'")
					: "https://api.facebook.com/restserver.php";
		}

		String response = Util.openUrl(url, "GET", parameters);

		return response;
	}

	public String requestUserInfo(Context context,
			com.facebook.android.Facebook facebook)
			throws FileNotFoundException, MalformedURLException, IOException {
		Bundle parameters = new Bundle();
		parameters.putString("fields",
				"id, name, username, picture&type=normal");
		if (facebook.isSessionValid()) {
			parameters.putString("access_token",
					SessionStore.getAccessTocken(context));
		}
		String url = "https://graph.facebook.com/";
		return Util.openUrl(url, "me", parameters);
	}

	public String getApplicationInfo(Context context,
			com.facebook.android.Facebook facebook, String applicationKey)
			throws FileNotFoundException, MalformedURLException, IOException,
			SocketTimeoutException, Exception {
		Bundle parameters = new Bundle();

		if (facebook.isSessionValid()) {
			// parameters.putString("access_token",facebookClient.getAccessToken());
		}
		String url = "https://graph.facebook.com/fql?&access_token="
				+ SessionStore.getAccessTocken(context)
				+ "&q="
				+ URLEncoder.encode(GET_APPLICATION_INFO + "'" + applicationKey
						+ "'");
		// : "https://api.facebook.com/restserver.php";

		String response = Util.openUrl(url, "GET", parameters);

		return response;
	}
}
