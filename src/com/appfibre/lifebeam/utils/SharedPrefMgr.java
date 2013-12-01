package com.appfibre.lifebeam.utils;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Utility Class to deal with shared preferences
 * @author christian
 * 
 */
public class SharedPrefMgr {

    private static final String PREFS_NAME = "MeechaPreferences";
    
    /**
     * Returns an object of SharedPrefereces class that this class manages
     * @param context
     * @return an object of {@link SharedPreferences} with name PokitPalPreferences
     */
    private static SharedPreferences getSharedPrefObject(Context context){
        SharedPreferences sharedPref = null;
        if(context != null ){
            sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        } else {
            Log.e("Error", "Invalid context parameter");
        }
        
        return sharedPref;
    }
    
    /**
     * Sets a string preference value
     * @param context
     * @param name  the name  of the preference to set
     * @param value the value of the preference "name"
     */
    public static void setString(Context context, String name, String value) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        Editor prefsEditor = preferences.edit();
        prefsEditor.putString(name, value);
        prefsEditor.commit();
        
        prefsEditor = null;
        preferences = null;
        Log.v("prefs name", name);
    }

    /**
     * Gets a string preference value
     * @param context
     * @param name the preference to retrieve
     * @return
     */
    public static String getString(Context context, String name) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        String data = preferences.getString(name, "");
        Log.v("prefs name", name);
        
        preferences = null;
        return data;
    }

    /**
     * Sets a string preference value
     * @param context
     * @param name  the name  of the preference to set
     * @param value the value of the preference "name"
     */
    public static void setInt(Context context, String name, int value) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        Editor prefsEditor = preferences.edit();
        prefsEditor.putInt(name, value);
        prefsEditor.commit();
        
        prefsEditor = null;
        preferences = null;
        Log.v("prefs name", name);
    }

    /**
     * Gets a string preference value
     * @param context
     * @param name the preference to retrieve
     * @return
     */
    public static int getInt(Context context, String name) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        int data = preferences.getInt(name, 0);
        Log.v("prefs name", name);
        
        preferences = null;
        return data;
    }
    
    /**
     * Sets a boolean preference value
     * @param context
     * @param name  the name  of the preference to set
     * @param value the value of the preference "name"
     */
    public static void setBool(Context context, String name, boolean value) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(name, value);
        prefsEditor.commit();
        
        
        prefsEditor = null;
        preferences = null;
        Log.v("prefs name", name);
    }

    /**
     * Gets a boolean preference value
     * @param context
     * @param name the preference to retrieve
     * @return
     */
    public static Boolean getBool(Context context, String name) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        boolean data = preferences.getBoolean(name, false);
        Log.v("prefs name", name);
        
        preferences = null;
        return data;
    }

    /**
     * Sets a long preference value
     * @param context
     * @param name  the name  of the preference to set
     * @param value the value of the preference "name"
     */
    public static void setLong(Context context, String name, long value) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        Editor prefsEditor = preferences.edit();
        prefsEditor.putLong(name, value);
        prefsEditor.commit();
        
        prefsEditor = null;
        preferences = null;
        
        Log.v("prefs name", name);
    }

    /**
     * Gets a long preference value
     * @param context
     * @param name the preference to retrieve
     * @return
     */
    public static Long getLong(Context context, String name) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        long data = preferences.getLong(name, 0);
        Log.v("prefs name", name);
        
        preferences = null;
        return data;
    }
    
    /**
     * Deletes the preference data where name is 'name'
     * @param context
     * @param name  the name  of the preference to be deleted
     */
    public static void removeData(Context context, String name) {
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        Editor prefsEditor = preferences.edit();
        prefsEditor.remove(name);
        prefsEditor.commit();
        
        prefsEditor = null;
        preferences = null;
        
        Log.v("prefs name", name);
    }
    
    /**
     * Returns all preferences data
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context){
        SharedPreferences preferences = SharedPrefMgr.getSharedPrefObject(context);
        
        Map<String, ?> data = preferences.getAll();
        preferences = null;
        
        return data;
    }
    
    
}
