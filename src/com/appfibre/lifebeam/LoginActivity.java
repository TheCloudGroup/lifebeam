package com.appfibre.lifebeam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class LoginActivity extends Activity implements OnClickListener{

	private Dialog progressDialog;
	private static final String TAG = "LoginActivity";
	private ParseUser currentUser;
	
	private String mUsername = "";
	private String mPassword = "";
	private EditText muserNameView;
	private EditText mPasswordView;
	
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		findViewById(R.id.btnRegisterUsingFacebook).setOnClickListener(this);
		findViewById(R.id.btnLogin).setOnClickListener(this);
		findViewById(R.id.txtForgotPassword).setOnClickListener(this);
		
		muserNameView = (EditText) findViewById(R.id.muserNameView);
		mPasswordView = (EditText) findViewById(R.id.mPasswordView);
		
		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		// Check if there is a currently logged in user
				// and they are linked to a Facebook account.
				currentUser = ParseUser.getCurrentUser();
				if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
					// Go to the user info activity
					//showUserDetailsActivity();
					//Toast.makeText(getApplicationContext(), "Straight now to login activity", Toast.LENGTH_SHORT).show();
					//showUserDetailsActivity();
					//Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
					//startActivity(myIntent);
				}
				
				//check if there is a stored session login via normal and not FB
				if (!"".equalsIgnoreCase(Session.getSessionId()) &&
						!"".equalsIgnoreCase(Session.getUserName()) &&
						!"".equalsIgnoreCase(Session.getUserPassword())&&
						(Session.getSessionId() != null &&
						(Session.getUserName() != null &&
						(Session.getUserPassword() != null)))){
					Log.v(TAG, "There is a valid session auto login please");
					Log.v(TAG, "Session.getSessionId() =" + Session.getSessionId());
					Log.v(TAG, "Session.getUserName() =" + Session.getUserName());
					Log.v(TAG, "Session.getUserPassword() =" + Session.getUserPassword());
					attemptLogin(Session.getUserName(), Session.getUserPassword());
				} else {
					//check if there is a stored session login via normal and not FB
					String sessionId = SharedPrefMgr.getString(this, "sessionId");
					String sessionUName = SharedPrefMgr.getString(this, "sessionUName");
					String sessionPWord = SharedPrefMgr.getString(this, "sessionPWord");
					
					if (!"".equalsIgnoreCase(sessionId) &&
							!"".equalsIgnoreCase(sessionUName) &&
							!"".equalsIgnoreCase(sessionPWord)&&
							(sessionId != null &&
							(sessionUName != null &&
							(sessionPWord != null)))){
						Log.v(TAG, "There is a valid session auto login please");
						Log.v(TAG, "sessionId =" + sessionId);
						Log.v(TAG, "sessionUName =" + sessionUName);
						Log.v(TAG, "sessionPWord =" + sessionPWord);
						attemptLogin(sessionUName, sessionPWord);
					} else {
						Log.v(TAG, "no stored session whatsoever so do fresh login please");
					}
				}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRegisterUsingFacebook:
			//Toast.makeText(getApplicationContext(), "Fb clicked", Toast.LENGTH_SHORT).show();
			onFBLoginButtonClicked();
			break;
		case R.id.txtForgotPassword:
			startActivity(new Intent(LoginActivity.this,LostPasswordActivity.class));
			finish();
			break;
		case R.id.btnLogin:
			attemptLogin();
			break;
		default:
			break;
		}
	}
	
	private void onFBLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		final List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_relationships", "user_birthday", "user_location", "email");

		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
					//Toast.makeText(getApplicationContext(), "Uh oh. The user cancelled the Facebook login.", Toast.LENGTH_SHORT).show();
				} else if (user.isNew()) {
					Log.d(TAG, "User signed up and logged in through Facebook!");
					//Toast.makeText(getApplicationContext(), "User signed up and logged in through Facebook!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Now try saving the user111!");
					makeMeRequest(); //putting up the fbProfile object and other objects
					//Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
					//startActivity(myIntent);
				} else {
					Log.d(TAG, "User logged in through Facebook!");
					//Toast.makeText(getApplicationContext(), "User logged in through Facebook!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Now try saving the user222!");
					makeMeRequest(); //displaying what has been extracted
					//Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
					//startActivity(myIntent);
				}
			}
		});
	}
	
	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					// Create a JSON object to hold the profile info
					JSONObject userProfile = new JSONObject();

					try {
						// Populate the JSON object
						if (user.getBirthday() != null) {
							userProfile.put("birthday",
									user.getBirthday());
						}
						if (user.getProperty("email") != null) {
							userProfile.put("email",
									user.getProperty("email"));
						}
						userProfile.put("facebookId", user.getId());
						if (user.getProperty("gender") != null) {
							userProfile.put("gender",
									(String) user.getProperty("gender"));
						}
						userProfile.put("name", user.getName());

						//manually building the pixURL here
						String pixUrl = "https://graph.facebook.com/" + user.getId() +
								"/picture?type=large&return_ssl_resources=1";

						String pixUrl2 = "http://graph.facebook.com/" + user.getId() +
								"/picture";

						userProfile.put("pictureURL", pixUrl);

						// Save the user profile info in a user property
						// and other properties as well
						currentUser = ParseUser.getCurrentUser();
						currentUser.put("fbProfile", userProfile); //object stored
						currentUser.put("name", user.getName()); //string stored

						//now to setup saving of profile picture in FB
						URL img_value = null;
						img_value = new URL(pixUrl);
						try {
							Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
							ParseFile file = new ParseFile("file.jpg", bitmapToByteArray(mIcon1));
							file.saveInBackground();
							currentUser.put("profilePic", file); //image stored as file	
						} catch (Exception e) {
							Log.v(TAG, "Error parsing FB image: " + e.getMessage());
						}

						//save it in background
						currentUser.saveInBackground();

					} catch (JSONException e) {
						Log.d(TAG, "Error parsing returned user data.");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 

				} else if (response.getError() != null) {
					if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
							|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
						Log.d(TAG, "The facebook session was invalidated.");
						logout();
					} else {
						Log.d(TAG, "Some other error: "
								+ response.getError()
								.getErrorMessage());
					}
				}
				
			}
		});
		request.executeAsync();
	}
	
	public static byte[] bitmapToByteArray(Bitmap bmp) {

		/*ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
	    byte[] byteArray = stream.toByteArray();
	    return byteArray;*/

		byte[] data = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			data = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private void logout() {
		// Log the user out
		if (ParseFacebookUtils.getSession() != null) {
			Log.v(TAG, "Now clearing tokens as there is an FB sessions");
			ParseFacebookUtils.getSession().closeAndClearTokenInformation();
		}
		ParseUser.logOut();

		// Go to the login page
		startLoginActivity();
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
	public void attemptLogin() {
		// Reset errors.
		clearErrors();

		// Store values at the time of the login attempt.
		mUsername = muserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(mUsername)) {
			muserNameView.setError(getString(R.string.error_field_required));
			focusView = muserNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);

			//removed softkeyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


			Log.v ("log in","Signing in...");

			// perform the user login attempt.
			ParseUser.logInInBackground(mUsername, mPassword, new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException e) {
					showProgress(false);
					if (e == null) {
						Session.setSessionId(user.getSessionToken());
						Session.setUserName(mUsername);
						Session.setUserPassword(mPassword);
						
						SharedPrefMgr.setString(LoginActivity.this, "sessionId", user.getSessionToken());
						SharedPrefMgr.setString(LoginActivity.this, "sessionUName", mUsername);
						SharedPrefMgr.setString(LoginActivity.this, "sessionPWord", mPassword);
						
						Intent myIntent = new Intent(LoginActivity.this, GalleryActivity.class);
						startActivity(myIntent);
					} else if (user == null){

						switch (e.getCode()) {

						case ParseException.OBJECT_NOT_FOUND:

							showAlertDialog(LoginActivity.this,"Login", "Username or Password is invalid. Try again.", false);
							break;

						case ParseException.PASSWORD_MISSING:
							mPasswordView.setError(getString(R.string.error_field_required));
							mPasswordView.requestFocus();
							break;

						case ParseException.USERNAME_MISSING:
							muserNameView.setError(getString(R.string.error_field_required));
							muserNameView.requestFocus();
							break;

						case ParseException.TIMEOUT: 
							Log.v(TAG, "There was a timeout");
							Toast.makeText(getApplicationContext(), "We currently could not log you in.  Please try again in a few minutes... ", Toast.LENGTH_SHORT).show();
							break;
						default:
							Log.v(TAG, "this error is untrapped");
							Toast.makeText(getApplicationContext(), "We currently could not log you in.  Please try again in a few minutes... ", Toast.LENGTH_SHORT).show();
							break;

						}

					}
				}
			});
		}
	}
	
	public void attemptLogin(String uName, String pWord) {

		// Show a progress spinner, and kick off a background task to
		showProgress(true);

		Log.v ("log in","Signing in...");

		// perform the user login attempt.
		ParseUser.logInInBackground(uName, pWord, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				showProgress(false);
				if (e == null) {
					//Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
					//startActivity(myIntent);
				} else if (user == null){
					switch (e.getCode()) {
					case ParseException.TIMEOUT: 
						Log.v(TAG, "There was a timeout");
						Toast.makeText(getApplicationContext(), "We currently could not log you in.  Please try again in a few minutes... ", Toast.LENGTH_SHORT).show();
						break;
					default:
						Log.v(TAG, "this error is untrapped");
						Toast.makeText(getApplicationContext(), "We currently could not log you in.  Please try again in a few minutes... ", Toast.LENGTH_SHORT).show();
						break;

					}

				}
			}
		});
	}
	
	private void clearErrors() {
		// TODO Auto-generated method stub
		muserNameView.setError(null);
		mPasswordView.setError(null);
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

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		// Setting alert dialog icon
		alertDialog.setIcon(R.drawable.delete);

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
}
