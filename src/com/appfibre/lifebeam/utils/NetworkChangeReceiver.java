package com.appfibre.lifebeam.utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.appfibre.lifebeam.classes.EventSerializable;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver{
	  @Override
      public void onReceive(final Context context, final Intent intent) {
          Boolean status = Utils.isOnline(context);
          
          if(Utils.isOnline(context)){
        	  ParseObject user = ParseUser.getCurrentUser();
        	  if(user != null){
        		  String userObjectId = user.getObjectId();
        		  String cacheKey = "event_cache_key_" + userObjectId;
        		  EventsCache eventsCache = new EventsCache(cacheKey, context);
        		  try {
					List<EventSerializable> eventsSerializable = eventsCache.getEvents();
					Toast.makeText(context, "There are " + eventsSerializable.size() + " cached events", Toast.LENGTH_LONG).show();
					eventsCache.emptyCache();
                  } catch (Exception e) {
                    Log.e(getClass().getName(), e.getMessage());
                  } 
        		  
        	  } else {
        		  Log.d(getClass().getName(), "No parse user");
        	  }
          }
      }
}
