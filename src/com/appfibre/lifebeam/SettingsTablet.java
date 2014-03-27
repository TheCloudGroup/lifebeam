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
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;


import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

@SuppressWarnings("deprecation")
public class SettingsTablet extends PreferenceActivity{
	
	private String TAG = "SettingsActivity2";
	private Preference account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.phoneprefs);

		Preference terms = (Preference)findPreference("terms");
		terms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsTablet.this);

				dlgAlert.setMessage(Html.fromHtml(getString(R.string.terms_and_conditions)));
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
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsTablet.this);

				dlgAlert.setMessage(Html.fromHtml(getString(R.string.privacy_policy)));
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
				final Dialog dialog = new Dialog(SettingsTablet.this);

		        dialog.setContentView(R.layout.dialog_resetpassword_tablet);
	            dialog.setTitle("Reset Passcode");
	            
				final TextView tvOldPasscode       = (TextView)dialog.findViewById(R.id.oldPasscode);
				final TextView tvNewPasscode       = (TextView)dialog.findViewById(R.id.newPasscode);
				final TextView tvVerifyNewPasscode = (TextView)dialog.findViewById(R.id.verifyNewPasscode);

	            Button resetBtn = (Button) dialog.findViewById(R.id.resetFamilyPasscodeResetBtn);
	            
	            resetBtn.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                	final Session session = Session.getInstance();
	                	final String oldPasscode       = tvOldPasscode.getText().toString();
	                	final String newPasscode       = tvNewPasscode.getText().toString();
	                	final String verifyNewPasscode = tvVerifyNewPasscode.getText().toString();
	                	
	                	final String sessionPasscode    = session.getUserPasscode(SettingsTablet.this);
	                	final String sessionFamilyName  = session.getUserFamilyAccount(SettingsTablet.this);
	                	final String sessionFamilyObjId = session.getFamilyObjectId(SettingsTablet.this);
	                	
                		if(sessionPasscode != null && sessionPasscode.equals(oldPasscode)){
                			if(verifyNewPasscode.equals(newPasscode)){
                				Utils.showProgressDialog(SettingsTablet.this, "Resetting passcode");
                				Utils.hideSoftKeyboard(SettingsTablet.this);
                    			ParseQuery<Family> queryFamily = new ParseQuery<Family>("Family");
                    			queryFamily.getInBackground(sessionFamilyObjId, new GetCallback<Family>() {
                				    public void done(Family family, ParseException e) {
                				        if (e == null) {
                				        	family.setPassCode(newPasscode);
                							family.saveInBackground(new SaveCallback() {													
												@Override
												public void done(ParseException exception) {
													Utils.hideProgressDialog();
													if(exception == null){
														session.setUserPasscode(SettingsTablet.this, newPasscode);
														Toast.makeText(SettingsTablet.this, "Passcode changed successfully", Toast.LENGTH_SHORT).show();
													} else {
														Toast.makeText(SettingsTablet.this,exception.getMessage(), Toast.LENGTH_SHORT).show();
													}
												}
											});
                				        } else {
                				        	Utils.hideProgressDialog();
                				        	Toast.makeText(SettingsTablet.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                				        }
                				    }
                				});
                    			//queryFamily.whereEqualTo("name", sessionFamilyName);
                    			//queryFamily.whereEqualTo("passCode", sessionPasscode);
                    			/*queryFamily.findInBackground(new FindCallback<Family>() {
                    				public void done(List<Family> Families, ParseException e) {
                    					if (e == null) {
                    						if (Families.size() == 0) {
                    							Utils.hideProgressDialog();
                    							Toast.makeText(getApplicationContext(),
                    									"Either your Family Account or Passcode is not correct", Toast.LENGTH_LONG)
                    									.show();
                    						} else {
                    							Family family = Families.get(0);
                    							family.setPassCode(newPasscode);
                    							family.saveInBackground(new SaveCallback() {													
													@Override
													public void done(ParseException exception) {
														Utils.hideProgressDialog();
														if(exception == null){
															session.setUserPasscode(SettingsTablet.this, newPasscode);
															Toast.makeText(SettingsTablet.this, "Passcode changed successfully", Toast.LENGTH_SHORT).show();
														} else {
															Toast.makeText(SettingsTablet.this,exception.getMessage(), Toast.LENGTH_SHORT).show();
														}
													}
												});
                    						}
                    					} else {
                    						Utils.hideProgressDialog();
                    						Toast.makeText(getApplicationContext(),
                    								"Error: " + e.getMessage(), Toast.LENGTH_LONG)
                    								.show();
                    						Log.v(TAG, "Error: " + e.getMessage());
                    					}
                    				}
                    			});	*/
                			} else {
    	                		Toast.makeText(SettingsTablet.this, "New passcode does not match.", Toast.LENGTH_LONG).show();
                			}                			
                		} else {
	                		Toast.makeText(SettingsTablet.this, "Current passcode does not much provided old passcode.", Toast.LENGTH_LONG).show();
                		}                    	
	                }
	            });
	            
	            Button cancelBtn = (Button) dialog.findViewById(R.id.resetFamilyPasscodeCancelBtn);
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
		account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				String family = Session.getInstance().getSessionFamily(SettingsTablet.this);
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsTablet.this);
				dlgAlert.setMessage(family);
				dlgAlert.setTitle("Currently Associated Family Account");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
				return true;	
			}
		});
		
		Preference build = (Preference)findPreference("build");
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String versionName = pInfo.versionName;
		int versionCode = pInfo.versionCode;
		
		final String builD = "VersionName: " + versionName + "\nVersionCode: " + versionCode;
		
		build.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsTablet.this);

				dlgAlert.setMessage(builD);
				dlgAlert.setTitle("Build Version");
				dlgAlert.setPositiveButton("OK", null);
				dlgAlert.setCancelable(true);
				dlgAlert.create().show();
				return true;
			}
		});		
	}	
}
