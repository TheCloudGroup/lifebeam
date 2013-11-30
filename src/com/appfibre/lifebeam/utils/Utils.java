package com.appfibre.lifebeam.utils;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.Context;

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
		if(pDialog != null){
			if(pDialog.isShowing()){
				pDialog.dismiss();
				pDialog = null;
			}
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
}
