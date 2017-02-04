package com.addressBook;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.addressBook.model.Contact;
import com.addressBook.sql.UserDetailsDatabaseHandler;
import com.addressBook.util.AppConstants;
import com.addressBook.util.CustomDialog;

public class UserDetailsActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	protected CustomDialog customDialog;
	private Button submit;
	private EditText firstNameEditText, lastNameEditText, userNameEditText,
			pwdEditText, confirmPwdEditText, phoneEditText, addroneEditText,
			addrtwoEditText, cityEditText, stateEditText, zipEditText,
			dobEditText, genderEditText;
	private Dialog listDialog;
	public UserDetailsDatabaseHandler db;
	public SQLiteDatabase mSqLiteDatabase;
	private int mYear;
	private int mMonth;
	private int mDay;
	private Calendar calendar;
	/*
	 * private Button gender; private String genderValue;
	 */
	static final int DATE_DIALOG_ID = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setLayoutId(R.layout.userdetails_layout);
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.userdetails_layout);

		init();

	}

	public void init() {

		db = new UserDetailsDatabaseHandler(this);

		firstNameEditText = (EditText) findViewById(R.id.firstname_edit_txt);
		lastNameEditText = (EditText) findViewById(R.id.lastname_edit_txt);
		userNameEditText = (EditText) findViewById(R.id.user_name_txt);
		pwdEditText = (EditText) findViewById(R.id.user_pwd_txt);
		confirmPwdEditText = (EditText) findViewById(R.id.user_confirmpwd_txt);
		phoneEditText = (EditText) findViewById(R.id.number_txt);
		addroneEditText = (EditText) findViewById(R.id.addrone_txt);
		addrtwoEditText = (EditText) findViewById(R.id.addrtwo_txt);
		cityEditText = (EditText) findViewById(R.id.city_txt);
		stateEditText = (EditText) findViewById(R.id.state_txt);
		zipEditText = (EditText) findViewById(R.id.zip_txt);
		genderEditText = (EditText) findViewById(R.id.genderspinner);

		submit = (Button) findViewById(R.id.submit_btn);
		submit.setOnClickListener(this);

		genderEditText.setText("Select");
		genderEditText.setOnClickListener(this);

		calendar = Calendar.getInstance();
		dobEditText = (EditText) findViewById(R.id.dob_txt);
		dobEditText.setOnClickListener(this);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DATE);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == submit.getId()) {

			String firstName = firstNameEditText.getText().toString().trim();
			String lastName = lastNameEditText.getText().toString().trim();
			String userName = userNameEditText.getText().toString().trim();
			String pwd = pwdEditText.getText().toString().trim();
			String confirmPwd = confirmPwdEditText.getText().toString().trim();
			String phone = phoneEditText.getText().toString().trim();
			String genderValue = genderEditText.getText().toString().trim();
			String addrone = addroneEditText.getText().toString().trim();
			String addrtwo = addrtwoEditText.getText().toString().trim();
			String city = cityEditText.getText().toString().trim();
			String state = stateEditText.getText().toString().trim();
			String zip = zipEditText.getText().toString().trim();
			String dob = dobEditText.getText().toString().trim();

			db.addContact(new Contact(0, firstName, lastName, userName, pwd,
					confirmPwd, phone, genderValue, addrone, addrtwo, city,
					state, zip, dob));
			Toast.makeText(getApplicationContext(),
					" Contact added Successfully!", Toast.LENGTH_SHORT).show();
			Intent homeIntent = new Intent(getApplicationContext(), Home.class);
			startActivity(homeIntent);

		} else if (v.getId() == dobEditText.getId()) {
			// updateDisplay();
			showDialog(DATE_DIALOG_ID);
		} else if (v == genderEditText) {
			showdialog();
		}

	}

	private void showdialog() {
		listDialog = new Dialog(this);
		listDialog.setTitle("Gender");
		LayoutInflater layoutInflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = layoutInflator.inflate(R.layout.listview, null, false);
		listDialog.setContentView(v);
		listDialog.setCancelable(true);
		ListView list1 = (ListView) listDialog.findViewById(R.id.listview);
		list1.setOnItemClickListener(this);
		list1.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, AppConstants.GENDER));
		listDialog.show();
	}

	private void updateDisplay() {
		dobEditText.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("/").append(mDay).append("/")
				.append(mYear).append(" "));
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	protected void onListItemClick(ListView listView, View view, int position,
			long id) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		listDialog.cancel();
		genderEditText.setText(AppConstants.GENDER[arg2]);
	}
}
