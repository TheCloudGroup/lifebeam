package com.appfibre.lifebeam.utils;

import java.io.File;
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

    public void clearold(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files){
        	Date lastModDate = new Date(f.lastModified());
        	Log.v("last modified" , "File last modified @ : "+ lastModDate.toString());
        	/** Today's date */
            Date today = new Date();
            //subtract to get days
            long diff = today.getTime() - lastModDate.getTime();
            long days_stored = diff / (1000 * 60 * 60 * 24);
            if(days_stored > 7){
            	f.delete();
            }
            
        }
    	
    }
}