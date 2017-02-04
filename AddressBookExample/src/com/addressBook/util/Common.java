package com.addressBook.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

public class Common {
	public static final boolean DEBUG=true;
	
	public static final boolean isInternetAvailable(Context ctx) {
		boolean lRetVal = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) ctx
			.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo nInfo = cm.getActiveNetworkInfo();
				if (null != nInfo) {
					lRetVal = nInfo.isConnectedOrConnecting();
				}
			}
		} catch (Exception e) {
			return lRetVal;
		}

		return lRetVal;
	}
	
	public static boolean isAirplaneModeOn(Context ctx) {
		boolean isModeOn;
		isModeOn = Settings.System.getInt(ctx.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		return isModeOn;
	}
	
	public static void showToast(Context context,String Mesg)
	{
		Toast.makeText(context,Mesg,Toast.LENGTH_LONG).show();
	}
	
	/*public static String getAppVersion(Application app){
		Resources res = app.getResources();
		final String version = String.format(res.getString(R.string.version), res.getString(R.string.version_number));
		return version;
	}
*/
}
