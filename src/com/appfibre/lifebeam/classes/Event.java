package com.appfibre.lifebeam.classes;

import java.text.ParsePosition;
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
	
	public ParseObject getAuthor() {
		return getParseObject("author");
	}

	public void setAuthor(ParseObject author) {
		put("author", author);
	}	
	
	public ParseFile getImage() {
		return getParseFile("image");
	}

	public void setImage(ParseFile image) {
		put("image", image);
	}
	
	public String getFamily() {
		return getString("family");
	}

	public void setFamily(String family) {
		put("family", family);
	}
	
	public String getSplendidCount() {
		return getString("splendidCount");
	}

	public void setSplendidCount(String splendidCount) {
		put("splendidCount", splendidCount);
	}
	
	public String getRazzleCount() {
		return getString("razzleCount");
	}

	public void setRazzleCount(String razzleCount) {
		put("razzleCount", razzleCount);
	}
	
	public String getSmileyCount() {
		return getString("smileyCount");
	}

	public void setSmileyCount(String smileyCount) {
		put("smileyCount", smileyCount);
	}

}


