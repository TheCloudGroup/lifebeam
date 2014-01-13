package com.appfibre.lifebeam.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class CameraUtils {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private static String TAG = "CameraUtils";

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type, String packageName){
		return Uri.fromFile(getOutputMediaFile(type, packageName));
	}

	/** Create a File for saving an image or video */
	@SuppressLint("NewApi")
	private static File getOutputMediaFile(int type, String packageName){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"IMG_"+ timeStamp + ".jpg");

			//mediaFile = new File("data/data/" + packageName + "/files/" +
			//		"IMG_"+ timeStamp + ".jpg");
		} else if(type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator +
					"VID_"+ timeStamp + ".mp4");
		} else {
			return null;
		}
		return mediaFile;
	}

	public static byte[] convertUriToBytes(Context context, Uri uri){
		byte[] data = null;
		ByteArrayOutputStream blob = null;
		try {
			
			
			BitmapFactory.Options options=new BitmapFactory.Options();
			options.inSampleSize = 8;
			ContentResolver cr = context.getContentResolver();
			InputStream inputStream = cr.openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();

			//resize it here
			Bitmap original = BitmapFactory.decodeByteArray(data , 0, data.length);
			Log.v(TAG, "original height = " + original.getHeight());
			Log.v(TAG, "original width = " + original.getWidth());
			
			int REQUIRED_SIZE = 300; //either its width or height should be within this
			int width_tmp=original.getWidth(), height_tmp=original.getHeight();
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
			
            Log.v(TAG, "height or width should not be less than 300 here unless it originally was");
            Log.v(TAG, "new reduced height = " + height_tmp);
			Log.v(TAG, "new reduced width = " + width_tmp);
            
			Bitmap resized = Bitmap.createScaledBitmap(original, width_tmp, height_tmp, true);

			blob = new ByteArrayOutputStream();
			resized.compress(Bitmap.CompressFormat.JPEG, 100, blob);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return blob.toByteArray();
	}
}
