package com.appfibre.lifebeam.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Invitee")
public class Invitee extends ParseObject{

	public Invitee() {
		// A default constructor is required.
	}
	
	public String getId() {
		return getObjectId();
	}
	
	public String getName() {
		return getString("name");
	}

	public void setName(String name) {
		put("name", name);
	}
	
	public String getEmail() {
		return getString("email");
	}

	public void setEmail(String email) {
		put("email", email);
	}
	
	public String getStatus() {
		return getString("status");
	}

	public void setStatus(String status) {
		put("status", status);
	}
	

}


