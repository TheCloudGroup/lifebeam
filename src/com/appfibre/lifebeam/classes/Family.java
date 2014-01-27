package com.appfibre.lifebeam.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;

@ParseClassName("Family")
public class Family extends ParseObject{

	public Family() {
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
	
	public String getPassCode() {
		return getString("passCode");
	}

	public void setPassCode(String passCode) {
		put("passCode", passCode);
	}	
	
	public ParseRelation<Family> getInvitees() {
		return getRelation("invitees");
	}
	
	public void setInvitees(ParseRelation<Family> invitees) {
		put("invitees", invitees);
	}
	
	public ParseRelation<Event> getEvents() {
		return getRelation("events");
	}
	
	public void setEvents(ParseRelation<Event> events) {
		put("events", events);
	}

}


