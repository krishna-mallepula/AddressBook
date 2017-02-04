package com.addressBook;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.addressBook.model.FBAlbumGridInfoBean;
import com.addressBook.util.User;

public class FBConnectAlbumGallery extends Activity implements
		OnItemClickListener {
	private static int mPicsCount;
	public static ArrayList<String> tagNames;
	private int mPrevSelectedPosition = -1;// when no image is selected
	private boolean isInMultiselectedMode = false;
	private Button mBtnPrint;
	private final String mEnablePrintTextColor = "#e01935";
	private final String mDisablePrintTextColor = "#464646";
	private ProgressDialog mProgDialog = null;
	private ProgressBar mProgressbar = null;
	public final static int MAX_UPLOAD_LIMIT = 100;
	protected final String TAG = "FBConnectAlbumGallery";
	AlertDialog mUploadCancelAlertBox = null;
	private Activity activity;
	private ImageAdapter mImageAdapter;
	private GridView mImagegrid;
	private boolean mIsGalleryLoaderActivated;
	private UrlImageLoader urlImageLoader;
	private ProgressDialog mProgressDialog = null;
	private ImageUploaderTask mImageUploaderTask = null;
	public static ArrayList<String> mUploadedImagesPathList = new ArrayList<String>();
	public static ArrayList<String> mSelectedImagesPathList = new ArrayList<String>();
	private JSONObject rootObj;
	private String mAlbumId;
	User user;
	private List<com.addressBook.model.FBAlbumGridInfoBean> mImagesList;
	private com.facebook.android.Facebook mFacebook;
	private com.facebook.android.AsyncFacebookRunner mAsyncRunner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fbconnect_album_galary);
		activity = this;
		Bundle bundle = this.getIntent().getExtras();

		initializeDataBundle(bundle);
		mImagegrid = (GridView) findViewById(R.id.phoneImageGrid);
		mImagegrid.setOnItemClickListener(this);
		mIsGalleryLoaderActivated = true;
		urlImageLoader = new UrlImageLoader(getApplicationContext(), true);
		mImagesList = new ArrayList<com.addressBook.model.FBAlbumGridInfoBean>();
		mFacebook = new com.facebook.android.Facebook(User.FACEBOOK_APP_ID);
		mAsyncRunner = new com.facebook.android.AsyncFacebookRunner(mFacebook);

		SessionStore.restore(mFacebook, this);
		SessionEvents.addAuthListener(new SampleAuthListener());
		SessionEvents.addLogoutListener(new SampleLogoutListener());
		new LoadPhotosTask().execute();

		findViewById(R.id.empty_album_text).setVisibility(View.GONE);

	}

	private class LoadPhotosTask extends AsyncTask<String, Boolean, Boolean> {
		ProgressDialog mProgress;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgress = ProgressDialog.show(FBConnectAlbumGallery.this, "", "",
					false, false);
			mProgress.setOnKeyListener(new DialogInterface.OnKeyListener() {
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
							.requestPhotoList(getApplicationContext(),
									mFacebook, mAlbumId);
					parsePhotosResponse(response);
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
				return false;

			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mProgress != null && mProgress.isShowing()) {
				mProgress.dismiss();
				mProgress = null;
			}
			if (mImagesList.size() < 1) {
				findViewById(R.id.empty_album_text).setVisibility(View.VISIBLE);
				mImagegrid.setVisibility(View.GONE);
			} else {
				findViewById(R.id.empty_album_text).setVisibility(View.GONE);
				mImagegrid.setVisibility(View.VISIBLE);
				mImageAdapter = new ImageAdapter(getApplicationContext());
				mImagegrid.setAdapter(mImageAdapter);
			}

		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		cleanMemory();
		// mImageLoader.clearCache();
	}

	private void cleanMemory() {

		mImageAdapter = null;
		rootObj = null;
		mImagesList.clear();
		mImagesList = null;
		dismissUploadProgressBar();
		mProgressDialog = null;

		if (mImageUploaderTask != null) {
			mImageUploaderTask.cancel(true);
			mImageUploaderTask = null;
		}
		mImagegrid = null;
		urlImageLoader = null;

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		urlImageLoader.recycleBitmaps();
		urlImageLoader.clearCache();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mUploadedImagesPathList.size() > 0
				|| mSelectedImagesPathList.size() > 0) {
			enablePrintButton();
		} else {
			canDisablePrintButton();
			for (FBAlbumGridInfoBean item : mImagesList) {
				item.setUploaded(false);
				item.setSelected(false);
			}
		}

		if (mImageAdapter != null) {
			// reload the images.
			mImageAdapter.notifyDataSetChanged();
		}
	}

	private void initializeDataBundle(Bundle bundle) {
		if (null != bundle) {
			mAlbumId = bundle.getString("album-id");
			mPicsCount = bundle.getInt("TOTAL_COUNT");
			isInMultiselectedMode = true;
		}
	}

	protected void startPrint() {
		if (mSelectedImagesPathList.size() == 0
				&& mUploadedImagesPathList.size() > 0) {
			printHtmlCheckoutAlert(FBConnectAlbumGallery.this, "");
		} else {
			/*
			 * mBtnPrint.setTextColor(Color.parseColor("#464646"));
			 * mBtnPrint.setClickable(false);// To Avoid multiple clicks
			 */// disable
				// the button
			navigateToHtmlCheckOut();
		}
	}

	private void navigateToHtmlCheckOut() {

		/*
		 * if (Common.isInternetAvailable(FBConnectAlbumGallery.this) ||
		 * !Common.isAirplaneModeOn(FBConnectAlbumGallery.this)) { if
		 * (mApiContext == null) { mApiContextInitializerTask = new
		 * ApiContextInitializerTask(); mApiContextInitializerTask.execute(null,
		 * null, null); } else { mImageUploaderTask = new ImageUploaderTask();
		 * mImageUploaderTask.execute(null, null, null); }
		 * 
		 * } else {
		 * 
		 * enablePrintButton(); Alert.showAlert( FBConnectAlbumGallery.this,
		 * getString(R.string.fb_connect_alert_InternetConnection_title),
		 * getString(R.string.fb_connect_alert_InternetConnection)); }
		 */
	}

	public static void displayToast(Activity activity) {

	}

	private class ImageUploaderTask extends AsyncTask<Void, Integer, Void> {

		private volatile boolean mReceiveResponse = false;
		private ProgressDialog mProgDialog;
		private ProgressBar mProgressbar;

		@Override
		protected void onPreExecute() {

			mProgDialog = new ProgressDialog(FBConnectAlbumGallery.this);
			mProgDialog.setCancelable(true);
			mProgDialog.show();
			mProgDialog.setContentView(R.layout.custom_progressbar_layout);
			mProgDialog.setOnKeyListener(new OnKeyListener() {

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
			mProgressbar = (ProgressBar) mProgDialog
					.findViewById(R.id.progressbar1);
			mProgressbar.setMax(mSelectedImagesPathList.size() * 100);
			mProgDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							displayConfirmationCancel(
									FBConnectAlbumGallery.this, "", "");
						}
					});

		}

		public void setReceiveResponse(boolean isReceived) {
			mReceiveResponse = isReceived;
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			for (int i = 1; i <= mSelectedImagesPathList.size(); i++) {
				if (isCancelled())
					break;

				try {
					Thread.sleep(1000);
					publishProgress((int) i * 100);
				} catch (InterruptedException e) {

					break;
				}

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... prog) {

			mProgressbar.setProgress(prog[0]);
		}

		@Override
		protected void onCancelled() {
			dismissUploadProgressBar();
		}

		@Override
		protected void onPostExecute(Void result) {

			if (isCancelled()) {
				return;
			}
			// mProgressbar.setProgress(100);
			for (int i = 0; i < mImagesList.size(); i++) {
				if (mImagesList.get(i).isSelected()) {

					mImagesList.get(i).setSelected(false);
					mImagesList.get(i).setUploaded(true);
				}
			}
			mUploadedImagesPathList.addAll(mSelectedImagesPathList);
			mSelectedImagesPathList.clear();
			dismissUploadProgressBar();
			dismissConfirmationCancel();
			updateUI();
			// mImageAdapter.notifyDataSetChanged();

			if (mUploadedImagesPathList.size() == MAX_UPLOAD_LIMIT) {
				showAlertAndNavigateToHtml("", "");
			} else if (mUploadedImagesPathList.size() == mPicsCount
					&& mPicsCount > 0) {
				showAlertAndNavigateToHtml("", "");
			} else {

				printHtmlCheckoutAlert(FBConnectAlbumGallery.this, "", "");
			}

		}

	}

	private void showAlertAndNavigateToHtml(String Title, String Mesg) {/*
																		 * AlertDialog
																		 * .
																		 * Builder
																		 * alertbox
																		 * = new
																		 * AlertDialog
																		 * .
																		 * Builder
																		 * (
																		 * FBConnectAlbumGallery
																		 * .
																		 * this)
																		 * ;
																		 * alertbox
																		 * .
																		 * setTitle
																		 * (
																		 * Title
																		 * );
																		 * alertbox
																		 * .
																		 * setIcon
																		 * (R.
																		 * drawable
																		 * .
																		 * alert_arrow
																		 * );
																		 * alertbox
																		 * .
																		 * setMessage
																		 * (
																		 * Mesg)
																		 * ;
																		 * alertbox
																		 * .
																		 * setCancelable
																		 * (
																		 * false
																		 * );
																		 * alertbox
																		 * .
																		 * setPositiveButton
																		 * (
																		 * "Yes"
																		 * , new
																		 * DialogInterface
																		 * .
																		 * OnClickListener
																		 * () {
																		 * 
																		 * public
																		 * void
																		 * onClick
																		 * (
																		 * DialogInterface
																		 * dialog
																		 * , int
																		 * which
																		 * ) {
																		 * mIsGalleryLoaderActivated
																		 * =
																		 * false
																		 * ;
																		 * mImageAdapter
																		 * .
																		 * notifyDataSetChanged
																		 * ();
																		 * Intent
																		 * htmlFlow
																		 * = new
																		 * Intent
																		 * (
																		 * FBConnectAlbumGallery
																		 * .
																		 * this,
																		 * FBConnectHtml5CheckoutContainer
																		 * .
																		 * class
																		 * );
																		 * startActivity
																		 * (
																		 * htmlFlow
																		 * );
																		 * 
																		 * } });
																		 * alertbox
																		 * .
																		 * setNegativeButton
																		 * (
																		 * "No",
																		 * new
																		 * DialogInterface
																		 * .
																		 * OnClickListener
																		 * () {
																		 * 
																		 * public
																		 * void
																		 * onClick
																		 * (
																		 * DialogInterface
																		 * dialog
																		 * , int
																		 * which
																		 * ) {
																		 * mIsGalleryLoaderActivated
																		 * =
																		 * false
																		 * ;
																		 * mImageAdapter
																		 * .
																		 * notifyDataSetChanged
																		 * ();
																		 * if (
																		 * mUploadedImagesPathList
																		 * .
																		 * size(
																		 * ) >
																		 * 0) {
																		 * enablePrintButton
																		 * (); }
																		 * 
																		 * }
																		 * 
																		 * });
																		 * alertbox
																		 * .
																		 * setOnKeyListener
																		 * (new
																		 * OnKeyListener
																		 * () {
																		 * 
																		 * @Override
																		 * public
																		 * boolean
																		 * onKey
																		 * (
																		 * DialogInterface
																		 * dialog
																		 * , int
																		 * keyCode
																		 * ,
																		 * KeyEvent
																		 * event
																		 * ) {
																		 * if
																		 * (keyCode
																		 * ==
																		 * KeyEvent
																		 * .
																		 * KEYCODE_SEARCH
																		 * &&
																		 * event
																		 * .
																		 * getRepeatCount
																		 * () ==
																		 * 0) {
																		 * return
																		 * true;
																		 * //
																		 * Pretend
																		 * we
																		 * processed
																		 * it }
																		 * return
																		 * false
																		 * ; }
																		 * });
																		 * 
																		 * alertbox.
																		 * show
																		 * ();
																		 */
	}

	private void dismissUploadProgressBar() {

		try {
			if (mProgDialog != null && mProgDialog.isShowing()) {
				mProgDialog.dismiss();

			}
		} catch (Exception e) {

		}
		mProgDialog = null;
	}

	private void dismissConfirmationCancel() {
		try {
			if (mUploadCancelAlertBox != null) {
				mUploadCancelAlertBox.dismiss();
			}
		} catch (Exception e) {

		}

	}

	public static int getUploadLimit() {
		int batchLimit = MAX_UPLOAD_LIMIT - mUploadedImagesPathList.size();
		if (batchLimit < FBConnectAlbumGallery.MAX_UPLOAD_LIMIT) {
			return batchLimit;
		} else {
			return FBConnectAlbumGallery.MAX_UPLOAD_LIMIT;
		}
	}

	public void displayConfirmationCancel(final Context context, String Title,
			String Message) {

		AlertDialog.Builder AlertBox = new AlertDialog.Builder(context);
		AlertBox.setTitle(Title);
		AlertBox.setIcon(R.drawable.alert_icon);
		AlertBox.setMessage(Message);
		AlertBox.setCancelable(false);
		AlertBox.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						if (null != mImageUploaderTask) {
							mImageUploaderTask.setReceiveResponse(true);
							mImageUploaderTask.cancel(true);
						}
						dismissUploadProgressBar();
						enablePrintButton();// enabling print button
					}
				});
		AlertBox.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				mIsGalleryLoaderActivated = false;
				if (mProgDialog != null) {
					mProgDialog.show();
				}
			}
		});
		AlertBox.setOnKeyListener(new OnKeyListener() {

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

		mUploadCancelAlertBox = AlertBox.create();
		mUploadCancelAlertBox.show();
	}

	public void printHtmlCheckoutAlert(final Context context, String Message) {/*
																				 * 
																				 * AlertDialog
																				 * .
																				 * Builder
																				 * alertbox
																				 * =
																				 * new
																				 * AlertDialog
																				 * .
																				 * Builder
																				 * (
																				 * context
																				 * )
																				 * ;
																				 * alertbox
																				 * .
																				 * setIcon
																				 * (
																				 * R
																				 * .
																				 * drawable
																				 * .
																				 * alert_arrow
																				 * )
																				 * ;
																				 * alertbox
																				 * .
																				 * setMessage
																				 * (
																				 * Message
																				 * )
																				 * ;
																				 * alertbox
																				 * .
																				 * setCancelable
																				 * (
																				 * false
																				 * )
																				 * ;
																				 * alertbox
																				 * .
																				 * setPositiveButton
																				 * (
																				 * "Yes"
																				 * ,
																				 * new
																				 * DialogInterface
																				 * .
																				 * OnClickListener
																				 * (
																				 * )
																				 * {
																				 * 
																				 * public
																				 * void
																				 * onClick
																				 * (
																				 * DialogInterface
																				 * dialog
																				 * ,
																				 * int
																				 * which
																				 * )
																				 * {
																				 * mIsGalleryLoaderActivated
																				 * =
																				 * false
																				 * ;
																				 * mImageAdapter
																				 * .
																				 * notifyDataSetChanged
																				 * (
																				 * )
																				 * ;
																				 * Intent
																				 * htmlFlow
																				 * =
																				 * new
																				 * Intent
																				 * (
																				 * FBConnectAlbumGallery
																				 * .
																				 * this
																				 * ,
																				 * FBConnectHtml5CheckoutContainer
																				 * .
																				 * class
																				 * )
																				 * ;
																				 * startActivity
																				 * (
																				 * htmlFlow
																				 * )
																				 * ;
																				 * 
																				 * }
																				 * }
																				 * )
																				 * ;
																				 * alertbox
																				 * .
																				 * setNegativeButton
																				 * (
																				 * "No"
																				 * ,
																				 * new
																				 * DialogInterface
																				 * .
																				 * OnClickListener
																				 * (
																				 * )
																				 * {
																				 * 
																				 * public
																				 * void
																				 * onClick
																				 * (
																				 * DialogInterface
																				 * dialog
																				 * ,
																				 * int
																				 * which
																				 * )
																				 * {
																				 * mIsGalleryLoaderActivated
																				 * =
																				 * false
																				 * ;
																				 * mImageAdapter
																				 * .
																				 * notifyDataSetChanged
																				 * (
																				 * )
																				 * ;
																				 * if
																				 * (
																				 * mUploadedImagesPathList
																				 * .
																				 * size
																				 * (
																				 * )
																				 * >
																				 * 0
																				 * )
																				 * {
																				 * enablePrintButton
																				 * (
																				 * )
																				 * ;
																				 * }
																				 * 
																				 * }
																				 * 
																				 * }
																				 * )
																				 * ;
																				 * alertbox
																				 * .
																				 * setOnKeyListener
																				 * (
																				 * new
																				 * OnKeyListener
																				 * (
																				 * )
																				 * {
																				 * 
																				 * @
																				 * Override
																				 * public
																				 * boolean
																				 * onKey
																				 * (
																				 * DialogInterface
																				 * dialog
																				 * ,
																				 * int
																				 * keyCode
																				 * ,
																				 * KeyEvent
																				 * event
																				 * )
																				 * {
																				 * if
																				 * (
																				 * keyCode
																				 * ==
																				 * KeyEvent
																				 * .
																				 * KEYCODE_SEARCH
																				 * &&
																				 * event
																				 * .
																				 * getRepeatCount
																				 * (
																				 * )
																				 * ==
																				 * 0
																				 * )
																				 * {
																				 * return
																				 * true
																				 * ;
																				 * /
																				 * /
																				 * Pretend
																				 * we
																				 * processed
																				 * it
																				 * }
																				 * return
																				 * false
																				 * ;
																				 * }
																				 * }
																				 * )
																				 * ;
																				 * alertbox
																				 * .
																				 * show
																				 * (
																				 * )
																				 * ;
																				 */
	}

	public void printHtmlCheckoutAlert(final Context context, String Title,
			String Message) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
		alertbox.setTitle(Title);
		alertbox.setIcon(R.drawable.alert_arrow);
		alertbox.setMessage(Message);
		alertbox.setCancelable(false);
		alertbox.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						mIsGalleryLoaderActivated = false;
						mImageAdapter.notifyDataSetChanged();
						if (mUploadedImagesPathList.size() > 0) {
							enablePrintButton();
						}

					}
				});
		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub

			}/*
			 * 
			 * public void onClick(DialogInterface dialog, int which) {
			 * mIsGalleryLoaderActivated = false;
			 * mImageAdapter.notifyDataSetChanged(); Intent htmlFlow = new
			 * Intent(FBConnectAlbumGallery.this,
			 * FBConnectHtml5CheckoutContainer.class); startActivity(htmlFlow);
			 * 
			 * }
			 */
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

	private void updateUI() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				mImageAdapter.notifyDataSetChanged();
				canDisablePrintButton();
			}

		});
	}

	private void canDisablePrintButton() {

		if (mUploadedImagesPathList.size() == 0
				&& mSelectedImagesPathList.size() == 0) {

			mBtnPrint.setText("Print");
			mBtnPrint.setTextColor(Color.parseColor(mDisablePrintTextColor));
			mBtnPrint.setClickable(false);
		} else {

			enablePrintButton();
		}
	}

	private void enablePrintButton() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (mSelectedImagesPathList.size() > 0) {
							mBtnPrint.setText("Print");

						} else if (mUploadedImagesPathList.size() > 0) {
							mBtnPrint.setText("Checkout");
						} else {
							mBtnPrint.setText("Print");
						}
						mBtnPrint.setTextColor(Color
								.parseColor(mEnablePrintTextColor));
						mBtnPrint.setClickable(true);
					}
				});

			}
		});

	}

	public static int getNoOfUploadedImages() {
		int noOfUploadedImages = 0;

		if (null != mUploadedImagesPathList
				&& mUploadedImagesPathList.size() > 0) {
			noOfUploadedImages = mUploadedImagesPathList.size();
		}
		return noOfUploadedImages;
	}

	public class SampleAuthListener implements
			com.addressBook.SessionEvents.AuthListener {

		public void onAuthSucceed() {
		}

		public void onAuthFail(String error) {
		}
	}

	public class SampleLogoutListener implements
			com.addressBook.SessionEvents.LogoutListener {
		public void onLogoutBegin() {

		}

		public void onLogoutFinish() {
			finish();
		}
	}

	public void parsePhotosResponse(String response) throws Exception {
		// JSONArray itemList;
		// rootObj = new JSONObject(response);
		// itemList = rootObj.getJSONArray("data");
		// rootObj = null;
		// if (itemList.length() > 0) {
		// for (int i = 0; i < itemList.length(); i++) {
		// FBAlbumGridInfoBean fbAlbumGridInfoBean = new FBAlbumGridInfoBean();
		// JSONObject object = itemList.getJSONObject(i);
		// if (object.has("images")) {
		// JSONArray jsonArray = object.getJSONArray("images");
		// //
		// fbAlbumGridInfoBean.setSourceUrl(jsonArray.getJSONObject(0).get("source").toString());
		// }
		// if (object.has("src_big")) {
		// fbAlbumGridInfoBean.setSourceUrl(object.get("src_big")
		// .toString());
		// }
		// if (object.has("src_small")) {
		// fbAlbumGridInfoBean.setPictureUrl(object.get("src_small")
		// .toString());
		// }
		// if (object.has("object_id")) {
		// fbAlbumGridInfoBean.setId(object.get("object_id")
		// .toString());
		// }
		// if (object.has("src")) {
		// fbAlbumGridInfoBean.setSourceURL(object.get("src")
		// .toString());
		// }
		// if (object.has("src_width")) {
		// fbAlbumGridInfoBean.setSourceWidth(object
		// .getInt("src_width"));
		// }
		// if (object.has("src_height")) {
		// fbAlbumGridInfoBean.setSourceHeight(object
		// .getInt("src_height"));
		// }
		// fbAlbumGridInfoBean.setSelected(false);
		// fbAlbumGridInfoBean.setUploaded(false);
		// mImagesList.add(fbAlbumGridInfoBean);
		// }
		//
		// }

		JSONArray itemList;
		rootObj = new JSONObject(response);
		itemList = rootObj.getJSONArray("data");
		rootObj = null;
		String srcBigUrl = null;
		String imgArrayUrl = null;
		int srcBigWidth = 0;
		int srcBigHeight = 0;

		if (itemList.length() > 0) {
			for (int i = 0; i < itemList.length(); i++) {

				FBAlbumGridInfoBean fbAlbumGridInfoBean = new FBAlbumGridInfoBean();
				JSONObject object = itemList.getJSONObject(i);

				srcBigUrl = null;
				srcBigWidth = srcBigHeight = 0;
				imgArrayUrl = null;

				if (object.has("src_big")) {

					// src_big can be empty
					try {
						srcBigUrl = object.get("src_big").toString();

						if (object.has("src_big_width")) {
							// src_big_width can be empty
							srcBigWidth = object.getInt("src_big_width");

							// fbAlbumGridInfoBean.setSrcBigWidth(srcBigWidth);
						}

						if (object.has("src_big_height")) {
							// src_big_height can be empty
							srcBigHeight = object.getInt("src_big_height");
							// fbAlbumGridInfoBean.setSrcBigHeight(srcBigHeight);
						}

					} catch (JSONException e) {

					}
				}

				if (object.has("images")) {
					JSONArray jsonArray = object.getJSONArray("images");
					imgArrayUrl = jsonArray.getJSONObject(0).get("source")
							.toString();
					// fbAlbumGridInfoBean.setImgArrayUrl(jsonArray.getJSONObject(0).get("source").toString());
				}

				if (object.has("src")) {
					fbAlbumGridInfoBean.setSourceURL(object.get("src")
							.toString());
				}

				fbAlbumGridInfoBean.setSelected(false);
				fbAlbumGridInfoBean.setUploaded(false);
				mImagesList.add(fbAlbumGridInfoBean);
			}
		}

	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;

		public ImageAdapter(Context c) {
			mContext = c;
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return mImagesList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.local_album_gallery_item, null);
				holder.mimageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);

				holder.mPreviewText = (TextView) convertView
						.findViewById(R.id.txt_preview);
				holder.mNotText = (TextView) convertView
						.findViewById(R.id.txt_not);
				holder.mAvailableText = (TextView) convertView
						.findViewById(R.id.txt_available);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			convertView.setTag(holder);
			if (mImagesList.get(position).isSelected()) {

				holder.mimageview.setAlpha(80);
			} else {

				holder.mimageview.setBackgroundColor(color.white);
				holder.mimageview.setAlpha(255);
			}
			if (urlImageLoader != null) {
				holder.mimageview.setTag(mImagesList.get(position)
						.getSourceURL());
				urlImageLoader.DisplayImage(mImagesList.get(position)
						.getSourceURL(), FBConnectAlbumGallery.this,
						holder.mimageview);
			}

			// / holder.mimageview.setBackgroundColor(Color.BLACK);
			/*
			 * holder.mcheckbox.setOnClickListener(new OnClickListener() {
			 * 
			 * public void onClick(View v) { CheckBox cb = (CheckBox) v;
			 * FrameLayout parentLayout = (FrameLayout) v.getParent();
			 * LinearLayout layout = (LinearLayout) parentLayout .getChildAt(0);
			 * ImageView imgView = (ImageView) layout.getChildAt(0);
			 * callCheckBoxValidations(cb, imgView); }
			 * 
			 * });
			 */
			holder.mimageview.setId(position);
			if (mSelectedImagesPathList.contains(mImagesList.get(position)
					.getPrintUrl()) || mImagesList.get(position).isSelected()) {
				mImagesList.get(position).setSelected(true);
				holder.mimageview.setAlpha(80);
				holder.mimageview.setBackgroundColor(Color.BLACK);

			} else {

			}

			if (mImagesList.get(position).isSelected()) {

				holder.mimageview.setAlpha(80);
			} else {

				holder.mimageview.setAlpha(255);
			}
			if (isUploadedImage(mImagesList.get(position).getPrintUrl())
					|| mImagesList.get(position).isUploaded()) {
				holder.mimageview.setAlpha(80);
				holder.mimageview.setBackgroundColor(Color.BLACK);

			} else {

			}
			holder.id = position;

			return convertView;
		}

		class ViewHolder {
			ImageView mimageview;
			TextView muploadedText;
			TextView mPreviewText;
			TextView mNotText;
			TextView mAvailableText;
			int id;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int pPosition,
			long arg3) {
		FrameLayout parentLayout = (FrameLayout) view;
		CheckBox cb = (CheckBox) parentLayout.getChildAt(1);
		LinearLayout layout = (LinearLayout) parentLayout.getChildAt(0);
		ImageView imgView = (ImageView) layout.getChildAt(0);
		callCheckBoxValidations(cb, imgView);

	}

	private void callCheckBoxValidations(CheckBox cb, ImageView imgView) {
		CheckBox PrevSelectedCheckBox = null;
		ImageView PrevSelectedImageView = null;
		int id = cb.getId();

		if (isUploadedImage(mImagesList.get(id).getPrintUrl())) {
			return;
		}
		if (!isInMultiselectedMode && mPrevSelectedPosition != id) {
			if (mPrevSelectedPosition != -1) {
				try {
					mImagesList.get(mPrevSelectedPosition).setSelected(true);
					mSelectedImagesPathList.add(mImagesList.get(
							mPrevSelectedPosition).getPrintUrl());
					// mThumbnailsselection[mPrevSelectedPosition] = false;
					FrameLayout parentLayout = (FrameLayout) mImagegrid
							.getChildAt(mPrevSelectedPosition
									- mImagegrid.getFirstVisiblePosition());
					PrevSelectedCheckBox = (CheckBox) parentLayout
							.getChildAt(1);
					PrevSelectedImageView = (ImageView) parentLayout
							.getChildAt(0);
					PrevSelectedCheckBox.setVisibility(View.GONE);
					// This check is for No preview images
					/*
					 * if
					 * (mImagesList.get(mPrevSelectedPosition).getPictureUrl()
					 * != null) { PrevSelectedImageView.setAlpha(255); }
					 */
				} catch (Exception e) {

				}
			}
			mPrevSelectedPosition = id;
		} else {
			mPrevSelectedPosition = -1;
		}

		if (mImagesList.get(id).isSelected()) {
			cb.setVisibility(View.GONE);
			cb.setChecked(false);
			/*
			 * // This check is for No preview images if
			 * (mImagesList.get(id).getPictureUrl() != null) {
			 * imgView.setAlpha(255); }
			 */
			mImagesList.get(id).setSelected(false);
			mSelectedImagesPathList.remove(mImagesList.get(id).getPrintUrl());
			canDisablePrintButton();

		} else {
			if (mSelectedImagesPathList.size() < getUploadLimit()) {
				cb.setVisibility(View.VISIBLE);
				cb.setChecked(true);
				imgView.setAlpha(80);
				imgView.setBackgroundColor(Color.BLACK);
				mImagesList.get(id).setSelected(true);
				mSelectedImagesPathList.add(mImagesList.get(id).getPrintUrl());
				enablePrintButton();
			} else {
				displayToast(FBConnectAlbumGallery.this);
			}

		}
		mImageAdapter.notifyDataSetChanged();
	}

	private boolean isUploadedImage(String path) {

		return mUploadedImagesPathList.contains(path);

	}

	public void internetAlertMsg(Context ctx) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
		alertbox.setTitle("");
		alertbox.setMessage("");
		alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				navigateToPhotoLanding();
			}
		});
		alertbox.show();
	}

	private void navigateToPhotoLanding() {
		Intent i = new Intent(FBConnectAlbumGallery.this,
				PhotoLandingActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();

	}

}
