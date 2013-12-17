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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * @author REBUCAS RENANTE
 *
 */
public class SettingsActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayUseLogoEnabled(false);
        
        final Button btnTermsAndConditions = (Button)findViewById(R.id.btnTermsAndCondition);
        final Button btnPrivacyStatement = (Button)findViewById(R.id.btnPrivacyStatement);
        final Button btnAccount = (Button)findViewById(R.id.btnAccount);
        
        btnTermsAndConditions.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Button Terms And Conditions selected",
						   Toast.LENGTH_SHORT).show();
			}
		});
        
        btnPrivacyStatement.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Button Privacy Statement selected",
						   Toast.LENGTH_SHORT).show();
			}
		});
		
        btnAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Button Account selected",
						   Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
	    	startActivity(new Intent(SettingsActivity.this,GalleryActivity.class));
	    	finish();
	    	return true; 
		}
		
		return true;
	}
}
