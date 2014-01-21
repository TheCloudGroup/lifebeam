package com.appfibre.lifebeam;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.parse.ParseAnalytics;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private static final String APP_ID = "7e4f30dd5ccb0d568d1b1d1582b7db1d";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Add code to print out the key hash
		/*try {
			PackageInfo info = getPackageManager().getPackageInfo("com.appfibre.lifebeam", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "namenotfoound exception: " + e.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "nosuch algorithm: " + e.toString());
		}*/

		/*Log.v(TAG, "run a separate service to load up contacts with emails");
		Intent intent = new Intent(LoginActivity.this, DownloadOffersService.class);
		// add infos for the service which file to download and where to store
		//intent.putExtra(DownloadOffersService.SESSION_ID, sessionID);
		//intent.putExtra(DownloadOffersService.PHONE_LAT, currentLat);
		//intent.putExtra(DownloadOffersService.PHONE_LONG, currentLong);
		//intent.putIntegerArrayListExtra(DownloadOffersService.CATEGORY_IDS, (ArrayList<Integer>) category_ids);

		startService(intent);*/
		
		
		ParseAnalytics.trackAppOpened(getIntent());
		
		checkForUpdates();

		final Button btnRegister = (Button) findViewById(R.id.btnRegister);
		final Button btnLogin = (Button) findViewById(R.id.btnLogin);

		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,RegisterActivity.class));
			}
		});

		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,LoginActivity.class));
			}
		});
		
		if (SharedPrefMgr.getBool(this, "hasSetKeptLogin")) {
			btnLogin.performClick();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkForCrashes();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 private void checkForCrashes() {
		   CrashManager.register(this, APP_ID);
		 }

		 private void checkForUpdates() {
		   // Remove this for store builds!
		   UpdateManager.register(this, APP_ID);
		 }

}
