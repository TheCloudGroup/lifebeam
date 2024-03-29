package com.appfibre.lifebeam;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.parse.ParseAnalytics;

public class MainActivity extends Activity implements OnClickListener{

	private static final String TAG = "MainActivity";
	private static final String APP_ID = "7e4f30dd5ccb0d568d1b1d1582b7db1d";
	
	private boolean isTablet;

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

		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (isTablet) {
			findViewById(R.id.llyTabletMain).setVisibility(View.VISIBLE);
			findViewById(R.id.llyMobileMain).setVisibility(View.GONE);
		} else {
			findViewById(R.id.llyTabletMain).setVisibility(View.GONE);
			findViewById(R.id.llyMobileMain).setVisibility(View.VISIBLE);
		}

		findViewById(R.id.btnRegister).setOnClickListener(this);
		findViewById(R.id.btnLogin).setOnClickListener(this);
		findViewById(R.id.btnLogin2).setOnClickListener(this);

		final Button btnLogin = (Button) findViewById(R.id.btnLogin);
		final Button btnLogin2 = (Button) findViewById(R.id.btnLogin2);
		
		/*btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(MainActivity.this,LoginActivity.class));
			}
		});*/

		if (!isTablet && SharedPrefMgr.getBool(this, "hasSetKeptLogin")) {
			btnLogin.performClick();
		} 
		
		if(isTablet){
			Session session = Session.getInstance();
			String familyAccount = session.getUserFamilyAccount(getApplicationContext());
			String passCode = session.getUserPasscode(getApplicationContext());
			if ( familyAccount!= null && familyAccount.length() > 0 && 
				 passCode !=null &&  passCode.length() > 0 ) {
				btnLogin2.performClick();
			}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRegister:
			startActivity(new Intent(MainActivity.this,RegisterActivity.class));
			break;

		case R.id.btnLogin:
		case R.id.btnLogin2:
			if (isTablet) {
				startActivity(new Intent(MainActivity.this,LoginActivityTablet.class));
			} else {
				startActivity(new Intent(MainActivity.this,LoginActivity.class));	
			}
			break;
		default:
			break;
		}
	}

}
