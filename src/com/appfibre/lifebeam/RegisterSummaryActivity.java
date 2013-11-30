package com.appfibre.lifebeam;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RegisterSummaryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationsummary);
		
		ActionBar ab = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
        ab.setBackgroundDrawable(colorDrawable);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowHomeEnabled(false);
        
        final Button btnInvite = (Button) findViewById(R.id.btnInvite);
        final Button btnContinue = (Button) findViewById(R.id.btnContinue);
        
        btnInvite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), 
                        "Button Invite selected ", Toast.LENGTH_SHORT).show();
			}
		});
        
        btnContinue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), 
                        "Button Continue selected ", Toast.LENGTH_SHORT).show();
			}
		});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_registersummary, menu);
		return true;
	}
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menuInviteFamily:
	      Toast.makeText(this, "Action Invite Family selected", Toast.LENGTH_SHORT)
	          .show();
	      break;
	    case R.id.menuNext:
	    	  Toast.makeText(this, "Action Next selected", Toast.LENGTH_SHORT)
	          .show();
	      break;

	    default:
	      break;
	    }

	    return true;
	  }
}