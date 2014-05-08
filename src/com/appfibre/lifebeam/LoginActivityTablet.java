package com.appfibre.lifebeam;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivityTablet extends Activity implements OnClickListener{

	private Dialog progressDialog;
	private static final String TAG = "LoginActivity";
	private ParseUser currentUser;

	private String mFamilyAccount = "";
	private String mPasscode = "";
	private EditText edtFamilyAccount;
	private EditText edtPasscode;

	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private ArrayList<Bitmap> eventBmaps = new ArrayList<Bitmap>();
	private List<String> eventImageUrls = new ArrayList<String>();
    private Session session = Session.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_tablet);

		Log.v(TAG, "Now in OnCreate ==========================================");

		ParseUser.enableAutomaticUser();

		findViewById(R.id.btnLogin).setOnClickListener(this);
		findViewById(R.id.txtForgotPassword).setOnClickListener(this);

		edtFamilyAccount = (EditText) findViewById(R.id.edtFamilyAccount);
		edtPasscode = (EditText) findViewById(R.id.edtPassCode);

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		//check if there is a stored session login via normal and not FB
		/*String familyAccount = session.getUserFamilyAccount(getApplicationContext());
		String passCode = session.getUserPasscode(getApplicationContext());
		
		if ( familyAccount!= null && familyAccount.length() > 0 && 
			 passCode !=null &&  passCode.length() > 0 ) {
			//attemptLogin(Session.getUserFamilyAccount(), Session.getUserPasscode());
			doLogin(familyAccount,passCode);
		}*/		
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
		String familyAccount = session.getUserFamilyAccount(getApplicationContext());
		String passCode = session.getUserPasscode(getApplicationContext());
		
		if ( familyAccount!= null && familyAccount.length() > 0 && 
			 passCode !=null &&  passCode.length() > 0 ) {
			//attemptLogin(Session.getUserFamilyAccount(), Session.getUserPasscode());
			doLogin(familyAccount,passCode);
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
		case R.id.txtForgotPassword:
			startActivity(new Intent(LoginActivityTablet.this,LostPasswordActivity.class));
			finish();
			break;
		case R.id.btnLogin:
			if(isValidLogin()){
				doLogin(mFamilyAccount, mPasscode);
			}
			break;

		default:
			break;
		}
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

	public boolean isValidLogin() {
		// Reset errors.
		clearErrors();

		// Store values at the time of the login attempt.
		mFamilyAccount = edtFamilyAccount.getText().toString();
		mPasscode = edtPasscode.getText().toString();

		boolean isValid = true;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPasscode)) {
			edtPasscode.setError(getString(R.string.error_field_required));
			focusView = edtPasscode;
			isValid = false;
		} else if (mPasscode.length() < 4) {
			edtPasscode.setError(getString(R.string.error_invalid_password));
			focusView = edtPasscode;
			isValid = false;
		}

		// Check for a valid username.
		if (TextUtils.isEmpty(mFamilyAccount)) {
			edtFamilyAccount.setError(getString(R.string.error_field_required));
			focusView = edtFamilyAccount;
			isValid = false;
		}

		if (!isValid) {
			focusView.requestFocus();
		} else {
			//removed softkeyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);			
		}
	    return isValid;
	}

	public void doLogin(final String familyAccountName, final String passcode) {
		// Show a progress spinner, and kick off a background task to
		mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
		showProgress(true);

		Log.v ("log in","Signing in...");

		// query the Events table here with family matchinng

		//final ParseQuery<ParseUser> innerQuery = ParseUser.getQuery();
		//innerQuery.whereEqualTo("objectId", userId);

		ParseQuery<Family> queryFamily = new ParseQuery<Family>("Family");
		queryFamily.whereEqualTo("name", familyAccountName);
		queryFamily.whereEqualTo("passCode", passcode);
		queryFamily.findInBackground(new FindCallback<Family>() {
			public void done(List<Family> Families, ParseException e) {
				showProgress(false);
				if (e == null) {
					if (Families.size() == 0) {
						Toast.makeText(getApplicationContext(),
								"Either your Family Account or Passcode is not correct", Toast.LENGTH_LONG)
								.show();
					} else {
						Log.v(TAG, "just printing contents of events here content = " + Families.get(0).getName());
						String familyObjectId = Families.get(0).getObjectId();
						session.setFamilyObjectId(getApplicationContext(), familyObjectId);
						session.setUserFamilyAccount(getApplicationContext(), familyAccountName);
						session.setUserPasscode(getApplicationContext(), passcode);
						session.setSessionId(getApplicationContext(), ParseUser.getCurrentUser().getSessionToken());
						//extractEvents(familyAccountName);
						Intent myIntent = new Intent(LoginActivityTablet.this, SlideShowActivity2.class);
						//myIntent.putStringArrayListExtra("eventImageUrls", (ArrayList<String>) eventImageUrls);
						startActivity(myIntent);	
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

	private void clearErrors() {
		edtFamilyAccount.setError(null);
		edtPasscode.setError(null);
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
			Toast.makeText(LoginActivityTablet.this, "We have trouble connecting to your facebook account.  Please try again later.", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		View currFocus = getCurrentFocus();
		if(currFocus != null && currFocus.getWindowToken() != null){
		    imm.hideSoftInputFromWindow(currFocus.getWindowToken(), 0);		
		}		
		return true;
	}

	private void extractEvents(String familyAccount) {
		Utils.showProgressDialog(LoginActivityTablet.this, "Extracting events.");
		Log.v(TAG, "how many events for this family ?????????????? " + familyAccount);
		ParseQuery<Event> queryEvent = new ParseQuery<Event>("Event");
		queryEvent.whereEqualTo("family", familyAccount);
		queryEvent.include("author");
		queryEvent.findInBackground(new FindCallback<Event>() {
			public void done(List<Event> Events, ParseException e) {
				Utils.hideProgressDialog();
				ParseObject user = new ParseObject("User");
				if (e == null) {
					
					if (Events.size() == 0) {
						Toast.makeText(getApplicationContext(),
								"There are no events associated with this family account.", Toast.LENGTH_LONG)
								.show();
					} else {
						Log.v(TAG, "hook up a global variable here for the events loading in slideshowactivity");
						LifebeamApp globalVars = ((LifebeamApp)getApplication());
						globalVars.setEvents(Events); 
						Intent myIntent = new Intent(LoginActivityTablet.this, SlideShowActivity.class);
						//myIntent.putStringArrayListExtra("eventImageUrls", (ArrayList<String>) eventImageUrls);
						startActivity(myIntent);	
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
}
