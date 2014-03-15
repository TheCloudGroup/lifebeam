/**
 * 
 */
package com.appfibre.lifebeam;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;


import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.RequestPasswordResetCallback;
public class SettingsActivity2 extends PreferenceActivity{
	
	private String TAG = "SettingsActivity2";
	private boolean isCreateNewAccount;
	private TextView txtFamilyLabel;
	private EditText edtFamilyName;
	private EditText edtPassCode;
	private boolean isAssociated;
	private String associationMessage;
	private Family family;
	private Preference account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

		Preference terms = (Preference)findPreference("terms");
		terms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity2.this);

				dlgAlert.setMessage("This will contain terms and coditions here");
				dlgAlert.setTitle("Terms and Conditions");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
				return true;
			}
		});
		
		Preference privacy = (Preference)findPreference("privacy");
		privacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity2.this);

				dlgAlert.setMessage("This will contain app privacy statements here");
				dlgAlert.setTitle("Privacy Statement");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
				return true;
			}
		});
		
		Preference passwordReset = (Preference)findPreference("passwordReset");
		passwordReset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				final Dialog dialog = new Dialog(SettingsActivity2.this);

		        dialog.setContentView(R.layout.dialog_resetpassword);
				final TextView email    = (TextView)dialog.findViewById(R.id.resetPasswordEmail);
				
	            // Set dialog title
	            dialog.setTitle("Reset Password");

	            Button resetBtn = (Button) dialog.findViewById(R.id.resetPasswordResetBtn);
	            resetBtn.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	String sEmail = email.getText().toString();
	                	String sessionEmail = Session.getInstance().getUserName(SettingsActivity2.this);
                		if(sessionEmail != null && sessionEmail.equals(sEmail)){
                			Utils.showProgressDialog(SettingsActivity2.this, "Sending reset password instructions");
	                		ParseUser.requestPasswordResetInBackground(sEmail,
	                                new RequestPasswordResetCallback() {
								public void done(ParseException e) {
									if (e == null) {
										Utils.hideProgressDialog();
				                		Toast.makeText(SettingsActivity2.this, "Password reset instructions sent!", Toast.LENGTH_LONG).show();
				                		dialog.dismiss();
									} else {
										Toast.makeText(SettingsActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
									}
								}
							});
                		} else {
	                		Toast.makeText(SettingsActivity2.this, "Email does not match currently logged in user.", Toast.LENGTH_LONG).show();
                		}                    	
	                }
	            });
	            
	            Button cancelBtn = (Button) dialog.findViewById(R.id.resetPasswordCancelBtn);
	            cancelBtn.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    dialog.dismiss();
	                }
	            });
	            dialog.show();
		        return true;
			}
		});
		
		account = (Preference)findPreference("account");
		ParseUser currentUser = ParseUser.getCurrentUser();
		final String family = currentUser.getString("family");
		account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				Log.v(TAG, "reconfirm that there is an associated family for this user");
				String strFamily = ParseUser.getCurrentUser().getString("family");
				if (strFamily == null) {
					AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity2.this);

					dlgAlert.setMessage("You need to be associated to a" +
							" Family Account.  Please join or create your own Family Account.");
					dlgAlert.setTitle("No Associated Family Account");
					dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							SharedPrefMgr.setBool(SettingsActivity2.this, "isFromSettings", true);
							startActivity(new Intent(SettingsActivity2.this, AssociateFamilyActivity.class));
						}});
					dlgAlert.setCancelable(true);
					dlgAlert.create().show();
					return true;
				} else {
					AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity2.this);

					dlgAlert.setMessage(family);
					dlgAlert.setTitle("Currently Associated Family Account");
					dlgAlert.setPositiveButton("OK", null);
					dlgAlert.setCancelable(true);
					dlgAlert.create().show();
					return true;	
				}
				
				
				
			}
		});
		
		Preference build = (Preference)findPreference("build");
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String versionName = pInfo.versionName;
		int versionCode = pInfo.versionCode;
		
		final String builD = "VersionName: " + versionName + "\nVersionCode: " + versionCode;
		
		build.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity2.this);

				dlgAlert.setMessage(builD);
				dlgAlert.setTitle("Build Version");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
				return true;
			}
		});
		
		final CheckBoxPreference keepLoggedIn = (CheckBoxPreference)findPreference("keepLoggedIn");
		keepLoggedIn.setChecked(SharedPrefMgr.getBool(SettingsActivity2.this, "hasSetKeptLogin"));
		keepLoggedIn.setOnPreferenceClickListener(new CheckBoxPreference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				// TODO Auto-generated method stub
				if (keepLoggedIn.isChecked()) {
					SharedPrefMgr.setBool(SettingsActivity2.this, "hasSetKeptLogin", true);
					return true;
				} else {
					SharedPrefMgr.setBool(SettingsActivity2.this, "hasSetKeptLogin", false);
					return false;
				}
			}
		});
	}
	
	private boolean createNewFamilyAccount() {
		Utils.showProgressDialog(SettingsActivity2.this, "Creating new Family Account...");
		isAssociated = false;
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
								isAssociated = true;
							} else {
								Log.v(TAG, "Error saving new family to current user : " + e.toString());
								associationMessage = "Error saving new family to current user. \n\n" +
										" Please try again later";
							}
						}

					});	
				} else {
					Log.v(TAG, "Error saving new family : " + e.toString());
				}
			}
		});
		return isAssociated;
	}

	private boolean associateWithExistingAccount() {

		// Show a progress spinner, and kick off a background task to
		Utils.showProgressDialog(this, "Looking up your Family Account...");
		isAssociated = false;
		
		ParseQuery<Family> queryFamily = new ParseQuery<Family>("Family");
		queryFamily.whereEqualTo("name", edtFamilyName.getText().toString());
		queryFamily.whereEqualTo("passCode", edtPassCode.getText().toString());
		queryFamily.findInBackground(new FindCallback<Family>() {
			public void done(List<Family> Families, ParseException e) {
				Utils.hideProgressDialog();
				if (e == null) {
					if (Families.size() == 0) {
						associationMessage = "Either your Family Account or Passcode is not correct";
					} else {
						isAssociated = true;
						ParseUser.getCurrentUser().add("family", edtFamilyName.getText().toString());
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Error: " + e.getMessage(), Toast.LENGTH_LONG)
							.show();
					Log.v(TAG, "Error: " + e.getMessage());
				}
			}
		});	
		
		return isAssociated;
		
	} 
}