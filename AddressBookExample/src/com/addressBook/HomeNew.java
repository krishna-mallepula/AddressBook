package com.addressBook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class HomeNew extends Activity implements OnClickListener,
		OnTouchListener {
	public static final String PREFS_NAME = "MyPrefsFile";
	private ViewPagerAdapter mViewPagerAdapter;
	public ViewPager mViewPager;
	ImageView previousBtn = null;
	ImageView nextBtn = null;
	private final int PAGE_FIRST = 0;
	private final int PAGE_SECOND = 1;
	private Bundle b;
	private int Where;

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		setLayoutId(R.layout.home_new);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_new);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPagerAdapter = new ViewPagerAdapter(this);
		previousBtn = (ImageView) findViewById(R.id.previousButton);
		previousBtn.setOnTouchListener(this);
		nextBtn = (ImageView) findViewById(R.id.nextButton);
		nextBtn.setOnTouchListener(this);

		

		

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				if (position == 0) {
					nextBtn.setImageResource(R.drawable.pages_inactive);
					previousBtn.setImageResource(R.drawable.pages_active);
				} else {
					nextBtn.setImageResource(R.drawable.pages_active);
					previousBtn.setImageResource(R.drawable.pages_inactive);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		mViewPager.setAdapter(mViewPagerAdapter);

		b = this.getIntent().getExtras();

		// getUser();
		Intent mIntent = getIntent();
		Where = mIntent.getIntExtra("SHOW", 0);

	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnMyprofile:

			break;
		case R.id.btnSearch:

			break;
		case R.id.btnNewsfeeds:

			break;
		case R.id.btnPhoto:
			Intent photoIntent = new Intent(this, PhotoLandingActivity.class);
			startActivity(photoIntent);
			break;
		case R.id.btnShop:

			break;

		default:
			break;
		}
		

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mViewPager.setCurrentItem(PAGE_FIRST);
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mViewPager.setCurrentItem(PAGE_SECOND);
			return true;
		}
		return false;
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.logout:
			if (item.getItemId() == R.id.logout) {
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.remove("logged");
				editor.commit();
				finish();
			}
			return true;// super.onOptionsItemSelected(item);

		case R.id.help:
			return true;
		}
		return false;
	}*/
}
