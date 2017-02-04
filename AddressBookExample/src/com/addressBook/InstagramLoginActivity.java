package com.addressBook;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.addressBook.util.ApplicationData;
import com.instagram.android.InstagramApp;
import com.instagram.android.InstagramApp.OAuthAuthenticationListener;

public class InstagramLoginActivity extends Activity implements OnClickListener {
	private InstagramApp mApp;
	private Button btnConnect, mPhotosBtn;
	private TextView tvSummary;
	private ImageView profilePic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instagram_main_layout);

		mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
		mApp.setListener(listener);

		tvSummary = (TextView) findViewById(R.id.tvSummary);

		profilePic = (ImageView) findViewById(R.id.imv_albums_profile_picture);

		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(this);

		mPhotosBtn = (Button) findViewById(R.id.photos);
		mPhotosBtn.setOnClickListener(this);

		if (mApp.hasAccessToken()) {
			tvSummary.setText("Logged in as " + mApp.getUserName());
			btnConnect.setText("Logout");
			mPhotosBtn.setVisibility(View.VISIBLE);
			String pic = mApp.getProfilePicUrl();
			new GetProfileImage().execute(new String[] { pic });

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConnect:
			  taponLogin();
			
			break;

		case R.id.photos :
			Intent i = new Intent(this, FBConnectAlbumGallery.class);
			startActivity(i);
		}

	}

	OAuthAuthenticationListener listener = new OAuthAuthenticationListener() {

		@Override
		public void onSuccess() {
			tvSummary.setText("Logged in as " + mApp.getUserName());
			btnConnect.setText("Logout");
			mPhotosBtn.setVisibility(View.VISIBLE);
			String pic = mApp.getProfilePicUrl();
			new GetProfileImage().execute(new String[] { pic });
		}

		@Override
		public void onFail(String error) {
			Toast.makeText(InstagramLoginActivity.this, error,
					Toast.LENGTH_SHORT).show();
		}
	};

	class GetProfileImage extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bmp = getBitmapFromURL(params[0]);
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap bmp) {
			super.onPostExecute(bmp);
			if (null != bmp) {
				profilePic.setImageBitmap(bmp);
			} else {
				profilePic.setImageResource(R.drawable.loding_album);
			}
		}

	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	private void taponLogin() {
		if (mApp.hasAccessToken()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					InstagramLoginActivity.this);
			builder.setMessage("Disconnect from Instagram?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog, int id) {
									mApp.resetAccessToken();
									btnConnect.setText("Login");
									tvSummary.setText("Not LoggedIn");
									profilePic.setImageResource(R.drawable.loding_album);
									mPhotosBtn.setVisibility(View.GONE);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
			final AlertDialog alert = builder.create();
			alert.show();
		} else {
			mApp.authorize(); 
			
		}
		
	}
}
