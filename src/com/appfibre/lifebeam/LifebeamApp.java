package com.appfibre.lifebeam;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Application;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.classes.Invitee;
import com.appfibre.lifebeam.utils.MyContactItem3;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class LifebeamApp extends Application{

	private final String PARSE_APPLICATION_ID = "209r35YNaAJttCErF8XdLM1zOvU4eKVYzxmp5NCr";
	private final String PARSE_CLIENT_KEY = "PruRnuoRNTNhpxwAlL1hkXHpDx0E5yUNxf9ZvydS";

/*	private ArrayList<MyContactItem3> ContactsHolder = new ArrayList<MyContactItem3>();
	
	*//**
	 * @return the contactsHolder
	 *//*
	public ArrayList<MyContactItem3> getContactsHolder() {
		return ContactsHolder;
	}

	*//**
	 * @param contactsHolder the contactsHolder to set
	 *//*
	public void setContactsHolder(ArrayList<MyContactItem3> contactsHolder) {
		ContactsHolder = contactsHolder;
	} */
	
	
	public void onCreate() {
		
		ParseObject.registerSubclass(Event.class);
		ParseObject.registerSubclass(Family.class);
		ParseObject.registerSubclass(Invitee.class);
		
		Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
		
		ParseFacebookUtils.initialize(getResources().getString(R.string.fbAppID));
		
		
	}



}
