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
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

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
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayUseLogoEnabled(false);
        
        final TableRow TRpasscode = (TableRow) findViewById(R.id.tRPasscode);
        final TableLayout TLfamilymembers = (TableLayout) findViewById(R.id.TLFamilyMembers);
        
        Switch switchFamily = (Switch) findViewById(R.id.switchFamily);
        switchFamily.setChecked(false);
        //TRpasscode.setVisibility(View.VISIBLE);
        switchFamily.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					TRpasscode.setVisibility(View.GONE);
					TLfamilymembers.setVisibility(View.VISIBLE);
				}else{
					TRpasscode.setVisibility(View.VISIBLE);
					TLfamilymembers.setVisibility(View.GONE);
				}
				
			}
		});
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
	    	startActivity(new Intent(RegisterFamilyActivity.this,RegistrationFormActivity.class));
	    	finish();
	    	return true; 
	    	
	    case R.id.menuNext:
	    	startActivity(new Intent(RegisterFamilyActivity.this,RegisterSummaryActivity.class));
	    	finish();
	      break;

	    default:
	    	return super.onOptionsItemSelected(item);
	    }

	    return true;
	  }

}
