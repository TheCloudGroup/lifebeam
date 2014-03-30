package com.appfibre.lifebeam.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.appfibre.lifebeam.classes.EventSerializable;

public class EventsCache {
    private String KEY;
    private Context context;
    
    public EventsCache(String key, Context context){
    	this.KEY = key;
    	this.context = context;
    }
    
    public void addEvent(EventSerializable event){
    	try{
    		List<EventSerializable> events = getEvents();
    		if(emptyCache()){
    			events.add(event);
    			writeObject(events);
    		} else {
    			Log.e(getClass().getName(), "Unable to empty cache for cache " + KEY);
    		}
    	} catch (FileNotFoundException fne){
    		List<EventSerializable> events = new ArrayList<EventSerializable>();
    		events.add(event);
    		try {
				writeObject(events);
			} catch (IOException e) {
				Log.e(getClass().getName(), e.getMessage());
				e.printStackTrace();
			}
    	} catch (IOException ioe) {
    		Log.e(getClass().getName(), ioe.getMessage());
    	} catch (ClassNotFoundException cce) {
    		Log.e(getClass().getName(), cce.getMessage());
    	}
    }
    
    private void writeObject(List<EventSerializable> object) throws IOException {
        FileOutputStream fos = context.openFileOutput(KEY, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }
   
    public List<EventSerializable> getEvents() throws FileNotFoundException, IOException,
           ClassNotFoundException {
        FileInputStream fis = this.context.openFileInput(KEY);
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<EventSerializable> object = (List<EventSerializable>)ois.readObject();
        return object;
     }
    
    public boolean emptyCache(){
    	File file = new File(KEY);
    	return file.delete();
    }
}