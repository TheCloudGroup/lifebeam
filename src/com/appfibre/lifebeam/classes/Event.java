package com.appfibre.lifebeam.classes;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Event")
public class Event extends ParseObject{

	public Event() {
		// A default constructor is required.
	}
	
	public String getId() {
		return getObjectId();
	}
	
	public String getDate() {
		SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy");
		Date date = getCreatedAt();
		return dfDate.format(date);
	}
	
	public String getTime() {
		SimpleDateFormat dfTime = new SimpleDateFormat("hh:mm aa");
		Date date = getCreatedAt();
		return dfTime.format(date);
	}
	
	public String getContent() {
		return getString("content");
	}

	public void setContent(String content) {
		put("content", content);
	}
	
	public ParseUser getAuthor() {
		return getParseUser(getId());
	}

	public void setAuthor(ParseUser author) {
		put("author", author);
	}	
	
	public ParseFile getImage() {
		return getParseFile("image");
	}

	public void setImage(ParseFile image) {
		put("image", image);
	}

}


