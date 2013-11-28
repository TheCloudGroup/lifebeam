/**
 * 
 */
package com.appfibre.lifebeam;


import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author REBUCAS RENANTE
 *
 */
public class RegistrationFormActivity extends Activity {
	
	private LinearLayout RegisterForm1,RegisterForm2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationform);
		
		ActionBar ab = getActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
        ab.setBackgroundDrawable(colorDrawable);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowHomeEnabled(false);
        
        RegisterForm1 = (LinearLayout) findViewById(R.id.LLregistrationform1);
        RegisterForm2 = (LinearLayout) findViewById(R.id.LLregistrationform2);

		

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration_menu, menu);
		return true;
	}
	
	 @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menuRegister:
	      Toast.makeText(this, "Action Register selected", Toast.LENGTH_SHORT)
	          .show();
	      break;
	    case R.id.menuNext:
	    	RegisterForm1.setVisibility(View.GONE);
	    	RegisterForm2.setVisibility(View.VISIBLE);
	      break;

	    default:
	      break;
	    }

	    return true;
	  }
	 
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
	
		 return true;
	 }

}
