package com.addressBook;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.addressBook.model.Contact;
import com.addressBook.sql.UserDetailsDatabaseHandler;

public class ContactsList extends Activity {
	public UserDetailsDatabaseHandler db;
	private UserDetailsActivity userDetails;
	private ListView contactsList;
	private Button addContact;
	private Context context;
	private EditText nameEditText, phoneEditText, addroneEditText,
			addrtwoEditText, cityEditText, stateEditText, zipEditText,
			dobEditText;
	ArrayAdapter<ContactsList> contactsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactslist_main);

		contactsList = (ListView) findViewById(R.id.contacts_list);
		registerForContextMenu(contactsList);
		db = new UserDetailsDatabaseHandler(this);
		Cursor cursor = getContacts();
		showContacts(getContacts());
	}

	private Cursor getContacts() {
		SQLiteDatabase db = this.db.getReadableDatabase();
		Cursor cursor = db.query(UserDetailsDatabaseHandler.USERDETAILS_TABLE, null, null,
				null, null, null, null);

		startManagingCursor(cursor);
		return cursor;
	}

	private void showContacts(Cursor cursor) {
		List<String> mList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			long id = cursor.getLong(0);
			mList.add(cursor.getString(2));
		}
		contactsList.setAdapter(new ArrayAdapter<String>(this
				.getApplicationContext(), android.R.layout.simple_list_item_1,
				mList));
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.contextmenu, menu);
		menu.setHeaderTitle("Select");
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
		// menu.add(0, v.getId(), 0, "GetLatLong&StoreinDB");

	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//		 int menuItemIndex = item.getItemId();
		 if(item.getTitle()=="Edit"){
			 editFunction(item.getItemId());
			 }  
	        else if(item.getTitle()=="Delete"){
	        	deleteFunction(item.getItemId());
	        	}  
	        else {return false;}  
	    return true;  
//		switch (item.getItemId()) {
//		case R.id.edit_context:
//			Toast.makeText(getApplicationContext(), "EDIT", Toast.LENGTH_SHORT).show();
//			return true;
//		case R.id.delete_context:
//			Toast.makeText(getApplicationContext(), "DELETE", Toast.LENGTH_SHORT).show();
//			return true;
//		
//		}
//		return super.onContextItemSelected(item);
	}
	

	private boolean deleteFunction(int itemId) {//
		Contact contact;
//		Toast.makeText(getApplicationContext(), "DELETE", Toast.LENGTH_SHORT).show();
//		 SQLiteDatabase db = context.getWritableDatabase();
//	        db.delete(DatabaseHandler.USERDETAILS_TABLE, KEY_ID + " = ?",
//	                new String[] { String.valueOf(contact.getID()) });
	       
		db.getWritableDatabase().delete(UserDetailsDatabaseHandler.USERDETAILS_TABLE, null, null);
		 db.close();
		Toast.makeText(getApplicationContext(),	" Contact deleted Successfully!",Toast.LENGTH_LONG).show();
	    return true;
	}

	private void editFunction(int itemId) {
		Toast.makeText(getApplicationContext(), "EDIT function called ! ", Toast.LENGTH_SHORT).show();
		ArrayList<Contact> contactList=(ArrayList<Contact>) db.getAllContacts();
		try {
						
		Contact contact=contactList.get(1);
		nameEditText.setText(contact.getUser_name());
		phoneEditText.setText(contact.getPhone_number());
		addroneEditText.setText(contact.getAddr_1());
		addrtwoEditText.setText(contact.getAddr_2());
		cityEditText.setText(contact.getCity());			
		stateEditText.setText(contact.getState());
		zipEditText.setText(contact.getZipcode());
		dobEditText.setText(contact.getDob());	
		} catch (Exception e) {
			// TODO: handle exception
		}			
		    
	//SQLiteDatabase dbdata = db.getReadableDatabase();
		Cursor cursor = db.mSqLiteDatabase.query(UserDetailsDatabaseHandler.USERDETAILS_TABLE, null, null,
				null, null, null, null);

		startManagingCursor(cursor);
		// return cursor;
		int i = 0;
		while (cursor.moveToNext()) {
			long id = cursor.getLong(0);
		// long time = cursor.getLong(1);
		String name = cursor.getString(1);
		String no = cursor.getString(2);
		String addrone=cursor.getString(3);
		String addrtwo = cursor.getString(4);
		String city = cursor.getString(5);
		String state = cursor.getString(6);
		String zip = cursor.getString(7);
		String dob = cursor.getString(8);
		
		Intent intent = new Intent(this, EditProfile.class);  
		Bundle b= new Bundle();
		b.putString("Name", name);
		b.putString("Number", no);
		b.putString("Addr One", addrone);
		b.putString("Addr Two", addrtwo);
		b.putString("City", city);
		b.putString("State", state);
		b.putString("ZIP", zip);
		b.putString("DOB", dob);
		intent.putExtras(b);
		startActivity(intent);  
	}
	}

	private Cursor getCity() {
		SQLiteDatabase db = this.db.getReadableDatabase();
		Cursor cursor = db.query(UserDetailsDatabaseHandler.USERDETAILS_TABLE, null, null,
				null, null, null, null);

		startManagingCursor(cursor);
		return cursor;
	}

	private void showCity(Cursor cursor) {
		List<String> mList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			long id = cursor.getLong(0);
			mList.add(cursor.getString(5));
		}
	}

}



