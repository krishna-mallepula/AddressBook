package com.addressBook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.addressBook.model.LoginDetails;
import com.addressBook.sql.UserLoginDetailsDatabaseHandler;
import com.addressBook.util.AppConstants;

public class LoginActivity extends Activity implements OnClickListener {
	public static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERNAME = "username"; // fields to be
															// saved
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_CHECKED = "checked";
	private Button signInBtn, registerBtn;
	private EditText userNameTxt, pwdTxt;
	private String name, pwd;
	private CheckBox chkBox;
	public UserLoginDetailsDatabaseHandler loginDatabase;
	private SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
//		 setLayoutId(R.layout.login);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		userNameTxt = (EditText) findViewById(R.id.uname_txt);
		pwdTxt = (EditText) findViewById(R.id.pwd_txt);

		chkBox = (CheckBox) findViewById(R.id.chkRememerme);

		signInBtn = (Button) findViewById(R.id.btnLogin);
		signInBtn.setOnClickListener(this);

		registerBtn = (Button) findViewById(R.id.register_btn);
		registerBtn.setOnClickListener(this);

		/*settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		if (settings.getString("logged", "").toString().equals("logged")) {
			Intent intent = new Intent(LoginActivity.this, Home.class);
			startActivity(intent);
		}*/

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == signInBtn.getId()) {
			name = userNameTxt.getText().toString().trim();
			pwd = pwdTxt.getText().toString().trim();

			if (validateUsernameandPassword()) {
				if (name != null && pwd != null) {
					if (chkBox.isChecked()) {
						loginDatabase=new UserLoginDetailsDatabaseHandler(getApplicationContext());
						loginDatabase.addUserLoginDetails(new LoginDetails(0, name, pwd));
//						Toast.makeText(getApplicationContext(),
//								" Login details saved in database Successfully!", Toast.LENGTH_SHORT).show();
					/*	getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
								.edit()
								.putString(PREF_USERNAME,
										userNameTxt.getText().toString())
								.putString(PREF_PASSWORD,
										pwdTxt.getText().toString())
								.commit();*/

						Toast.makeText(getApplicationContext(),
								"Successfully Loggedin", Toast.LENGTH_SHORT)
								.show();
						Intent subIntent = new Intent(LoginActivity.this,
								Home.class);
						startActivity(subIntent);
					} else if (!chkBox.isChecked()) {
						getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
								.clear().commit();

					}
				}
				Toast.makeText(getApplicationContext(),
						"Successfully Loggedin", Toast.LENGTH_SHORT).show();
				Intent subIntent = new Intent(LoginActivity.this, Home.class);
				startActivity(subIntent);

			} else {
				Toast.makeText(getApplicationContext(),
						"Please enter User Name and Password..",
						Toast.LENGTH_LONG).show();
			}

		} else if (v.getId() == registerBtn.getId()) {
			Intent registerIntent = new Intent(this, UserDetailsActivity.class);
			startActivity(registerIntent);

		}

	}

	private boolean validateUsernameandPassword() {
		boolean validate = true;
		if (name.equals(AppConstants.EMPTY_STRING)
				&& pwd.equals(AppConstants.EMPTY_STRING)) {
			Toast.makeText(this, AppConstants.USERNAME_AND_PASSWORD_REQ,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (name.length() > 0 && pwd.length() <= 0) {
			Toast.makeText(this, AppConstants.LOGIN_PASSWORD_REQ,
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (name.length() <= 0 && pwd.length() > 0) {
			Toast.makeText(this, AppConstants.USERNAME_REQ, Toast.LENGTH_SHORT)
					.show();
			return false;
		}

		return validate;
	}

}
