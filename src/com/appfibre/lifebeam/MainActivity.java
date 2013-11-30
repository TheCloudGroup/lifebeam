package com.appfibre.lifebeam;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import com.parse.ParseAnalytics;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";

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

		ParseAnalytics.trackAppOpened(getIntent());

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
