package com.appfibre.lifebeam;

import java.util.List;

import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AssociateFamilyActivity extends Activity implements OnClickListener {
	
	private String TAG = "AssociateFamilyActivity";
	private boolean isCreateNewAccount;
	private TextView txtFamilyLabel;
	private EditText edtFamilyName;
	private EditText edtPassCode;
	private Family family;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_create_family);
		
		edtFamilyName = (EditText) findViewById(R.id.edtFamilyName);
		edtPassCode = (EditText) findViewById(R.id.edtPassCode);
		txtFamilyLabel = (TextView) findViewById(R.id.txtFamilyLabel);
		
		findViewById(R.id.btnNext).setOnClickListener(this);
		findViewById(R.id.btnCancel).setOnClickListener(this);
		
		final CheckBox chkFamily = (CheckBox) findViewById(R.id.chkFamily);
		
		chkFamily.setChecked(false);
		chkFamily.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					txtFamilyLabel.setText("Create");
					isCreateNewAccount = true;
				}else{
					txtFamilyLabel.setText("Join");
					isCreateNewAccount = false;
				}

			}
		});
		
	}
	
	private void createNewFamilyAccount() {
		Utils.showProgressDialog(AssociateFamilyActivity.this, "Creating new Family Account...");
		ParseACL eventACL = new ParseACL(ParseUser.getCurrentUser());
		eventACL.setPublicReadAccess(true);

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
					user.add("family", edtFamilyName.getText().toString());
					user.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							Utils.hideProgressDialog();
							if (e == null) {
								Log.v(TAG, "successfully added family here");
								if (SharedPrefMgr.getBool(AssociateFamilyActivity.this, "isFromSettings")) {
									SharedPrefMgr.setBool(AssociateFamilyActivity.this, "isFromSettings", false);
									finish();
								} else {
									startActivity(new Intent(AssociateFamilyActivity.this, GalleryActivity.class));
									finish();	
								}
							} else {
								Log.v(TAG, "Error saving new family to current user : " + e.toString());
								Toast.makeText(AssociateFamilyActivity.this, "Error saving new family to current user.", Toast.LENGTH_SHORT).show();
							}
						}

					});	
				} else {
					Log.v(TAG, "Error saving new family : " + e.toString());
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
		queryFamily.getFirstInBackground(new GetCallback<Family>() {
		//queryFamily.findInBackground(new FindCallback<Family>() {

		@Override
		public void done(Family family, ParseException e) {
			Utils.hideProgressDialog();
			if (e == null) {
				if (family == null) {
					Toast.makeText(getApplicationContext(),
							"Either your Family Account or Passcode is not correct", Toast.LENGTH_LONG)
							.show();
					Log.v(TAG, "Either your Family Account or Passcode is not correct");
				} else {
					ParseUser.getCurrentUser().put("family", edtFamilyName.getText().toString());
					ParseUser.getCurrentUser().saveEventually();
					if (SharedPrefMgr.getBool(AssociateFamilyActivity.this, "isFromSettings")) {
						SharedPrefMgr.setBool(AssociateFamilyActivity.this, "isFromSettings", false);
						finish();
					} else {
						startActivity(new Intent(AssociateFamilyActivity.this, GalleryActivity.class));
						finish();	
					}
					finish();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Error associating to Family Account : " + e.getMessage(), Toast.LENGTH_LONG)
						.show();
				Log.v(TAG, "Error associating to Family Account : " + e.getMessage());
			}
		}

		});	
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			finish();
			break;
		case R.id.btnNext:
			Log.v(TAG, "Save New fAmily or Associate with existing family account");
			if ("".equals(edtFamilyName.getText().toString())) {
				Toast.makeText(AssociateFamilyActivity.this, "Family Name is Required...", Toast.LENGTH_SHORT).show();
				edtFamilyName.requestFocus();
				return;
			}
			if ("".equals(edtPassCode.getText().toString())) {
				Toast.makeText(AssociateFamilyActivity.this, "Passcode is Required...", Toast.LENGTH_SHORT).show();
				edtPassCode.requestFocus();
				return;
			}

			if (isCreateNewAccount) {
				Log.v(TAG, "save new family name account in parse here");
				createNewFamilyAccount();
			} else {
				Log.v(TAG, "attempt to associate account");
				associateWithExistingAccount();
			}
			break;

		default:
			break;
		}
	} 
}
