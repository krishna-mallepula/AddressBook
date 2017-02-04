package com.addressBook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.addressBook.model.FacebookUserInfoBean;
import com.addressBook.util.User;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.instagram.android.InstagramApp;

public class PhotoLandingActivity extends Activity implements OnClickListener {
	ImageView loginFbBtn;
	Button instagramBtn;
	private InstagramApp mApp;
	private FacebookUserInfoBean mFacebookUserInfoBean;
	private Facebook facebook;
	private Activity activity;
	public Handler mHandler;
	private boolean mIsAlertDisplayed = false;
	private AsyncFacebookRunner mAsyncFacebookRunner;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (User.FACEBOOK_APP_ID == null) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running this example: see Example.java");
		}
		setContentView(R.layout.photo_main);

		loginFbBtn = (ImageView) findViewById(R.id.login);
		loginFbBtn.setOnClickListener(this);
		instagramBtn = (Button) findViewById(R.id.insta_btn);
		instagramBtn.setOnClickListener(this);

	}

	
	
	@Override
	public void onClick(View v) throws FacebookError {
		if (v.getId() == loginFbBtn.getId()) {
			loginToFacebook();
		} else if (v == instagramBtn) {
			Intent instaIntent=new Intent(this,InstagramLoginActivity.class);
			startActivity(instaIntent);
		
		}

	}

	
	private void loginToFacebook() {
		facebook = new Facebook(User.FACEBOOK_APP_ID);
		mHandler = new Handler();
		mAsyncFacebookRunner = new AsyncFacebookRunner(facebook);
		SessionStore.restore(facebook, getApplicationContext());
		SessionEvents.addAuthListener(new FacebookAuthListener());
		SessionEvents.addLogoutListener(new FacebookLogoutListener());
		if (facebook.isSessionValid()) {
			mProgressDialog = ProgressDialog.show(PhotoLandingActivity.this,
					"Loading", "Please wait...", false, false);
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
			mAsyncFacebookRunner
					.request("me", new UserDetailsRequestListener());
		} else {
			facebook.authorize(PhotoLandingActivity.this,
					Util.FACEBOOK_PERMISSIONS, Facebook.FORCE_DIALOG_AUTH,
					new FacebookLoginDialogListener());
		}
	}

	private void callServiceUnAvailMesg() {
		if (!mIsAlertDisplayed) {
			showServiceUnAvailbleDialog(PhotoLandingActivity.this,
					getString(R.string.service_unavailable_title),
					getString(R.string.fb_connect_connection_error));
		}
	}

	class FacebookLoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {

			SessionEvents.onLoginSuccess();
			SessionStore.save(facebook, getApplicationContext());
			PhotoLandingActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					mProgressDialog = ProgressDialog.show(
							PhotoLandingActivity.this, "Loading",
							"Please wait...", false, false);
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
				}
			});
			mAsyncFacebookRunner
					.request("me", new UserDetailsRequestListener());
		}

		public void onFacebookError(FacebookError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");

		}
	}

	private class UserDetailsRequestListener implements RequestListener,
			DialogListener {

		@Override
		public void onComplete(String response, Object state) {
			Log.i("Facebook response - UserDetailsRequestListener", response);
			mFacebookUserInfoBean = new FacebookUserInfoBean();
			try {
				JSONObject json = Util.parseJson(response);
				mFacebookUserInfoBean.setUserID(json.getString("id"));
				mFacebookUserInfoBean.setUserImageURL(Util.IMAGELINK
						+ json.getString("id") + Util.NORMAL_SIZE_PIC);
				if (json.has("username"))
					mFacebookUserInfoBean.setUserName(json
							.getString("username"));
				mFacebookUserInfoBean
						.setUserProfileName(json.getString("name"));
				PhotoLandingActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						mProgressDialog.dismiss();
						Intent intent = new Intent(getApplicationContext(),
								FBConnectActivity.class);
						intent.putExtra("FacebookUserInfoBean",
								mFacebookUserInfoBean);
						startActivity(intent);
					}
				});
				/*
				 * SessionStore.saveUserName(
				 * mFacebookUserInfoBean.getUserProfileName(),
				 * getApplicationContext());
				 */
			} catch (JSONException e) {
				if (mProgressDialog != null && mProgressDialog.isShowing())
					mProgressDialog.dismiss();

				PhotoLandingActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						callServiceUnAvailMesg();
					}
				});
				e.printStackTrace();
			} catch (FacebookError e) {
				e.printStackTrace();
				if (facebook.isSessionValid()) {
					SessionEvents.onLogoutBegin();
					AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(
							facebook);
					asyncRunner.logout(getApplicationContext(),
							new LogoutRequestListener());
				}
			}
		}

		@Override
		public void onIOException(IOException e, Object state) {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});
			e.printStackTrace();
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});
			e.printStackTrace();
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});
			e.printStackTrace();
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			Toast.makeText(getApplicationContext(),
					"Authentication with Facebook failed!", 10).show();
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});
		}

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e) {
			e.printStackTrace();
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});

		}

		@Override
		public void onError(DialogError e) {
			e.printStackTrace();
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});

		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

	}

	private class LogoutRequestListener implements RequestListener {

		@Override
		public void onComplete(String response, Object state) {
			// callback should be run in the original thread,
			// not the background thread
			PhotoLandingActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					mProgressDialog.dismiss();
				}
			});
			mHandler.post(new Runnable() {
				public void run() {
					SessionEvents.onLogoutFinish();
					SessionStore.clear(getApplicationContext());
					facebook.authorize(PhotoLandingActivity.this,
							Util.FACEBOOK_PERMISSIONS,
							Facebook.FORCE_DIALOG_AUTH,
							new FacebookLoginDialogListener());
				}
			});

		}

		@Override
		public void onIOException(IOException e, Object state) {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					callServiceUnAvailMesg();
				}
			});

		}

	}

	public class FacebookLogoutListener implements
			com.addressBook.SessionEvents.LogoutListener {
		public void onLogoutBegin() {

		}

		public void onLogoutFinish() {

		}
	}

	public class FacebookAuthListener implements
			com.addressBook.SessionEvents.AuthListener {

		public void onAuthSucceed() {
			SessionStore.save(facebook, getApplicationContext());
		}

		public void onAuthFail(final String error) {

			PhotoLandingActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					if (!error.equalsIgnoreCase("Action Canceled")) {
						callServiceUnAvailMesg();
					} else {
						SessionEvents.onLogoutBegin();
						AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(
								facebook);
						asyncRunner.logout(getApplicationContext(),
								new LogoutRequestListener1());
					}

				}
			});
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

						dialog.cancel();
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

	private class LogoutRequestListener1 implements
			com.facebook.android.AsyncFacebookRunner.RequestListener {

		public void onComplete(String response, Object state) {
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

	}
	
}
