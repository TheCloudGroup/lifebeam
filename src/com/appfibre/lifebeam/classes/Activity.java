package com.appfibre.lifebeam.classes;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Activity")
public class Activity extends ParseObject{

	public Activity() {
		// A default constructor is required.
	}
	
	public String getId() {
		return getObjectId();
	}
	
	public String getContent() {
		return getString("content");
	}

	public void setContent(String content) {
		put("content", content);
	}
	
	public ParseFile getImage() {
		return getParseFile("image");
	}

	public void setImage(ParseFile image) {
		put("image", image);
	}

}


