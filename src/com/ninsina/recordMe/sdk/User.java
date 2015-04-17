package com.ninsina.recordMe.sdk;

import java.util.HashMap;
import java.util.Map;

public class User {

	public static short TYPE_DEFAULT = 0;
	public static short TYPE_ROOT = 1;
	public static short TYPE_ADMIN = 2;
	public static short TYPE_USER = 3;
	
	public String id;
	// login
	public String email;
	public String password;
	public String firstName;
	public String lastName;
	
	public boolean valid = false;
	public String validToken;
	public String validDate;
	
	public short type = TYPE_DEFAULT;
	public Map<String, String> properties = new HashMap<String, String>();
	
	
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	
}
