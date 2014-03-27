/**
 * 
 */
package com.appfibre.lifebeam;

import java.util.ArrayList;
import java.util.List;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

/**
 * @author REBUCAS RENANTE
 *
 */
public class RegisterFamilyActivity extends Activity {

	private final String TAG = "RegisterFamilyActivity";
	private boolean isCreateNewAccount;
	private EditText edtFamilyName, edtPassCode;
	private Family family;
	private ArrayList<Family> FamilY;

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

		edtFamilyName = (EditText) findViewById(R.id.edtFamilyName);
		edtPassCode = (EditText) findViewById(R.id.edtPassCode);

		Switch switchFamily = (Switch) findViewById(R.id.switchFamily);
		switchFamily.setChecked(false);
		//TRpasscode.setVisibility(View.VISIBLE);
		switchFamily.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					((TextView) findViewById(R.id.txtFamilyLabel)).setText("Family Account to Create");
					isCreateNewAccount = true;
					//TRpasscode.setVisibility(View.GONE);
					//TLfamilymembers.setVisibility(View.VISIBLE);
				}else{
					((TextView) findViewById(R.id.txtFamilyLabel)).setText("Family Account to Join");
					isCreateNewAccount = false;
					//TRpasscode.setVisibility(View.VISIBLE);
					//TLfamilymembers.setVisibility(View.GONE);
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
			//startActivity(new Intent(RegisterFamilyActivity.this,RegisterSummaryActivity.class));
			Log.v(TAG, "Save New fAmily or Associate with existing family account");
			if ("".equals(edtFamilyName.getText().toString())) {
				Toast.makeText(RegisterFamilyActivity.this, "Family Name is Required...", Toast.LENGTH_SHORT).show();
				edtFamilyName.requestFocus();
				return true;
			}
			if ("".equals(edtPassCode.getText().toString())) {
				Toast.makeText(RegisterFamilyActivity.this, "Passcode is Required...", Toast.LENGTH_SHORT).show();
				edtPassCode.requestFocus();
				return true;
			}

			if (isCreateNewAccount) {
				Log.v(TAG, "save new family name account in parse here");
				createNewFamilyAccount();
			} else {
				Log.v(TAG, "attempt to associate account");
				associateWithExistingAccount();
			}
			//finish();
			break;	

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	private void createNewFamilyAccount() {
		Utils.showProgressDialog(RegisterFamilyActivity.this, "Creating new Family Account...");
		ParseACL eventACL = new ParseACL(ParseUser.getCurrentUser());
		eventACL.setPublicReadAccess(true);
		eventACL.setPublicWriteAccess(true);

		family = new Family();
		family.setACL(eventACL);
		family.setName(edtFamilyName.getText().toString());
		family.setPassCode(edtPassCode.getText().toString());

		family.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				Utils.hideProgressDialog();	
				if (e == null) {
					Log.v(TAG, "now add this event to the user ");
					ParseUser user = ParseUser.getCurrentUser();
					ParseRelation<ParseObject> families = user.getRelation("families");
					families.add(family);
					Log.v(TAG, "sucessfully added family object, now adding user family " + edtFamilyName.getText().toString());
					user.put("family", edtFamilyName.getText().toString());
					user.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							Utils.hideProgressDialog();
							if (e == null) {
								Log.v(TAG, "successfully added family here");
								Log.v(TAG, "Now we call library after saving and display all saved events");
								startActivity(new Intent(RegisterFamilyActivity.this, LoginActivity.class));
								finish();
							} else {
								Log.v(TAG, "Error saving new family to current user : " + e.toString());
							}
						}

					});	
				} else {
					Log.v(TAG, "Error saving new event : " + e.toString());
				}
			}
		});
	}

	private void associateWithExistingAccount() {

		// Show a progress spinner, and kick off a background task to
		Utils.showProgressDialog(this, "Looking up your Family Account...");
		
		ParseQuery<Family> queryFamily = new ParseQuery<Family>("Family");
		queryFamily.whereEqualTo("name", edtFamilyName.getText().toString());
		queryFamily.whereEqualTo("passCode", edtPassCode.getText().toString());
		queryFamily.findInBackground(new FindCallback<Family>() {
			public void done(List<Family> Families, ParseException e) {
				Utils.hideProgressDialog();
				if (e == null) {
					if (Families.size() == 0) {
						Toast.makeText(getApplicationContext(),
								"Either your Family Account or Passcode is not correct", Toast.LENGTH_LONG)
								.show();
					} else {
						ParseUser.getCurrentUser().add("family", edtFamilyName.getText().toString());
						startActivity(new Intent(RegisterFamilyActivity.this, LoginActivity.class));
						finish();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Error: " + e.getMessage(), Toast.LENGTH_LONG)
							.show();
					Log.v(TAG, "Error: " + e.getMessage());
				}
			}
		});	
	} 
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
