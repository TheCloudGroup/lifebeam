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
import android.widget.Toast;

/**
 * @author REBUCAS RENANTE
 *
 */
public class RegisterFamilyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registerfamily);
		
		ActionBar ab = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
        ab.setBackgroundDrawable(colorDrawable);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowHomeEnabled(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_registerfamily, menu);
		return true;
	}
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menuRegisterFamilyAccount:
	      Toast.makeText(this, "Action Register Family Account selected", Toast.LENGTH_SHORT)
	          .show();
	      break;
	    case R.id.menuNext:
	    	startActivity(new Intent(RegisterFamilyActivity.this,RegisterSummaryActivity.class));
	    	finish();
	      break;

	    default:
	      break;
	    }

	    return true;
	  }

}
