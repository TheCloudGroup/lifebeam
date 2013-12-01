/**
 * 
 */
package com.appfibre.lifebeam;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author REBUCAS RENANTE
 *
 */
public class RegistrationFormActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationform);
		
		ActionBar ab = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
        ab.setBackgroundDrawable(colorDrawable);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayUseLogoEnabled(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_registration, menu);
		return true;
	}
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    
	    case android.R.id.home:
	    	startActivity(new Intent(RegistrationFormActivity.this,MainActivity.class));
	    	finish();
	    	return true; 
	    	
	    case R.id.menuNext:
	    	startActivity(new Intent(RegistrationFormActivity.this,RegisterFamilyActivity.class));
	    	finish();
	    	break;

	    default:            
	         return super.onOptionsItemSelected(item);
	    }

	    return true;
	  }
	 
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
	
		 return true;
	 }

}
