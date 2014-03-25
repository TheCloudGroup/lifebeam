package com.appfibre.lifebeam.classes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.appfibre.lifebeam.utils.Utils;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Event")
public class Event extends ParseObject{

	public Event() {
		// A default constructor is required.
	}
	
	public String getId() {
		return getObjectId();
	}
	
	public String getDate() {
		SimpleDateFormat dfDate = Utils.getDateFormat();
		Date date = getCreatedAt();
		return dfDate.format(date);
	}
	
	public String getTime() {
		SimpleDateFormat dfTime = Utils.getTimeFormat();
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
	
	public Integer getSplendidCount() {
		Integer val = getInt("splendidCount");
		return val != null ? val : 0;
	}

	public void setSplendidCount(Integer splendidCount) {
		splendidCount = splendidCount != null ? splendidCount : 0;
		put("splendidCount", splendidCount);
	}
	
	public Integer getRazzleCount() {
		Integer val = getInt("razzleCount");
		return val != null ? val : 0;
	}

	public void setRazzleCount(Integer razzleCount) {
		razzleCount = razzleCount != null ? razzleCount : 0;
		put("razzleCount", razzleCount);
	}
	
}


