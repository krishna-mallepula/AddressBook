package com.addressBook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.addressBook.model.FBApplicationInfoBean;
import com.addressBook.model.FacebookAlbumInfoBean;
import com.addressBook.model.FacebookUserInfoBean;
import com.addressBook.util.User;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class FBConnectActivity extends Activity {
	ImageView profilePicture;
	public static final String RUN_ONCE = "facebook";
	TextView fbUserName;
	ListView albumsList;
	private UrlImageLoader urlImageLoader;
	private Facebook mFacebook;
	public int totalCount = 0;
	private ProgressDialog mProgressDialog = null;

	private boolean mIsAlertDisplayed;
	FacebookUserInfoBean mFacebookUserInfoBean;
	private ArrayList<FacebookAlbumInfoBean> listFacebookAlbumInfoBean;
	private FacebookAlbumInfoBean mFacebookAlbumInfoBean;
	private FBApplicationInfoBean fbApplicationInfoBean;
	private boolean photosOfMeFlag;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_connect_layout);
		urlImageLoader = new UrlImageLoader(getApplicationContext(), true);
		profilePicture = (ImageView) findViewById(R.id.imv_albums_profile_picture);
		fbUserName = (TextView) findViewById(R.id.tv_albums_fb_userName);
		albumsList = (ListView) findViewById(R.id.albums_list);
		mFacebook = new Facebook(User.FACEBOOK_APP_ID);
		mIsAlertDisplayed = false;
		Bundle bundle = this.getIntent().getExtras();
		initializeDataBundle(bundle);
		mHandler = new Handler();
		SessionStore.restore(mFacebook, this);
		SessionEvents.addAuthListener(new FacebookAuthListener());
		SessionEvents.addLogoutListener(new FacebookLogoutListener());
		new LoadApplicationInformation().execute(null, null, null);
		initializeDataBundle(bundle);
		albumsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				mProgressDialog = ProgressDialog.show(FBConnectActivity.this,
						getString(R.string.progress_default_title),
						getString(R.string.progress_rx_msg), false, false);
				mProgressDialog
						.setOnKeyListener(new DialogInterface.OnKeyListener() {
							@Override
							public boolean onKey(DialogInterface dialog,
									int keyCode, KeyEvent event) {
								if (keyCode == KeyEvent.KEYCODE_SEARCH
										&& event.getRepeatCount() == 0) {
									return true; // Pretend we processed it
								}
								return false; // Any other keys are still
												// processed
												// as normal
							}
						});
				if (listFacebookAlbumInfoBean.get(position).getAlbumID() == Util.TAGGED_PHOTOS_ID) {
				} else if (listFacebookAlbumInfoBean.get(position)
						.getAlbumName().equalsIgnoreCase("Profile Pictures")) {

				} else {

					Intent intent = new Intent(getApplicationContext(),
							FBConnectAlbumGallery.class);
					intent.putExtra("album-id",
							listFacebookAlbumInfoBean.get(position)
									.getAlbumID());
					intent.putExtra("TOTAL_COUNT", totalCount);
					startActivity(intent);
					if (mProgressDialog != null) {
						if (mProgressDialog.isShowing())
							mProgressDialog.dismiss();
					}
				}
			}
		});
		findViewById(R.id.fb_logout).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mFacebook = new Facebook(User.FACEBOOK_APP_ID);
				SessionStore.restore(mFacebook, FBConnectActivity.this);
				if (mFacebook.isSessionValid()) {
					mProgressDialog = ProgressDialog.show(
							FBConnectActivity.this,
							getString(R.string.progress_default_title),
							getString(R.string.progress_rx_msg), false, false);
					mProgressDialog
							.setOnKeyListener(new DialogInterface.OnKeyListener() {
								@Override
								public boolean onKey(DialogInterface dialog,
										int keyCode, KeyEvent event) {
									if (keyCode == KeyEvent.KEYCODE_SEARCH
											&& event.getRepeatCount() == 0) {
										return true; // Pretend we processed it
									}
									return false; // Any other keys are still
													// processed
													// as normal
								}
							});
					SessionEvents.onLogoutBegin();
					AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(
							mFacebook);
					asyncRunner.logout(getApplicationContext(),
							new LogoutRequestListener());
				}
			}
		});
		findViewById(R.id.headerhomeicon).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent nextStep = new Intent(getApplicationContext(),
								Home.class);
						nextStep.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(nextStep);
						FBConnectActivity.this.finish();
					}
				});
	}

	private void initializeDataBundle(Bundle bundle) {
		if (bundle != null) {
			mFacebookUserInfoBean = (FacebookUserInfoBean) bundle
					.get("FacebookUserInfoBean");
		}
		fbUserName.setText(mFacebookUserInfoBean.getUserProfileName());
		profilePicture.setTag(mFacebookUserInfoBean.getUserImageURL());
		urlImageLoader.DisplayImage(mFacebookUserInfoBean.getUserImageURL(),
				FBConnectActivity.this, profilePicture);
	}

	private void callServiceUnAvailMesg() {
		if (!mIsAlertDisplayed) {
			showServiceUnAvailbleDialog(FBConnectActivity.this,
					getString(R.string.service_unavailable_title),
					getString(R.string.fb_connect_connection_error));
		}
	}

	private void showServiceUnAvailbleDialog(Context context, String Title,
			String Message) {
		mIsAlertDisplayed = true;
		AlertDialog.Builder serviceUnAvailAlertbox = new AlertDialog.Builder(
				context);
		serviceUnAvailAlertbox.setTitle(Title);
		serviceUnAvailAlertbox.setMessage(Message);
		serviceUnAvailAlertbox.setCancelable(false);
		serviceUnAvailAlertbox.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						FBConnectActivity.this.finish();
					}
				});
		serviceUnAvailAlertbox.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH
						&& event.getRepeatCount() == 0) {
					return true; // Pretend we processed it
				}
				return false;
			}
		});
		serviceUnAvailAlertbox.show();
	}

	private void navigateToPhotoLanding() {
		Intent i = new Intent(FBConnectActivity.this,
				PhotoLandingActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();

	}

	@SuppressWarnings("unused")
	private void parseFBAlbumResponse(String response, String tagPhotosResponse)
			throws Exception {
		listFacebookAlbumInfoBean = new ArrayList<com.addressBook.model.FacebookAlbumInfoBean>();
		JSONObject jsonObject = new JSONObject(response);
		if (jsonObject.has("data")) {
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					mFacebookAlbumInfoBean = new FacebookAlbumInfoBean();
					if (object.has("aid")) {
						mFacebookAlbumInfoBean.setAlbumID(object
								.getString("aid"));
					}
					if (object.has("name")) {
						mFacebookAlbumInfoBean.setAlbumName(object
								.getString("name"));
					}
					if (object.has("cover_object_id")) {
						mFacebookAlbumInfoBean.setAlbumCoverPhotoID(object.get(
								"cover_object_id").toString());
						mFacebookAlbumInfoBean
								.setAlbumCoverPhotoURL(Util.IMAGELINK
										+ object.getString("cover_object_id")
										+ Util.ALBUM_COVER_PIC
										+ SessionStore
												.getAccessTocken(getApplicationContext()));
					}
					if (object.getInt("photo_count") > 0) {
						mFacebookAlbumInfoBean.setAlbumPicsCount(object.get(
								"photo_count").toString());
						if (!mFacebookAlbumInfoBean.getAlbumName()
								.equalsIgnoreCase(
										fbApplicationInfoBean
												.getApplicationDisplayName()))
							listFacebookAlbumInfoBean
									.add(mFacebookAlbumInfoBean);
					}
				}
			}
		}
		JSONObject tagsjsonObject = new JSONObject(tagPhotosResponse);
		if (tagsjsonObject.has("data")) {
			JSONArray tagjsonArray = tagsjsonObject.getJSONArray("data");
			if (tagjsonArray.length() > 0) {
				mFacebookAlbumInfoBean = new FacebookAlbumInfoBean();
				JSONObject obj = tagjsonArray.getJSONObject(0);
				mFacebookAlbumInfoBean.setAlbumID(Util.TAGGED_PHOTOS_ID);
				mFacebookAlbumInfoBean
						.setAlbumCoverPhotoURL(Util.IMAGELINK
								+ obj.get("object_id")
								+ "/picture?type=thumbnail&access_token="
								+ SessionStore
										.getAccessTocken(getApplicationContext()));
				mFacebookAlbumInfoBean.setAlbumPicsCount(""
						+ tagjsonArray.length());

				mFacebookAlbumInfoBean.setAlbumName("Photos of Me");
				listFacebookAlbumInfoBean.add(mFacebookAlbumInfoBean);
				photosOfMeFlag = true;
			}

		}

	}

	public class FacebookAuthListener implements
			com.addressBook.SessionEvents.AuthListener {

		public void onAuthSucceed() {
		}

		public void onAuthFail(String error) {
		}
	}

	public class FacebookLogoutListener implements
			com.addressBook.SessionEvents.LogoutListener {
		public void onLogoutBegin() {

		}

		public void onLogoutFinish() {
			finish();
		}
	}

	private class LoadApplicationInformation extends
			AsyncTask<String, Boolean, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog = ProgressDialog.show(FBConnectActivity.this,
					getString(R.string.progress_default_title),
					getString(R.string.progress_rx_msg), false, false);
			mProgressDialog
					.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog,
								int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_SEARCH
									&& event.getRepeatCount() == 0) {
								return true; // Pretend we processed it
							}
							return false; // Any other keys are still processed
											// as normal
						}
					});
		}

		@Override
		protected Boolean doInBackground(String... params) {

			if (mFacebook.isSessionValid()) {
				try {

					String response = new FBConnectQueryProcessor()
							.getApplicationInfo(getApplicationContext(),
									mFacebook, User.FACEBOOK_APP_ID);

					JSONObject mainObj = new JSONObject(response);
					if (mainObj.has("error")) {
						mainObj = null;
						return false;
					} else {
						if (mainObj.has("data")) {
							fbApplicationInfoBean = new FBApplicationInfoBean();
							JSONArray jsonArray = mainObj.getJSONArray("data");
							JSONObject jsonObject = jsonArray.getJSONObject(0);
							if (jsonObject.has("display_name")) {
								fbApplicationInfoBean
										.setApplicationDisplayName(jsonObject
												.getString("display_name")
												+ " Photos");
							}
							if (jsonObject.has("namespace")) {
								fbApplicationInfoBean
										.setApplicationNameSpace(jsonObject
												.getString("namespace")
												+ " Photos");
							}
							if (jsonObject.has("app_id")) {
								fbApplicationInfoBean
										.setApplicationID(jsonObject
												.getString("app_id"));
							}
						}
						mainObj = null;
					}
					return true;
				} catch (Exception exception) {
					return false;
				}
			} else {
				return false;
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			if (fbApplicationInfoBean != null) {
				new LoadAlbumsListTask().execute(null, null, null);
			} else {
				callServiceUnAvailMesg();
			}

		}
	}

	private class LoadAlbumsListTask extends
			AsyncTask<String, Boolean, Boolean> {
		ProgressDialog mProgDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgDialog = ProgressDialog.show(FBConnectActivity.this,
					getString(R.string.progress_default_title),
					getString(R.string.progress_rx_msg), false, false);
			mProgDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_SEARCH
							&& event.getRepeatCount() == 0) {
						return true; // Pretend we processed it
					}
					return false; // Any other keys are still processed
									// as normal
				}
			});
		}

		@Override
		protected Boolean doInBackground(String... params) {
			if (mFacebook.isSessionValid()) {
				try {
					String response = new FBConnectQueryProcessor()
							.requestAlbumList(getApplicationContext(),
									mFacebook);
					String tagphotoCount = new FBConnectQueryProcessor()
							.requestTaggedPhotoCount(getApplicationContext(),
									mFacebook);
					JSONObject mainObj = new JSONObject(response);
					if (mainObj.has("error")) {
						mainObj = null;
						return false;
					} else {
						mainObj = null;
					}
					mainObj = new JSONObject(tagphotoCount);
					if (mainObj.has("error")) {
						mainObj = null;
						return false;
					} else {
						mainObj = null;
					}
					parseFBAlbumResponse(response, tagphotoCount);
					if (listFacebookAlbumInfoBean != null
							&& listFacebookAlbumInfoBean.size() > 0) {
						for (int i = 0; i < listFacebookAlbumInfoBean.size(); i++) {
							totalCount += Integer
									.parseInt(listFacebookAlbumInfoBean.get(i)
											.getAlbumPicsCount());
						}
					}
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			if (mProgDialog != null && mProgDialog.isShowing()) {
				mProgDialog.dismiss();
				mProgDialog = null;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mProgDialog != null && mProgDialog.isShowing()) {
				mProgDialog.dismiss();
				mProgDialog = null;
			}
			if (!result) {
				callServiceUnAvailMesg();
				return;
			}
			if (listFacebookAlbumInfoBean.size() > 0) {
				FacebookAlbumInfoBean fbBean = null;
				for (int i = 0; i < listFacebookAlbumInfoBean.size(); i++) {
					if (listFacebookAlbumInfoBean.get(i).getAlbumName()
							.equalsIgnoreCase("Profile pictures")) {
						fbBean = listFacebookAlbumInfoBean.get(i);
						listFacebookAlbumInfoBean.remove(i);
						break;
					}

				}
				if (fbBean != null) {
					if (photosOfMeFlag) {
						listFacebookAlbumInfoBean.add(
								listFacebookAlbumInfoBean.size() - 1, fbBean);
					} else {
						listFacebookAlbumInfoBean.add(fbBean);
					}
				}

				hideOnScreenMesg();
				albumsList.setAdapter(new AlbumsListAdapter(
						FBConnectActivity.this, R.layout.fbconnect_album_row,
						listFacebookAlbumInfoBean, urlImageLoader));
			} else {
				showOnScreenMesg();
			}
			String runOnce = fetchRunOncePref(FBConnectActivity.this, 1);
			if (runOnce != null) {
				if (runOnce.equals("1")) {

				} else {
					showAlert(
							FBConnectActivity.this,
							"",
							getString(R.string.fbconnect_facebook_first_time_message));
				}
			} else {
				showAlert(
						FBConnectActivity.this,
						"",
						getString(R.string.fbconnect_facebook_first_time_message));
			}

		}
	}

	void saveRunOncePref(Context context, int key, String pRun) {
		SharedPreferences.Editor prefs = context.getSharedPreferences(RUN_ONCE,
				0).edit();
		prefs.putString(RUN_ONCE + key, pRun);
		prefs.commit();
	}

	String fetchRunOncePref(Context context, int key) {
		SharedPreferences prefs = context.getSharedPreferences(RUN_ONCE, 0);
		String runOnce = prefs.getString(RUN_ONCE + key, null);
		if (runOnce != null) {
			return runOnce;
		} else {
			return null;
		}
	}

	private void showAlert(Context context, String Title, String Message) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
		alertbox.setTitle(Title);
		alertbox.setMessage(Message);
		alertbox.setCancelable(false);
		alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				saveRunOncePref(FBConnectActivity.this, 1, "1");

			}
		});
		alertbox.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH
						&& event.getRepeatCount() == 0) {
					return true; // Pretend we processed it
				}
				return false;
			}
		});

		alertbox.show();
	}

	private void hideOnScreenMesg() {
		TextView emptyAlbumListText = (TextView) findViewById(R.id.no_albums);
		emptyAlbumListText.setVisibility(View.GONE);
		ListView albumList = (ListView) findViewById(R.id.albums_list);
		albumList.setVisibility(View.VISIBLE);
	}

	private void showOnScreenMesg() {
		TextView emptyAlbumListText = (TextView) findViewById(R.id.no_albums);
		emptyAlbumListText.setVisibility(View.VISIBLE);
		ListView albumList = (ListView) findViewById(R.id.albums_list);
		albumList.setVisibility(View.GONE);
	}

	private class LogoutRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			// callback should be run in the original thread,
			// not the background thread
			FBConnectActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					FBConnectAlbumGallery.mSelectedImagesPathList.clear();
					FBConnectAlbumGallery.mUploadedImagesPathList.clear();
					mProgressDialog.dismiss();
				}
			});
			mHandler.post(new Runnable() {
				public void run() {
					SessionEvents.onLogoutFinish();
					SessionStore.clear(getApplicationContext());
					FBConnectActivity.this.finish();
				}
			});

		}

		@Override
		public void onIOException(IOException e, Object state) {

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

		}

	}
}
