package com.addressBook;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.addressBook.model.Contact;
import com.addressBook.sql.UserDetailsDatabaseHandler;

public class EditProfile extends Activity implements OnClickListener{
	private Button submit;
	public UserDetailsDatabaseHandler db;
	public SQLiteDatabase mSqLiteDatabase;
	private Contact contact;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userdetails_layout);
		
		submit=(Button)findViewById(R.id.submit_btn);
		submit.setOnClickListener(this);
		
		Bundle b = getIntent().getExtras(); 
	    int value = b.getInt("state", 0);
	    
	    String name = b.getString("Name");
	    String number = b.getString("Number");
	    String addrone = b.getString("Addr One");
	    String addrtwo = b.getString("Addr Two");
	    String city = b.getString("City");
	    String state = b.getString("State");
	    String zip = b.getString("Zip");
	    String dob = b.getString("Dob");
	    
	        
	    EditText edt1 = (EditText) findViewById(R.id.firstname_edit_txt);
	    EditText edt2 = (EditText) findViewById(R.id.number_txt);
	    EditText edt3 = (EditText) findViewById(R.id.addrone_txt);
	    EditText edt4 = (EditText) findViewById(R.id.addrtwo_txt);
	    EditText edt5 = (EditText) findViewById(R.id.city_txt);
	    EditText edt6 = (EditText) findViewById(R.id.state_txt);
	    EditText edt7 = (EditText) findViewById(R.id.zip_txt);
	    EditText edt8 = (EditText) findViewById(R.id.dob_txt);
	   
	        
	    edt1.setText(name);
	    edt2.setText( number );
	    edt3.setText( addrone);
	    edt4.setText( addrtwo);
	    edt5.setText( city);
	    edt6.setText( state);
	    edt7.setText(zip);
	    edt8.setText( dob);
	}
	@Override
	public void onClick(View v) {
		if(v==submit){
			db=new UserDetailsDatabaseHandler(this);
			SQLiteDatabase sqlliteDB =db.getWritableDatabase();
		    ContentValues values = new ContentValues();
		    values.put(UserDetailsDatabaseHandler.KEY_USER_NAME, contact.getUser_name());
		    values.put(UserDetailsDatabaseHandler.KEY_USER_FIRST_NAME, contact.getFirst_name());
		    values.put(UserDetailsDatabaseHandler.KEY_USER_LAST_NAME, contact.getLast_name());
		    values.put(UserDetailsDatabaseHandler.KEY_USER_PWD, contact.getPwd());
		    values.put(UserDetailsDatabaseHandler.KEY_USER_CONFIRMPWD, contact.getConfirm_pwd());
		    values.put(UserDetailsDatabaseHandler.KEY_PH_NO, contact.getPhone_number());
		    values.put(UserDetailsDatabaseHandler.KEY_ADDR_ONE, contact.getAddr_1());
		    values.put(UserDetailsDatabaseHandler.KEY_ADDR_TWO, contact.getAddr_2());
		    values.put(UserDetailsDatabaseHandler.KEY_CITY, contact.getCity());
		    values.put(UserDetailsDatabaseHandler.KEY_STATE, contact.getState());
		    values.put(UserDetailsDatabaseHandler.KEY_ZIP, contact.getZipcode());
		    values.put(UserDetailsDatabaseHandler.KEY_DOB, contact.getDob());
		    
		    
//		    values.put(KEY_GEOLAT, contact.getLat());
//		    values.put(KEY_GEOLNG, contact.getLng());

		    // updating row
		    sqlliteDB.update(UserDetailsDatabaseHandler.USERDETAILS_TABLE, values, UserDetailsDatabaseHandler.KEY_ID + " = ?",
		            new String[] { String.valueOf(contact.get_id()) });
		}
		}
		
	}
	

