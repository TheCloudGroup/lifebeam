package com.appfibre.lifebeam.utils;

import com.appfibre.lifebeam.LoginActivityTablet;
import com.appfibre.lifebeam.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author kimlambiguit
 */
public class StartUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && isTablet) {
            Intent actIntent = new Intent(context, LoginActivityTablet.class);
            actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(actIntent);
        }
		Log.d(getClass().getName(),"Detect phone startup");
	}

}
