package com.appfibre.lifebeam.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.appfibre.lifebeam.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
    
    static MemoryCache memoryCache=new MemoryCache();
    static FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService; 
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    final int stub_id=R.drawable.no_image;
    //final int stub_id=R.drawable.no_image;
    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }
    
    public void DisplayImage_Scaled(String url, ImageView imageView)
    {
        int width, height;
    	imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
        	//scale the bimap here
            width			= bitmap.getWidth();
        	height 			= bitmap.getHeight();
        	int newWidth 	= 100;
        	int newHeight 	= 100;
	        // calculate the scale 
	        float scaleWidth = ((float) newWidth) / width;
	        float scaleHeight = ((float) newHeight) / height;

            // createa matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);
     
            // recreate the new Bitmap
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                              width, height, matrix, true);
       
            // make a Drawable from Bitmap to allow to set the BitMap
            // to the ImageView, ImageButton or what ever
            //BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
        	
        	
            imageView.setImageBitmap(resizedBitmap);
        
        }else{
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }
        
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {
    	Bitmap bm = null;
        File f=fileCache.getFile(url);
        if(f.exists()){ //from SD cache
        	bm = decodeFile(f);
        } else {
            try { //from web
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is=conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Utils.CopyStream(is, os);
                os.close();
                bm = decodeFile(f);
            } catch( MalformedURLException e){
            	Log.e("ImageLoader.getBitmap", e.getMessage());
            } catch( IOException e){
            	Log.e("ImageLoader.getBitmap", e.getMessage());
            } catch (OutOfMemoryError e){
            	Log.e("ImageLoader.getBitmap", e.getMessage());
               memoryCache.clear();
            }
        }
        
        return bm;
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
    	Bitmap bm = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;

            //File f = new File(imagePath);
            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);
            bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), new Matrix(), true);

            ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100,
                    baOutStream);
        } catch (IOException e) {
            Log.e("ImageLoader.decodeFile", e.getMessage());
        } catch (OutOfMemoryError e) {
        	Log.e("ImageLoader.decodeFile", e.getMessage());
        	memoryCache.clear();
        }
        return bm;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                Activity a=(Activity)photoToLoad.imageView.getContext();
                a.runOnUiThread(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public static void clearMemoryCache() {
        memoryCache.clear();
    }
    
    public static void clearFileCache() {
    	fileCache.clear();
    }

	public static Object getMemoryCache() {
		// TODO Auto-generated method stub
		return memoryCache;
	}
	
	public static Object getFileCache() {
		// TODO Auto-generated method stub
		return fileCache;
	}

    public static void clearOldFileCache() {
    	fileCache.clearold();
    }

	
}
