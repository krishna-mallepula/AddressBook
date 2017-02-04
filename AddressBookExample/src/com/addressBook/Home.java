package com.addressBook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Home extends BaseActivity implements OnClickListener {
	
	ImageView myProfile, search, newsFeed,photo;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutId(R.layout.home);
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.home);

		myProfile = (ImageView) findViewById(R.id.myprofile);
		myProfile.setOnClickListener(this);
		search = (ImageView) findViewById(R.id.searchicon);
		search.setOnClickListener(this);
		newsFeed = (ImageView) findViewById(R.id.newsfeeds);
		newsFeed.setOnClickListener(this);
		photo=(ImageView)findViewById(R.id.photo);
		photo.setOnClickListener(this );
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == myProfile.getId()) {

		} else if (v.getId() == search.getId()) {

		} else if (v.getId() == newsFeed.getId()) {

		}else if (v.getId() == photo.getId()) {
			Intent photoIntent= new Intent(this,PhotoLandingActivity.class);
			startActivity(photoIntent);

		}else if(v==myAccountMenu){
			Intent accIntent= new Intent(this,MyAccountActivity.class);
			startActivity(accIntent);
		}

	}

}
