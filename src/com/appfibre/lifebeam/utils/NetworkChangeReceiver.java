package com.appfibre.lifebeam.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.classes.EventSerializable;
import com.appfibre.lifebeam.classes.Family;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.appfibre.lifebeam.GalleryActivity;
import com.appfibre.lifebeam.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver{
	  private Context context;
	  private String cacheKey;
	  private List<EventSerializable> failedEvents;
	  private static Lock failedEventsLock = new ReentrantLock();
	  private static Lock eventCounterLock = new ReentrantLock();
	  private Integer eventCounter = 0;
	  private Integer eventNumCached;
	  
	  @Override
      public void onReceive(final Context context, final Intent intent) {
          Boolean isOnline = Utils.isOnline(context);
          this.context = context;
          if(isOnline){
        	  failedEvents = new ArrayList<EventSerializable>();
        	  ParseObject user = ParseUser.getCurrentUser();
        	  if(user != null){
        		  String userObjectId = user.getObjectId();
        		  cacheKey = "event_cache_key_" + userObjectId;
        		  EventsCache eventsCache = new EventsCache(cacheKey, context);
        		  try {
					List<EventSerializable> eventsSerializable = eventsCache.getEvents();
					eventNumCached = eventsSerializable.size();
					Log.i(getClass().getName(), "RE-sending " + eventNumCached + " events");
					for(EventSerializable event: eventsSerializable){
						eventSerializableToEvent(event);
					}
					eventsCache.emptyCache();
                  } catch (Exception e) {
                    Log.e(getClass().getName(), e.getMessage());
                  } 
        	  } else {
        		  Log.d(getClass().getName(), "No parse user");
        	  }
          }
      }
	  
	  private void eventSerializableToEvent(final EventSerializable eventSerialized){
		  if(eventSerialized.getImageData() != null ){
			  final ParseFile file = new ParseFile( "file.jpg", eventSerialized.getImageData() );
				file.saveInBackground(new SaveCallback() {
					@Override
					public void done(com.parse.ParseException e) {
						if (e == null) {
							Log.i(getClass().getName(), "Saving Image data");
							postEvent(file, eventSerialized);
						} else {
							Log.e(getClass().getName(), "Error saving event image" );
							setFailedEvent(eventSerialized);						
						}
					}
			 });
		  } else {
			  postEvent(null, eventSerialized);
		  }
	  }
	  
	  private void postEvent(ParseFile file, final EventSerializable eventSerialized){
			String strFamily = eventSerialized.getFamily();
			if (strFamily == null) {
				Log.e(context.getClass().getName(), "It is required that you be associated with a " +
						" Family Account before you can share events.  Please set the same in your" +
						" Settings");
			} else {
				Log.i(context.getClass().getName(), "Posting Event... ");
				ParseACL eventACL = new ParseACL(ParseUser.getCurrentUser());
				eventACL.setPublicReadAccess(true);
				eventACL.setPublicWriteAccess(true); //This is to accomodate splendid/razzledazzle/smiley updates for anonymous users in Tablet Version

				final Event event = new Event();
				event.setACL(eventACL);
				event.setContent(eventSerialized.getEventContent());
				
				if(file != null){
					event.setImage(file);
				}
				
				event.setAuthor(ParseUser.getCurrentUser());
				event.setFamily(strFamily);

				event.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							Log.v(getClass().getName(), "Adding event to user");
							ParseUser user = ParseUser.getCurrentUser();
							ParseRelation<ParseObject> events = user.getRelation("events");
							events.add(event);
							user.saveInBackground(new SaveCallback() {
								@Override
								public void done(ParseException e) {
									if (e == null) {
										Log.v(getClass().getName(), "Adding event to family");
										Family family = new Family();
										ParseRelation<ParseObject> relation = family.getRelation("events");
										relation.add(event);
										family.saveInBackground();
										if(eventCounterLock.tryLock()){
											try{
												eventCounterLock.lock();
												eventCounter++;
												if(eventCounter == eventNumCached){
													if(failedEvents.size() > 0){
														saveFailedEvents();
													}
													notifyUser();
												}
											} finally {
												eventCounterLock.unlock();												
											}
										}
									} else {
										setFailedEvent(eventSerialized);
										Log.v(getClass().getName(), "Error saving event to family: " + e.toString());
									}
								}
							});	
						} else {
							Log.v(getClass().getName(), "Error saving new event : " + e.toString());
						}
					}
				});
			}
	  }
	  
	  synchronized private void setFailedEvent(EventSerializable event){
		  if(failedEventsLock.tryLock()){
			  try{
				  failedEventsLock.lock();
				  failedEvents.add(event);
			  } finally {
				  failedEventsLock.unlock();
			  }
		  }
	  }
	  
	  private void saveFailedEvents(){
		  Log.d(getClass().getName(), "There were " + failedEvents.size() + " that were not sent");
		  EventsCache eventCache = new EventsCache(cacheKey, context);
		  
		  //save failed events back to cache	
		  for(EventSerializable event: failedEvents){
			  eventCache.addEvent(event);
	  	  }
	  }
	  
	  private void notifyUser() {
		  Log.v("notifyUser","notifying user");
		    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
		            new Intent(context, GalleryActivity.class), 0);

		    NotificationCompat.Builder mBuilder =
		            new NotificationCompat.Builder(context)
		            .setSmallIcon(R.drawable.ic_push_notification)
		            .setContentTitle("Lifebeam")
		            .setContentText("Offline Events sent!");
		    mBuilder.setContentIntent(contentIntent);
		    mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		    mBuilder.setAutoCancel(true);
		    NotificationManager mNotificationManager =
		        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    mNotificationManager.notify(1, mBuilder.build());

		} 
}
