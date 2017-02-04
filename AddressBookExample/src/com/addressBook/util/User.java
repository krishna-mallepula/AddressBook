package com.addressBook.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.Window;

public class User {
	public static final String FACEBOOK_APP_ID = "494151010600038";
	private static User sUtil = null;

	public static String PREFS_NAME = "Auth";
	SharedPreferences.Editor e;

	public static Handler mProgressHandler;

	public static Dialog dialog;

	private static Context mContext;

	private static boolean mCancelable = false;

	private boolean isAuthenticated;

	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getRememberUsername() {
		return rememeberUsername;
	}

	public void setRememeberUsername(boolean rememeberUsername) {
		this.rememeberUsername = rememeberUsername;
	}

	public boolean getRememberPassword() {
		return rememberPassword;
	}

	public void setRememberPassword(boolean rememberPassword) {
		this.rememberPassword = rememberPassword;
	}

	private String password;

	private boolean rememeberUsername;

	private boolean rememberPassword;

	private String authCode;
	private SharedPreferences settings;
	public static final String ENCRYPTION_KEY = "phW5854acbc576=";

	private static void writeToDB(Context context,
			OutputStream databaseOutputStream, int rawResource)
			throws Exception {
		InputStream databaseInputStream;
		int length = 0;
		byte[] buffer = new byte[1024];

		databaseInputStream = context.getResources().openRawResource(
				rawResource);
		while ((length = databaseInputStream.read(buffer)) > 0) {
			databaseOutputStream.write(buffer);
		}
		databaseInputStream.close();
	}

	public static boolean validateEmail(String strEmailId) {
		if (!(strEmailId instanceof String)
				|| strEmailId.equals(AppConstants.EMPTY_STRING)) {
			return false;
		}
		String expression = "!#$^'&*()+|}{[]?><~%:;/\\,=`\"";
		String numbers = "0123456789";
		char at = '@';
		char dot = '.';
		int lat = strEmailId.indexOf(at);
		int len = strEmailId.length() - 1;
		if (numbers.indexOf(strEmailId.charAt(0)) != -1) {
			return false;
		}
		if (strEmailId.indexOf(" ") != -1) {
			return false;
		}

		if ((len - strEmailId.lastIndexOf(dot)) < 2) {
			return false;
		}
		if (strEmailId.lastIndexOf(at) == len
				|| strEmailId.lastIndexOf(dot) == len) {
			return false;
		}
		if (strEmailId.indexOf(at) == -1 || strEmailId.indexOf(at) == 0
				|| strEmailId.indexOf(at) == len) {
			return false;
		}
		if (strEmailId.indexOf(dot) == -1 || strEmailId.indexOf(dot) == 0
				|| strEmailId.indexOf(dot) == len) {
			return false;
		}
		if (strEmailId.indexOf(dot, (lat + 2)) == -1) {
			return false;
		}
		int strEmailIdLength = strEmailId.length();
		for (int i = 0; i < strEmailIdLength; i++) {
			char c = strEmailId.charAt(i);
			if (expression.indexOf(c) != -1) {
				return false;
			}
		}
		return true;
	}

	public static void showSpinnerDialog(Context context, boolean cancelable) {

		try {
			mContext = context;
			mProgressHandler = new Handler();
			mProgressHandler.post(mShowCustomSpinnerDialog);
			mCancelable = cancelable;
		} catch (Exception e) {
			dialog = null;
		}
	}

	public static Runnable mShowCustomSpinnerDialog = new Runnable() {

		public void run() {
			try {
				showSpinner(mContext);
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
	};

	private static void showSpinner(Context ctx) {
		try {
			if (isDialogShown()) {
				dismisssSpinnerDialog();
			}
			dialog = new Dialog(ctx, android.R.style.Theme_Translucent);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			
		} catch (Exception e) {
			dialog = null;
		}
	}

	public static boolean isDialogShown() {
		if (dialog != null && dialog.isShowing()) {
			return true;
		}
		return false;
	}

	/**
 * 
 */
	public static void dismisssSpinnerDialog() {
		try {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
		} catch (Exception e) {
			dialog = null;
		}
	}

	

	public void resetCredentials() {

		this.userName = "";
		this.password = "";

		this.rememeberUsername = false;
		this.rememberPassword = false;
		this.authCode = "";
		this.isAuthenticated = false;
	}

	public void saveCredentials() {
		SharedPreferences.Editor e = settings.edit();
		e.putBoolean("rememberUsername", this.rememeberUsername);
		e.putBoolean("rememberPassword", this.rememberPassword);
		try {
			if (this.rememeberUsername) {
				e.putString("username",
						SimpleCrypto.encrypt(ENCRYPTION_KEY, this.userName));
			} else {
				e.putString("username", "");
			}
			if (this.rememberPassword) {
				e.putString("password",
						SimpleCrypto.encrypt(ENCRYPTION_KEY, this.password));
			} else {
				e.putString("password", "");
			}
		} catch (SignatureException e1) {
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		e.commit();
	}

	public void saveUserName() {
		SharedPreferences.Editor e = settings.edit();
		e.putBoolean("rememberUsername", true);
		try {
			e.putString("username",
					SimpleCrypto.encrypt(ENCRYPTION_KEY, this.userName));

		} catch (SignatureException e1) {
			e1.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		e.commit();
	}
	public static synchronized User getInstance(Application application) {
		if (null == sUtil) {
			sUtil = new User();
		}
		return sUtil;
	}

	public void resetPrefCredentials(boolean isUsername, boolean isPassword) {
		if (isUsername == false)
			this.userName = "";
		if (isPassword == false)
			this.password = "";
		this.rememeberUsername = isUsername;
		this.rememberPassword = isPassword;
	}

	public void resetUserObject() {
		if (!this.rememeberUsername) {
			this.userName = "";
		}

		if (!this.rememberPassword) {
			this.password = "";
		}

		/*
		 * this.rememeberUsername = false; this.rememberPassword = false;
		 */
		this.authCode = "";
		
	}
	 public static DefaultHttpClient getSSLByPassedHttpClient(){
	        DefaultHttpClient httpClient = null;
	        try{
	            // sets up parameters
	            HttpParams params = new BasicHttpParams();
	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	            HttpProtocolParams.setContentCharset(params, "utf-8");
	            params.setBooleanParameter("http.protocol.expect-continue", false);

	            // registers schemes for both http and https
	            SchemeRegistry registry = new SchemeRegistry();
	            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	            registry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

	            ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params,
	                    registry);

	            httpClient = new DefaultHttpClient(manager, params);
	            httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

	                @Override
	                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {

	                    HeaderElementIterator it = new BasicHeaderElementIterator(response
	                            .headerIterator(HTTP.CONN_KEEP_ALIVE));
	                    while (it.hasNext()) {
	                        HeaderElement he = it.nextElement();
	                        String param = he.getName();
	                        String value = he.getValue();
	                        
	                        if (value != null && param.equalsIgnoreCase("timeout")) {
	                            try {
	                                
	                                return Long.parseLong(value) * 1000;
	                            } catch (NumberFormatException ignore) {
	                               
	                        }
	                    }
	                   
	                    // keep alive for 30 seconds
	                    return 30 * 1000;
	                    }
						return 0;
	                }
	            });
	        }catch(Exception e){
	            e.printStackTrace();
	           
	        }
	        return httpClient;
	    }
	 
	

}
