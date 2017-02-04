package com.addressBook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.addressBook.model.Contact;
import com.addressBook.sql.UserDetailsDatabaseHandler;

public class MyAccountActivity extends Activity implements OnClickListener {
	TextView myprofile_info_txt;
	UserDetailsDatabaseHandler db;
	Button editBtn;
	EditText nameEditText, phoneEditText, addroneEditText, addrtwoEditText,
			cityEditText, stateEditText, zipEditText, dobEditText;
	EditText firstNameTxt, lastNameTxt, emailIdTxt, dobTxt, genderTxt;
	String name, number, addrOne, addrTwo, city, state, zip, dob;
	// private String firstNameStr, lastNameStr, emailIdStr, zipStr;
	List<String> list;
//	private Button genderBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.myaccount_layout);
//		displayMyProfileInfo();

		myprofile_info_txt = (TextView) findViewById(R.id.myprofile_info);
		editBtn = (Button) findViewById(R.id.btnEdit);
		editBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == editBtn.getId()) {

			firstNameTxt = (EditText) findViewById(R.id.firstnameprofile_txt);
			lastNameTxt = (EditText) findViewById(R.id.lastname_txt);
			emailIdTxt = (EditText) findViewById(R.id.emailid_txt);
			genderTxt = (EditText) findViewById(R.id.genderspinner);
			genderTxt.setOnClickListener(this);
			dobTxt = (EditText) findViewById(R.id.dob_txt);

			Bundle b = getIntent().getExtras();
			int value = b.getInt("state", 0);

			name = b.getString("Name");
			number = b.getString("Number");
			addrOne = b.getString("Addr One");
			addrTwo = b.getString("Addr Two");
			city = b.getString("City");
			state = b.getString("State");
			zip = b.getString("Zip");
			dob = b.getString("Dob");
			
			firstNameTxt.setText(name);
			lastNameTxt.setText(number);
			emailIdTxt.setText(addrOne);
			dobEditText.setText(dob);
			
			if(v==genderTxt){
				
			}
			

			/*ArrayList<Contact> contactList = (ArrayList<Contact>) db
					.getAllContacts();

			list.add("Name:" + name + ",Number: " + number + ",Address Line1:"
					+ addrOne + ",Address Line1Two:" + addrTwo + ",City:"
					+ city + ",State:" + state + ",ZIP:" + zip + ",DOB:" + dob);*/
			/*
			 * try {
			 * 
			 * Contact contact = contactList.get(1);
			 * nameEditText.setText(contact.getName());
			 * phoneEditText.setText(contact.getPhoneNumber());
			 * addroneEditText.setText(contact.getAddr_1());
			 * addrtwoEditText.setText(contact.getAddr_2());
			 * cityEditText.setText(contact.getCity());
			 * stateEditText.setText(contact.getState());
			 * zipEditText.setText(contact.getZipcode());
			 * dobEditText.setText(contact.getDob()); } catch (Exception e) { //
			 * TODO: handle exception }
			 */

			/*
			 * SQLiteDatabase dbdata = db.getReadableDatabase(); Cursor cursor =
			 * db.mSqLiteDatabase.query( DatabaseHandler.USERDETAILS_TABLE,
			 * null, null, null, null, null, null);
			 * 
			 * startManagingCursor(cursor); // return cursor; int i = 0; while
			 * (cursor.moveToNext()) { list = new ArrayList<String>();
			 * 
			 * // long id = cursor.getLong(0); // long time = cursor.getLong(1);
			 * String name = cursor.getString(1); String no =
			 * cursor.getString(2); String addrOne = cursor.getString(3); String
			 * addrTwo = cursor.getString(4); String city = cursor.getString(5);
			 * String state = cursor.getString(6); String zip =
			 * cursor.getString(7); String dob = cursor.getString(8);
			 */

		}
	}

	public void displayMyProfileInfo() {
		ArrayList<Contact> contactList = (ArrayList<Contact>) db
				.getAllContacts();
//		Cursor c =  db.query(db.USERDETAILS_TABLE, db.columns, null, null, null, null, null);
		Cursor c = (Cursor) db.getAllContacts();

		if (c != null) {
			if (c.moveToFirst()) {
				do {
					name = c.getString(c.getColumnIndex("name"));
					number = c.getString(c.getColumnIndex("phone_number"));
					addrOne = c.getString(c.getColumnIndex("Address_Line1"));
					addrTwo = c.getString(c.getColumnIndex("Address_Lin2"));
					city = c.getString(c.getColumnIndex("City"));
					state = c.getString(c.getColumnIndex("State"));
					zip = c.getString(c.getColumnIndex("ZipCode"));
					dob = c.getString(c.getColumnIndex("DOB"));

					list.add("Name:" + name + ",Phone Number: " + number
							+ ",Address Line1:" + addrOne
							+ ",Address Line1Two:" + addrTwo + ",City:" + city
							+ ",State:" + state + ",ZIP:" + zip + ",DOB:" + dob);

				} while (c.moveToNext());
			}
			myprofile_info_txt.setText(list.get(0).toString());

			
		}

	}

}
