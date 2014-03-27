package com.appfibre.lifebeam.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

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
		return new SimpleDateFormat("MM/dd/yyyy", Locale.US);
	}
	
	public static SimpleDateFormat getTimeFormat(){
		return new SimpleDateFormat("hh:mm aa", Locale.US);
	}
	
	public static void hideSoftKeyboard(Context context){
        if(context != null && context.getSystemService(Activity.INPUT_SERVICE) != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
}
