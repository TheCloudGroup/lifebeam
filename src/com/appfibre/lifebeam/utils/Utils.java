package com.appfibre.lifebeam.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

public class Utils {
	private static ProgressDialog pDialog = null;

	public static void showProgressDialog(Context context, String message){
		hideProgressDialog();
		pDialog = new ProgressDialog(context);
		pDialog.setMessage(message);
		pDialog.setIndeterminate(true);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	public static void hideProgressDialog(){
		if(pDialog != null && pDialog.isShowing()){			
			pDialog.dismiss();
			pDialog = null;
		}
	}

	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}
	
	public static SimpleDateFormat getDateFormat(){
		return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	}
	
	public static SimpleDateFormat getTimeFormat(){
		return new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
	}
	
	public static void hideSoftKeyboard(Context context){
        if(context != null && context.getSystemService(Activity.INPUT_SERVICE) != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
	
	public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return null != activeNetwork;
    }
	
	public static Bitmap resizeBitmap(Bitmap bitmap, Context context) {
        int width, height;
        Bitmap resizedBitmap = null;
        if(bitmap!=null){
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            int bounding = dpToPx(450, context);

            float xScale = ((float) bounding) / width;
            float yScale = ((float) bounding) / height;
            float scale = (xScale <= yScale) ? xScale : yScale;

            // Create a matrix for the scaling and add the scaling data
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            // Create a new bitmap and convert it to a format understood by the ImageView 
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        
        return resizedBitmap;
    }
	
	public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}
