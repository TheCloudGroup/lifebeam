package com.appfibre.lifebeam.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

@ParseClassName("User")
public class User extends ParseObject{

	public User() {
		// A default constructor is required.
	}
	
	public String getId() {
		return getObjectId();
	}
	
	public String getUserName() {
		return getString("username");
	}

	public void setUserName(String username) {
		put("username", username);
	}
	
	public String getEmail() {
		return getString("email");
	}

	public void setEmail(String email) {
		put("email", email);
	}

	public String getFirstName() {
		return getString("firstName");
	}

	public void setFirstName(String firstName) {
		put("firstName", firstName);
	}	
	
	public String getLastName() {
		return getString("lastName");
	}

	public void setLastName(String lastName) {
		put("lastName", lastName);
	}
	
	public ParseRelation<User> getFamilies() {
		return getRelation("families");
	}
	
	public void setFamilies(ParseRelation<User> families) {
		put("families", families);
	}

	public String getUserType() {
		return getString("userType");
	}

	public void setUserType(String userType) {
		put("userType", userType);
	}
	
}


