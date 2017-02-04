package com.addressBook.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Contact {
	// private variables
	private int id;
	private String first_name;
	private String last_name;
	private String user_name;
	private String pwd;
	private String confirm_pwd;
	private String phone_number;
	private String addr_1;
	private String addr_2;
	private String city;
	private String state;
	private String zipcode;
	private String dob;
	private String genderValue;

	// constructor
	public Contact(int id,  String firstName, String lastName,String uName,
			String password, String confirmPassword, String phoneNumber,
			String addr_1, String addr_2, String city, String state,
			String zipcode, String dob, String genderValue) {
		this.id = id;
		
		this.first_name = firstName;
		this.last_name = lastName;
		this.user_name = uName;
		this.pwd = password;
		this.confirm_pwd = confirmPassword;
		this.phone_number = phoneNumber;
		/*
		 * this.setUser_name(uName); this.setPhone_number(_phone_number);
		 */
		this.addr_1 = addr_1;
		this.addr_2 = addr_2;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		this.dob = dob;
		this.genderValue = genderValue;

	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getConfirm_pwd() {
		return confirm_pwd;
	}

	public void setConfirm_pwd(String confirm_pwd) {
		this.confirm_pwd = confirm_pwd;
	}

	public int get_id() {
		return id;
	}

	public void set_id(int id) {
		this.id = id;
	}

	public String getGenderValue() {
		return genderValue;
	}

	public void setGenderValue(String genderValue) {
		this.genderValue = genderValue;
	}

	/*
	 * private double lat; private double lng;
	 */
	private JSONObject jsonObj;

	public Contact(int id, String jsonString) {
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parse() {
		// jsonObj.
	}

	public String getAddr_1() {
		return addr_1;
	}

	public void setAddr_1(String addr_1) {
		this.addr_1 = addr_1;
	}

	public String getAddr_2() {
		return addr_2;
	}

	public void setAddr_2(String addr_2) {
		this.addr_2 = addr_2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	/**
	 * @return the user_name
	 */
	public String getUser_name() {
		return user_name;
	}

	/**
	 * @param user_name
	 *            the user_name to set
	 */
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	/**
	 * @return the phone_number
	 */
	public String getPhone_number() {
		return phone_number;
	}

	/**
	 * @param phone_number
	 *            the phone_number to set
	 */
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	/*
	 * // getting ID public int getID() { return this.id; }
	 * 
	 * // setting id public void setID(int id) { this.id = id; }
	 * 
	 * // getting name public String getName() { return this.user_name; }
	 * 
	 * // setting name public void setName(String name) { this.user_name = name;
	 * }
	 * 
	 * // getting phone number public String getPhoneNumber() { return
	 * this.phone_number; }
	 * 
	 * // setting phone number public void setPhoneNumber(String phone_number) {
	 * this.phone_number = phone_number; }
	 * 
	 * public String toString() { // TODO Auto-generated method stub return
	 * user_name;
	 * 
	 * }
	 */
}
