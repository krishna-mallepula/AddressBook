package com.addressBook.util;

import java.util.List;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.addressBook.model.Contact;

public class ContactAdapter extends ArrayAdapter<Contact>{
	LayoutInflater inflator;
	private List<Contact> contactsList;
	 private Context context;
	private int list;
	
	public ContactAdapter(Context context, int list) {
		super(context, list);
		this.context = context;
		this.list = list;
	}
	static class ViewHolder {
		protected ListView text;
	
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
//			 convertView = inflator.inflate(R.layout.contactlist_layout, null);
			 final ViewHolder viewHolder = new ViewHolder();
//				viewHolder.text = (ListView) view.findViewById(R.id.contact_list);
            	return view;
		}
		return view;
	}

	
	}


