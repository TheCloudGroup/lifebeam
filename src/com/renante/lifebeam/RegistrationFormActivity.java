/**
 * 
 */
package com.renante.lifebeam;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

/**
 * @author REBUCAS RENANTE
 *
 */
public class RegistrationFormActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationform);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
