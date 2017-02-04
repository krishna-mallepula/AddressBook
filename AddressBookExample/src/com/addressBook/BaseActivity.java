package com.addressBook;

import com.addressBook.util.AppConstants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class BaseActivity extends Activity implements OnClickListener{

	private int layoutID;
	private RelativeLayout controls;
	public Button searchMenu,myAccountMenu,logoutIcon;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutID);
		controls = (RelativeLayout) findViewById(R.id.controls);
		controls.setVisibility(View.GONE);
		searchMenu= (Button) findViewById(R.id.searchmenu_btn);
		searchMenu.setOnClickListener(this);
		myAccountMenu= (Button) findViewById(R.id.myaccountmenu_btn);
		myAccountMenu.setOnClickListener(this);
		logoutIcon= (Button) findViewById(R.id.signoutmenu_btn);
		logoutIcon.setOnClickListener(this);
		
	}

 

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	private void showControls() {
		controls.setVisibility(View.VISIBLE);
	}

	private void hideControls() {
		controls.setVisibility(View.GONE);
	}

	private boolean isVisible() {
		boolean status;
		if (controls.getVisibility() == View.VISIBLE) {
			status = true;
		} else {
			status = false;
		}
		return status;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (isVisible()) {
			hideControls();
		} else {
			showControls();
		}
		return true;
	}
	protected void setLayoutId(int layoutID) {
		this.layoutID = layoutID;
	}
	
	protected void setLayoutId(int layoutID,Context context) {
		this.layoutID = layoutID;
//		this.context=context;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if (isVisible()) {
				hideControls();
			}
	    	else {
	    		finish();
	    	}
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==searchMenu.getId()){
			Intent searchIntent = new Intent(this, SearchActivity.class);
			startActivity(searchIntent);
			hideControls();
			
		}
		else if(v.getId()==myAccountMenu .getId()){
			Intent accIntent = new Intent(this, MyAccountActivity.class);
			startActivity(accIntent);
			hideControls();
			
		}else if(v.getId()==logoutIcon.getId()){
			
				SharedPreferences settings = getSharedPreferences(AppConstants.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.remove("logged");
				editor.commit();
				finish();
			}
			
            hideControls();
		}
		
		
	
	
	public void onItemClickListener() {
		return;
	}
}