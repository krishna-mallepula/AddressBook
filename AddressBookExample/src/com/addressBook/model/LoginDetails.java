package com.addressBook.model;

public class LoginDetails {
	private int _id;
	private String _Uname;
	private String _pwd;
	
	
	public LoginDetails(int _id, String name, String pwd) {
		this._id = _id;
		this._Uname = name;
		this._pwd = pwd;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String get_Uname() {
		return _Uname;
	}
	public void set_Uname(String _Uname) {
		this._Uname = _Uname;
	}
	public String get_pwd() {
		return _pwd;
	}
	public void set_pwd(String _pwd) {
		this._pwd = _pwd;
	}
}
