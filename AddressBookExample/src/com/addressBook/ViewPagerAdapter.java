package com.addressBook;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {

	public static final int NUM_PAGE_VIEWS = 2;
	private Context mContext;

	public ViewPagerAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return NUM_PAGE_VIEWS;
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		View homeView = null;
		if (position == 0) {
			homeView = (View) LayoutInflater.from(mContext).inflate(
					R.layout.home_new_1, null);
		} else {
			homeView = (View) LayoutInflater.from(mContext).inflate(
					R.layout.home_new_2, null);
		}
		((ViewPager) collection).addView(homeView, 0);
		return homeView;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((View) object);
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
}
