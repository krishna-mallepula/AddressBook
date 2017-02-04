package com.addressBook;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.addressBook.model.FacebookAlbumInfoBean;

public class AlbumsListAdapter extends ArrayAdapter<FacebookAlbumInfoBean> {

	private List<com.addressBook.model.FacebookAlbumInfoBean> listFacebookAlbumInfoBeanBean;
	public Activity activity;
	private static LayoutInflater inflater = null;
	private UrlImageLoader fBConnectImageLoader;

	public AlbumsListAdapter(Activity activity, int resourceId,
			List<FacebookAlbumInfoBean> listFacebookAlbumInfoBeanBean,
			UrlImageLoader fBConnectImageLoader) {
		super(activity, resourceId, listFacebookAlbumInfoBeanBean);
		this.listFacebookAlbumInfoBeanBean = listFacebookAlbumInfoBeanBean;
		this.activity = (Activity) activity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.fBConnectImageLoader = fBConnectImageLoader;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// to findViewById() on each row.
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fbconnect_album_row, null);
			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			viewHolder = new ViewHolder();
			viewHolder.mAlbumName = (TextView) convertView
					.findViewById(R.id.fbconnect_album_user_name);
			viewHolder.mAlbumCount = (TextView) convertView
					.findViewById(R.id.fbconnect_album_count);
			viewHolder.coverImage = (ImageView) convertView
					.findViewById(R.id.fbconnect_album_image);
			convertView.setTag(viewHolder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.mAlbumName.setText(listFacebookAlbumInfoBeanBean.get(
				position).getAlbumName());
		viewHolder.mAlbumCount.setText(" ("
				+ listFacebookAlbumInfoBeanBean.get(position)
						.getAlbumPicsCount() + ")");
		viewHolder.coverImage.setTag(listFacebookAlbumInfoBeanBean
				.get(position).getAlbumCoverPhotoURL());
		fBConnectImageLoader.DisplayImage(
				listFacebookAlbumInfoBeanBean.get(position)
						.getAlbumCoverPhotoURL(), activity,
				viewHolder.coverImage);
		return convertView;
	}

	static class ViewHolder {
		protected TextView mAlbumName;
		protected TextView mAlbumCount;
		protected ImageView coverImage;
	}
}
