package com.addressBook.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
		View.OnClickListener mClickListener;
		/**
		 * @param mClickListener
		 */
		

	public void setOnClickListener(View.OnClickListener mClickListener) {
		this.mClickListener = mClickListener;

	}

}
