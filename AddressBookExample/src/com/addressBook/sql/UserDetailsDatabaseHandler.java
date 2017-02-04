package com.addressBook.sql;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.addressBook.model.Contact;

public class UserDetailsDatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "userDetailsDb";

	// UserDetails table name
	public static final String USERDETAILS_TABLE = "userDetails";

	// User Details Table Columns names
	public static final String KEY_ID = "id";

	public static final String KEY_USER_FIRST_NAME = "First_Name";
	public static final String KEY_USER_LAST_NAME = "Last_Name";
	public static final String KEY_USER_NAME = "User_Name";
	public static final String KEY_USER_PWD = "Password";
	public static final String KEY_USER_CONFIRMPWD = "Confirm_Pwd";
	public static final String KEY_PH_NO = "Phone_Number";
	public static final String KEY_GENDER = "Gender";
	public static final String KEY_ADDR_ONE = "Address_LineOne";
	public static final String KEY_ADDR_TWO = "Address_LineTwo";
	public static final String KEY_CITY = "City";
	public static final String KEY_STATE = "State";
	public static final String KEY_DOB = "DOB";
	public static final String KEY_ZIP = "ZipCode";

	public SQLiteDatabase mSqLiteDatabase;

	public UserDetailsDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mSqLiteDatabase = this.getWritableDatabase();
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_USERDETAILS_TABLE = "CREATE TABLE " + USERDETAILS_TABLE
				+ "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_USER_FIRST_NAME + " TEXT NOT NULL," + KEY_USER_LAST_NAME
				+ " TEXT NOT NULL," + KEY_USER_NAME + " TEXT NOT NULL,"
				+ KEY_USER_PWD + " TEXT NOT NULL," + KEY_USER_CONFIRMPWD
				+ " TEXT NOT NULL," + KEY_PH_NO + " TEXT NOT NULL,"
				+ KEY_GENDER + " TEXT NOT NULL," + KEY_ADDR_ONE
				+ " TEXT NOT NULL," + KEY_ADDR_TWO + " TEXT NOT NULL,"
				+ KEY_CITY + " TEXT NOT NULL," + KEY_STATE + " TEXT NOT NULL,"
				+ KEY_ZIP + " TEXT NOT NULL," + KEY_DOB + " TEXT NOT NULL)";
		db.execSQL(CREATE_USERDETAILS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + USERDETAILS_TABLE);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */
	// Adding new contact
	public void addContact(Contact contact) {

		ContentValues values = new ContentValues();
		values.put(KEY_ID, contact.get_id());
		values.put(KEY_USER_FIRST_NAME, contact.getFirst_name());
		values.put(KEY_USER_LAST_NAME, contact.getLast_name());
		values.put(KEY_USER_NAME, contact.getUser_name());
		values.put(KEY_USER_PWD, contact.getPwd());
		values.put(KEY_USER_CONFIRMPWD, contact.getConfirm_pwd());
		values.put(KEY_PH_NO, contact.getPhone_number());
		values.put(KEY_GENDER, contact.getGenderValue());
		values.put(KEY_ADDR_ONE, contact.getAddr_1());
		values.put(KEY_ADDR_TWO, contact.getAddr_2());
		values.put(KEY_CITY, contact.getCity());
		values.put(KEY_STATE, contact.getState());
		values.put(KEY_ZIP, contact.getZipcode());
		values.put(KEY_DOB, contact.getDob());

		// Inserting Row
		mSqLiteDatabase.insert(USERDETAILS_TABLE, null, values);
		// mSqLiteDatabase.close(); // Closing database connection
	}

	// Getting single contact
	Contact getContactDetails(int id) {

		Cursor cursor = mSqLiteDatabase.query(USERDETAILS_TABLE, new String[] {
				KEY_ID, KEY_USER_FIRST_NAME, KEY_USER_LAST_NAME, KEY_USER_NAME,
				KEY_USER_PWD, KEY_USER_CONFIRMPWD, KEY_PH_NO, KEY_GENDER,
				KEY_ADDR_ONE, KEY_ADDR_TWO, KEY_CITY, KEY_STATE, KEY_ZIP,
				KEY_DOB }, KEY_ID + "=?", new String[] { String.valueOf(id) },
				null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4), cursor.getString(5), cursor.getString(6),
				cursor.getString(7), cursor.getString(8), cursor.getString(9),
				cursor.getString(10), cursor.getString(11),
				cursor.getString(12), cursor.getString(13));

		return contact;
	}

	Contact getPhoneNumber(int id) {
		Cursor phoneCursor = mSqLiteDatabase.query(USERDETAILS_TABLE,
				new String[] { KEY_USER_NAME, KEY_PH_NO }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);
		if (phoneCursor != null)
			phoneCursor.moveToFirst();
		Contact contact = new Contact(
				Integer.parseInt(phoneCursor.getString(0)),
				phoneCursor.getString(1));
		return contact;
	}

	// Getting All Contacts
	public List<Contact> getAllContacts() {
		List<Contact> contactList = new ArrayList<Contact>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + USERDETAILS_TABLE;

		// SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = mSqLiteDatabase.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Contact contact = new Contact(0, selectQuery, selectQuery,
						selectQuery, selectQuery, selectQuery, selectQuery,
						selectQuery, selectQuery, selectQuery, selectQuery,
						selectQuery, selectQuery, selectQuery);

				contact.set_id(Integer.parseInt(cursor.getString(0)));
				contact.setUser_name(cursor.getString(1));
				contact.setPhone_number(cursor.getString(2));
				contact.setAddr_1(cursor.getString(3));
				contact.setAddr_2(cursor.getString(4));
				contact.setCity(cursor.getString(5));
				contact.setState(cursor.getString(6));
				contact.setZipcode(cursor.getString(7));
				contact.setDob(cursor.getString(8));
				contact.setGenderValue(cursor.getString(9));

				// Adding contact to list
				contactList.add(contact);
			} while (cursor.moveToNext());
		}

		// return contact list
		cursor.close();
		// mSqLiteDatabase.close();
		return contactList;
	}

	// Updating single contact
	public int updateContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		// values.put(KEY_ID, contact.get_id());
		values.put(KEY_USER_FIRST_NAME, contact.getFirst_name());
		values.put(KEY_USER_LAST_NAME, contact.getLast_name());
		values.put(KEY_USER_NAME, contact.getUser_name());
		values.put(KEY_USER_PWD, contact.getPwd());
		values.put(KEY_USER_CONFIRMPWD, contact.getConfirm_pwd());
		values.put(KEY_PH_NO, contact.getPhone_number());
		values.put(KEY_GENDER, contact.getGenderValue());
		values.put(KEY_ADDR_ONE, contact.getAddr_1());
		values.put(KEY_ADDR_TWO, contact.getAddr_2());
		values.put(KEY_CITY, contact.getCity());
		values.put(KEY_STATE, contact.getState());
		values.put(KEY_ZIP, contact.getZipcode());
		values.put(KEY_DOB, contact.getDob());

		// updating row
		return db.update(USERDETAILS_TABLE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.get_id()) });
	}

	// Deleting single contact
	public void deleteContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(USERDETAILS_TABLE, KEY_ID + " = ?",
				new String[] { String.valueOf(contact.get_id()) });
		db.close();
	}

	// Getting contacts Count
	public int getContactsCount() {
		String countQuery = "SELECT  * FROM " + USERDETAILS_TABLE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		cursor.close();
		db.close();
		// return count
		return cursor.getCount();
	}

}
