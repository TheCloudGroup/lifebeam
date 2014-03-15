/**
 * 
 */
package com.appfibre.lifebeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author REBUCAS RENANTE
 *
 */
public class RegistrationFormActivity extends Activity {

	private String TAG = "RegisterActivity";
	
	private EditText mFirstNameView;
	private EditText mLastNameView;
	private EditText mMobileNumberView;
	private EditText mEmailAddressView;
	private EditText mPasswordView;
	
	private String mFirstName = "";
	private String mLastName = "";
	private String mMobileNumber = "";
	private String mEmailAddress = "";
	private String mPassword = "";
	private View mRegisterFormView;
	private View mRegisterStatusView;
	private TextView mRegisterStatusMessageView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationform);


		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayUseLogoEnabled(false);
		
		mFirstNameView = (EditText) findViewById(R.id.firstname);
		mLastNameView = (EditText) findViewById(R.id.lastname);
		mMobileNumberView = (EditText) findViewById(R.id.mobilenumber);
		mEmailAddressView = (EditText) findViewById(R.id.emailaddress);
		mPasswordView = (EditText) findViewById(R.id.password);
		mRegisterFormView = findViewById(R.id.register_form);
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);
		
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
	    	startActivity(new Intent(RegistrationFormActivity.this,MainActivity.class));
	    	finish();
	    	return true; 
	    	
		case R.id.menuNext:
			Log.v(TAG, "do actual registration here");
			attemptRegister();
			//startActivity(new Intent(RegistrationFormActivity.this,RegisterFamilyActivity.class));
			//finish();
			break;

		default:
			 return super.onOptionsItemSelected(item);
		}

		return true;
	}



	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		// Reset errors.
		mFirstNameView.setError(null);
		mLastNameView.setError(null);
		mMobileNumberView.setError(null);
		mEmailAddressView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the registration attempt.
		mFirstName = mFirstNameView.getText().toString();
		mLastName = mLastNameView.getText().toString();
		mMobileNumber = mMobileNumberView.getText().toString();
		mEmailAddress = mEmailAddressView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		//Check for password
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}
		
		//check for email
		if (TextUtils.isEmpty(mEmailAddress)) {
			mEmailAddressView.setError(getString(R.string.error_field_required));
			focusView = mEmailAddressView;
			cancel = true;
		}  else if (!mEmailAddress.contains("@")) {
			mEmailAddressView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailAddressView;
			cancel = true;
		}
		
/*		//check for mobile
		if (TextUtils.isEmpty(mMobileNumber)) {
			mMobileNumberView.setError(getString(R.string.error_field_required));
			focusView = mMobileNumberView;
			cancel = true;
		}

		// Check for lastname.
		if (TextUtils.isEmpty(mLastName)) {
			mLastNameView.setError(getString(R.string.error_field_required));
			focusView = mLastNameView;
			cancel = true;
		} 
		
		// Check for lastname.
		if (TextUtils.isEmpty(mFirstName)) {
			mFirstNameView.setError(getString(R.string.error_field_required));
			focusView = mFirstNameView;
			cancel = true;
		}*/

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			doRegister();
		}
	}
	


	 
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu) {
		 return true;
	 }
	/**
	 * After all input fields are checked and validated, this will be called 
	 * to register the new user.
	 */
	public void doRegister(){
		if(mEmailAddressView.getText().length() == 0 || mPasswordView.getText().length() == 0)
			return;

		Utils.showProgressDialog(RegistrationFormActivity.this, "Registering user...");
		ParseUser user = new ParseUser();
		user.setUsername(mEmailAddress);
		user.setPassword(mPassword);
		user.setEmail(mEmailAddress);
		user.put("firstName", mFirstName);
		user.put("lastName", mLastName);
		user.put("mobile", mMobileNumber);
		//user.put("interests", mInterests);

		user.signUpInBackground(new SignUpCallback() {
			@Override
			public void done(com.parse.ParseException e) {
				if (e == null) {
					Utils.hideProgressDialog();
					mRegisterStatusMessageView.setText(R.string.login_progress_signing_in);
					showProgress(true);

					//let's login so we can add the selected interests and profile picture ( if any )
					ParseUser.logInInBackground(mEmailAddress, mPassword, new LogInCallback(){
						@Override
						public void done(ParseUser loggedInUser, com.parse.ParseException e) {
							showProgress(false);
							if(loggedInUser != null){//successfully logged in        						

								HashMap<String, String> params =  new HashMap<String, String>();
								params.put("email", mEmailAddress);
								params.put("name", mFirstName);
								
								//send confirmation email to user
								ParseCloud.callFunctionInBackground("sendConfirmationEmail", params, new FunctionCallback<String>() {
								  public void done(String result, ParseException e) {
								    if (e == null) {
								      Log.v(getClass().getName(), "Confirmation email sent.");
								    } else{
								      Log.e(getClass().getName(), e.getMessage());	
								    }
								  }
								});
																
								Session session = Session.getInstance();
								session.setSessionId(getApplicationContext(), loggedInUser.getSessionToken());
								session.setUserName(getApplicationContext(), mEmailAddress);
								session.setUserPassword(getApplicationContext(), mPassword);
								startActivity(new Intent(RegistrationFormActivity.this, RegisterFamilyActivity.class));
								finish();
							} else {
								Log.v(TAG, "not successfully logged in");
							}
						}
					});
				} else {
					Utils.hideProgressDialog();
					// Sign up didn't succeed. Look at the ParseException
					// to figure out what went wrong
					switch(e.getCode()){

					case com.parse.ParseException.EMAIL_TAKEN:
						mEmailAddressView.setError(getString(R.string.error_duplicate_email));
						mEmailAddressView.requestFocus();
						break;
					case com.parse.ParseException.INVALID_EMAIL_ADDRESS:
						mEmailAddressView.setError(getString(R.string.error_invalid_email));
						mEmailAddressView.requestFocus();
						break;
					case com.parse.ParseException.EMAIL_MISSING:
						mEmailAddressView.setError(getString(R.string.error_field_required));
						mEmailAddressView.requestFocus();
						break;
					case com.parse.ParseException.PASSWORD_MISSING:
						mPasswordView.setError(getString(R.string.error_field_required));
						mPasswordView.requestFocus();
						break;
/*					case com.parse.ParseException.USERNAME_MISSING:
						mUserNameView.setError(getString(R.string.error_field_required));
						mUserNameView.requestFocus();
						break;*/
/*					case com.parse.ParseException.USERNAME_TAKEN:
						mUserNameView.setError(getString(R.string.error_duplicate_username));
						mUserNameView.requestFocus();
						break;*/
					default:
						Log.v(TAG, "Error in registration:  " + e.toString());
						//mErrorField.setText(e.getLocalizedMessage());
					}
				}
			}
		});
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mRegisterStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mRegisterFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}
