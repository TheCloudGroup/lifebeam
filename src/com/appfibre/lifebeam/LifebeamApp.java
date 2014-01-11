package com.appfibre.lifebeam;

import android.app.Application;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.classes.Invitee;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class LifebeamApp extends Application{

	private final String PARSE_APPLICATION_ID = "209r35YNaAJttCErF8XdLM1zOvU4eKVYzxmp5NCr";
	private final String PARSE_CLIENT_KEY = "PruRnuoRNTNhpxwAlL1hkXHpDx0E5yUNxf9ZvydS";

	public void onCreate() {
		
		ParseObject.registerSubclass(Event.class);
		ParseObject.registerSubclass(Family.class);
		ParseObject.registerSubclass(Invitee.class);
		
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		
		ParseFacebookUtils.initialize(getResources().getString(R.string.fbAppID));
	} 

}
