package com.addressBook.util;

import android.app.Dialog;
import android.os.Handler;

public class AppConstants {

	public static final String EMPTY_STRING = "";

	public static Dialog dialog;

	public static Handler mProgressHandler;

	public static final String USERNAME_AND_PASSWORD_REQ = "Please enter User Name  and password.";

	public static final String PASSWORD_FIELD_EMPTY = "Password field is empty";

	public static final String PASSWORD_NOT_MATCH = "Password does not match";

	public static final String USERNAME_REQ = "Please enter your User Name.";

	public static final String LOGIN_EMAIL_ADD_INVALID = "Please enter valid User Name.";

	public static final String LOGIN_PASSWORD_REQ = "Please enter password.";

	public static final String EMAIL = "email";

	public static final String PASSWORD = "password";

	public static final String SETTINGS_USERNAME = "username";

	public static final String SETTINGS_PASSWORD = "password";

	public static final String SETTINGS_REMEMBERME = "rememberme";
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String[] GENDER = { "Male", "Female" };

	// Instagram
	private static final String AUTHURL = "https://api.instagram.com/oauth/authorize/";
	private static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
	public static final String APIURL = "https://api.instagram.com/v1";
	public static String CALLBACKURL = "Your Redirect URI";
}
