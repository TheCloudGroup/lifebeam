package com.appfibre.lifebeam;

import android.app.Application;

import com.appfibre.lifebeam.classes.Activity;
import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.classes.Invitee;
import com.appfibre.lifebeam.classes.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class LifebeamApp extends Application{

	private final String PARSE_APPLICATION_ID = "209r35YNaAJttCErF8XdLM1zOvU4eKVYzxmp5NCr";
	private final String PARSE_CLIENT_KEY = "PruRnuoRNTNhpxwAlL1hkXHpDx0E5yUNxf9ZvydS";

	public void onCreate() {
		
		ParseObject.registerSubclass(Activity.class);
		ParseObject.registerSubclass(Family.class);
		ParseObject.registerSubclass(Invitee.class);
		ParseObject.registerSubclass(User.class);
		
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		
		//ParseFacebookUtils.initialize(getResources().getString(R.string.fbAppID));
	} 

}
