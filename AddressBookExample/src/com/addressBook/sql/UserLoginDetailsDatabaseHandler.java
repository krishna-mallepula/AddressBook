package com.addressBook.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.addressBook.model.LoginDetails;

public class UserLoginDetailsDatabaseHandler extends SQLiteOpenHelper {
	
	// Database Version
		private static final int DATABASE_VERSION = 1;

		// User Login Database Name
		private static final String DATABASE_NAME = "userLoginDetailsDb";

		// User Login Details table name
		public static final String USER_LOGIN_DETAILS_TABLE = "userLoginDetails";

		// User Login Details Table Columns names
		public static final String KEY_ID = "id";
		public static final String KEY_UNAME = "userName";
		public static final String KEY_PWD = "password";
		
		public SQLiteDatabase mSqLiteDatabase;
		
		

	public UserLoginDetailsDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mSqLiteDatabase = this.getWritableDatabase();
	}
	// Creating Login Details Table
		@Override
		public void onCreate(SQLiteDatabase db) {
			String CREATE_USER_LOGIN_DETAILS_TABLE = "CREATE TABLE " + USER_LOGIN_DETAILS_TABLE
					+ "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ KEY_UNAME + " TEXT NOT NULL," + KEY_PWD + " TEXT NOT NULL)";
			db.execSQL(CREATE_USER_LOGIN_DETAILS_TABLE);
		}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
				db.execSQL("DROP TABLE IF EXISTS " + USER_LOGIN_DETAILS_TABLE);

				// Create tables again
				onCreate(db);
	}
	// Adding new contact
		public void addUserLoginDetails(LoginDetails loginDetails) {

			ContentValues values = new ContentValues();
			values.put(KEY_ID, loginDetails.get_id());
			values.put(KEY_UNAME,loginDetails.get_Uname()); 
			values.put(KEY_PWD, loginDetails.get_pwd()); 
		

			// Inserting Row
			mSqLiteDatabase.insert(USER_LOGIN_DETAILS_TABLE, null, values);
			mSqLiteDatabase.close(); // Closing database connection
		}

}