//case R.id.edit:
//	Log.i("CLICKED", "EDIT");
//	Toast.makeText(this, " Edit function  called", Toast.LENGTH_SHORT).show();  
//    return true;
//	ArrayList<Contact> contactList=(ArrayList<Contact>) db.getAllContacts();
//	try {
//					
//	Contact contact=contactList.get(1);
//	nameEditText.setText(contact.getName());
//	phoneEditText.setText(contact.getPhoneNumber());
//	addroneEditText.setText(contact.getAddr_1());
//	addrtwoEditText.setText(contact.getAddr_2());
//	cityEditText.setText(contact.getCity());			
//	stateEditText.setText(contact.getState());
//	zipEditText.setText(contact.getZipcode());
//	dobEditText.setText(contact.getDob());	
//	} catch (Exception e) {
//		// TODO: handle exception
//	}			
//	    
////SQLiteDatabase dbdata = db.getReadableDatabase();
//	Cursor cursor = db.mSqLiteDatabase.query(DatabaseHandler.USERDETAILS_TABLE, null, null,
//			null, null, null, null);
//
//	startManagingCursor(cursor);
//	// return cursor;
//	int i = 0;
//	while (cursor.moveToNext()) {
//		long id = cursor.getLong(0);
//	// long time = cursor.getLong(1);
//	String name = cursor.getString(1);
//	String no = cursor.getString(2);
//	String addrone=cursor.getString(3);
//	String addrtwo = cursor.getString(4);
//	String city = cursor.getString(5);
//	String state = cursor.getString(6);
//	String zip = cursor.getString(7);
//	String dob = cursor.getString(8);
//
//		
//
//	}
//	 
//case R.id.delete:
//	db.getWritableDatabase().delete(DatabaseHandler.USERDETAILS_TABLE, null, null);
//	Toast.makeText(getApplicationContext(),	" Contact deletd Successfully!",Toast.LENGTH_LONG).show();
//	Intent editIntent=new Intent(ContactsList.this,EditProfile.class);
//	startActivity(editIntent);
//	Toast.makeText(this, " Delete function  called", Toast.LENGTH_SHORT).show(); 
//    return true;
/*case R.id.geocode:
showCity(getCity());

	//fetch
	
	Toast.makeText(this, " GeoCode function  called", Toast.LENGTH_SHORT).show(); 
    return true;*/
//default:
//	return super.onContextItemSelected(item);

