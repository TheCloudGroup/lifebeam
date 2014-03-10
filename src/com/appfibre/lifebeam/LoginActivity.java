package com.appfibre.lifebeam;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class LoginActivity extends Activity implements OnClickListener{

	private Dialog progressDialog;
	private static final String TAG = "LoginActivity";
	private ParseUser currentUser;

	private EditText email;

	private String mUsername = "";
	private String mPassword = "";
	private String mEmail = "";
	private EditText muserNameView;
	private EditText mPasswordView;

	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private CheckBox checkBoxKeepLoggedIn;
	private boolean isTablet;
    private Session session = Session.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		isTablet = getResources().getBoolean(R.bool.isTablet);
		Log.v(TAG, "Now in OnCreate ==========================================");

		findViewById(R.id.btnRegisterUsingFacebook).setOnClickListener(this);
		findViewById(R.id.btnLogin).setOnClickListener(this);
		findViewById(R.id.txtForgotPassword).setOnClickListener(this);
		checkBoxKeepLoggedIn = (CheckBox) findViewById(R.id.checkBoxKeepLoggedIn);		
		checkBoxKeepLoggedIn.setChecked(SharedPrefMgr.getBool(this, "hasSetKeptLogin"));
		checkBoxKeepLoggedIn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					SharedPrefMgr.setBool(LoginActivity.this, "hasSetKeptLogin", true);
				}else{
					SharedPrefMgr.setBool(LoginActivity.this, "hasSetKeptLogin", false);
				}
			}
		});
		

		


		muserNameView = (EditText) findViewById(R.id.muserNameView);
		mPasswordView = (EditText) findViewById(R.id.mPasswordView);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser) &&
				SharedPrefMgr.getBool(LoginActivity.this, "hasSetKeptLogin")) {
			Log.v(TAG, "reconfirm that there is an associated family for this user");
			String strFamily = ParseUser.getCurrentUser().getString("family");
			if (strFamily == null) {
				attemptToAssociateFamily();	
			} else {
				startActivity(new Intent(LoginActivity.this, GalleryActivity.class));
				finish();
			}
		}

		//check if there is a stored session login via normal and not FB
		if ( session.getSessionId(getApplicationContext()) != null  && SharedPrefMgr.getBool(LoginActivity.this, "hasSetKeptLogin")) {
			doLogin(session.getUserName(getApplicationContext()), session.getUserPassword(getApplicationContext()));
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
			Log.v(TAG, "reconfirm that there is an associated family for this user");
			String strFamily = ParseUser.getCurrentUser().getString("family");
			if (strFamily == null) {
				attemptToAssociateFamily();	
			} else {
				onFBLoginButtonClicked();
			}
			break;
		case R.id.txtForgotPassword:
			showMenuAlertDialog(LoginActivity.this,"Change Password", false);
			break;
		case R.id.btnLogin:
			if(isValidForLogin()){
				doLogin(mUsername, mPassword);
			}
			break;

		default:
			break;
		}
	}

	private void onFBLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		final List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"email");

		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(TAG, "User signed up and logged in through Facebook!");
					Log.d(TAG, "Now try saving the user111!");
					makeMeRequest(); //putting up the fbProfile object and other objects
					Log.v(TAG, "reconfirm that there is an associated family for this user");
					String strFamily = ParseUser.getCurrentUser().getString("family");
					if (strFamily == null) {
						attemptToAssociateFamily();	
					} else {
						startActivity(new Intent(LoginActivity.this, GalleryActivity.class));
						finish();
					}
					finish();
				} else {
					Log.d(TAG, "User logged in through Facebook!");
					//Toast.makeText(getApplicationContext(), "User logged in through Facebook!", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "Now try saving the user222!");
					makeMeRequest(); //displaying what has been extracted
					Log.v(TAG, "reconfirm that there is an associated family for this user");
					String strFamily = ParseUser.getCurrentUser().getString("family");
					if (strFamily == null) {
						attemptToAssociateFamily();	
					} else {
						startActivity(new Intent(LoginActivity.this, GalleryActivity.class));
						finish();
					}
					finish();
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
					currentUser = ParseUser.getCurrentUser();
					try {
						if (user.getProperty("email") != null) {
							currentUser.put("username", user.getProperty("email")); 
							currentUser.put("email", user.getProperty("email")); 
						}
						currentUser.put("name", user.getName()); //string stored
						currentUser.saveInBackground();

					} catch (Exception e) {
						Log.d(TAG, "Error parsing returned user data.  " + e.toString());
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
		session.reset(getApplicationContext());
		startLoginActivity();
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public void doLogin(final String username, final String password) {
		// Show a progress spinner, and kick off a background task to
		mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
		showProgress(true);
		
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				showProgress(false);
				if (e == null) {
					session.setSessionId(getApplicationContext(), user.getSessionToken());
					session.setUserName(getApplicationContext(), username);
					session.setUserPassword(getApplicationContext(),password);
					Log.v(TAG, "reconfirm that there is an associated family for this user");
					String strFamily = ParseUser.getCurrentUser().getString("family");
					if (strFamily == null) {
						attemptToAssociateFamily();	
					} else {
						startActivity(new Intent(LoginActivity.this, GalleryActivity.class));
						finish();
					}
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
	
	public boolean isValidForLogin() {
		// Reset errors.
		clearErrors();

		// Store values at the time of the login attempt.
		mUsername = muserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean valid = true;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			valid = false;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			valid = false;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(mUsername)) {
			muserNameView.setError(getString(R.string.error_field_required));
			focusView = muserNameView;
			valid = false;
		}

		if (!valid) {
			focusView.requestFocus();
		} else {
			//removed softkeyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		}
		
		return valid;
	}

	private void clearErrors() {
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "onActivityResult=============================================");
		try {
			Log.v(TAG, "onActivityResult -- Now calling parsefacebookutils.finishautentication=============================================");
			ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);	
		} catch (Exception e) {
			Log.e(TAG, "Error: " + e.getMessage());
			Toast.makeText(LoginActivity.this, "We have trouble connecting to your facebook account.  Please try again later.", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
	}

	private void forgotPassword() {
		mEmail = email.getText().toString();
		showProgress(true);
		ParseUser.requestPasswordResetInBackground(mEmail, new RequestPasswordResetCallback() {
			public void done(ParseException e) {
				showProgress(false);
				if (e == null) {
					requestedSuccessfully();
				} else {
					requestDidNotSucceed();
				}
			}

		});
	}

	protected void requestDidNotSucceed() {
		Toast toast =Toast.makeText(this, "An error has occured. Please try again.", Toast.LENGTH_LONG);
		toast.show();
	}

	protected void requestedSuccessfully() {
		Toast toast =Toast.makeText(this, "A password reset email has been sent.", Toast.LENGTH_LONG);
		toast.show();
	}

	public void showMenuAlertDialog(Context context, String title, Boolean status) {	
		// get prompts.xml view

		LayoutInflater layoutInflater = LayoutInflater.from(context);

		View promptView = layoutInflater.inflate(R.layout.dialog_forgot_password, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set prompts.xml to be the layout file of the alertdialog builder
		alertDialogBuilder.setView(promptView);
		alertDialogBuilder.setTitle(title);
		// Setting alert dialog icon
		alertDialogBuilder.setIcon(R.drawable.password);
		email = (EditText) promptView.findViewById(R.id.etEmail);

		// setup a dialog window
		alertDialogBuilder
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// get user input and set it to result
				forgotPassword();
			}
		})
		.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		// create an alert dialog
		AlertDialog alertD = alertDialogBuilder.create();

		alertD.show();
	}

	private void attemptToAssociateFamily() {
		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);

		dlgAlert.setMessage("To start sharing events you need to be associated to a " +
				" Family Account.  \n\nPlease join or create your own Family Account.");
		dlgAlert.setTitle("No Associated Family Account");
		dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				startActivity(new Intent(LoginActivity.this, AssociateFamilyActivity.class));
			}});
		dlgAlert.setCancelable(true);
		dlgAlert.create().show();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkBoxKeepLoggedIn.setChecked(SharedPrefMgr.getBool(this, "hasSetKeptLogin"));
		
	}
}
