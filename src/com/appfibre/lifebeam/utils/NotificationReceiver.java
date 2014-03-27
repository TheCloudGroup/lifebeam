package com.appfibre.lifebeam.utils;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver{
	private static final String TAG = "NotificationReceiver";
	 
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            SharedPrefMgr.setString(context, "updatedEventId", json.getString("eventId"));
            
            Iterator itr = json.keys();
            
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(TAG, "..." + key + " => " + json.getString(key));
                if(key.equals("eventId")){
                	SharedPrefMgr.setString(context, "updatedEventId", json.getString(key));
                	break;
                }                
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
}
