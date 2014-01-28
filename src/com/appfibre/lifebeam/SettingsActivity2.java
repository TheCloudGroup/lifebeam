/**
 * 
 */
package com.appfibre.lifebeam;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.parse.ParseUser;

public class SettingsActivity2 extends PreferenceActivity{

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
		
		Preference account = (Preference)findPreference("account");
		ParseUser currentUser = ParseUser.getCurrentUser();
		final String family = currentUser.getString("family");
		account.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SettingsActivity2.this);

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
}