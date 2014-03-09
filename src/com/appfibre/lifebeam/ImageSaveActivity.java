/**
 * 
 */
package com.appfibre.lifebeam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.utils.CameraUtils;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author REBUCAS RENANTE
 *
 */
public class ImageSaveActivity extends Activity  implements OnClickListener{

	private ImageView imgPhoto;
	private EditText edtPhotoDesc;
	private TextView txtLength;
	private Event event;
	private ParseFile file;
	private Bitmap origbitmap, origbitmapResized;
	private boolean hasNoImage;
	private String strFamily;
	
	private boolean isCreateNewAccount;
	private TextView txtFamilyLabel;
	private EditText edtFamilyName;
	private EditText edtPassCode;

	private static final int CAPTURE_CAMERA_CODE  = 1337;
	private static final int GALLERY_CODE  = 1338;
	private Uri profileImageUri = null;

	/* (non-Javadoc)
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		Log.v(TAG, "now in onWindowFocusChanged here ===================");
		int width = imgPhoto.getWidth();
		int height = imgPhoto.getHeight();
		BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
		if (profileImageUri != null) {
			factoryOptions.inJustDecodeBounds = true; BitmapFactory.decodeFile(profileImageUri.getPath(),
					factoryOptions);
			int imageWidth = factoryOptions.outWidth; int imageHeight = factoryOptions.outHeight;
			// Determine how much to scale down the image
			int scaleFactor = Math.min(imageWidth/width, imageHeight/height);
			// Decode the image file into a Bitmap sized to fill the View
			factoryOptions.inJustDecodeBounds = false; factoryOptions.inSampleSize = scaleFactor; factoryOptions.inPurgeable = true;
			Bitmap bitmap = BitmapFactory.decodeFile(profileImageUri.getPath(),
					factoryOptions);
			origbitmap = BitmapFactory.decodeFile(profileImageUri.getPath(),
					factoryOptions);
			imgPhoto.setImageBitmap(bitmap);
			hasNoImage = false;
			findViewById(R.id.imgRotatePhoto).setVisibility(View.VISIBLE);
		} else {
			hasNoImage = true;
			findViewById(R.id.imgRotatePhoto).setVisibility(View.GONE);
		}
	}

	Uri outputFileUri;

	private String TAG = "ImageSaveActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagesave);

		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(true);
		ab.setDisplayUseLogoEnabled(false);

		imgPhoto = (ImageView)findViewById(R.id.imgPhoto); 

		//Bundle extras = getIntent().getExtras();

		/*		if (extras != null) {
			String imgLink = extras.getString("imgLink");
			outputFileUri = Uri.parse(imgLink);
		}*/

		edtPhotoDesc = (EditText) findViewById(R.id.edtPhotoDesc);
		txtLength = (TextView) findViewById(R.id.txtLength);

		findViewById(R.id.imgRotatePhoto).setOnClickListener(this);
		findViewById(R.id.txtForPhotoHolder).setOnClickListener(this);

		final TextWatcher mTextEditorWatcher = new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				txtLength.setText(String.valueOf(200 - s.length()));
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		};

		edtPhotoDesc.addTextChangedListener(mTextEditorWatcher);
		Intent i = getIntent();
        boolean isGallery = i.getBooleanExtra("isGallery", false);
        if(!isGallery){
        	captureImage();
        } else {
        	getGalleryImage();
        }
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.v(TAG, "now in onStart method here =================");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_imagesave, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuPost:
			Log.v(TAG, "Posts yeah");
			if (hasNoImage) {
				Toast.makeText(ImageSaveActivity.this, "You need to take an image to proceed with posting your event.", Toast.LENGTH_LONG).show();
				finish();
				return true;
			}
			//saveToParse();
			saveApprovedRotatedImage();
			break;
		case R.id.menuNext:
			/*Log.v(TAG, "do actual registration here");
			attemptRegister();
			startActivity(new Intent(RegistrationFormActivity.this,RegisterFamilyActivity.class));
			finish();*/
			break;

		default:
			break;
		}

		return true;
	}

	private void saveToParse() {
		Log.v(TAG, "reconfirm that there is an associated family for this user");
		strFamily = ParseUser.getCurrentUser().getString("family");
		if (strFamily == null) {
			Toast.makeText(ImageSaveActivity.this, "It is required that you be associated with a " +
					" Family Account before you can share events.  Please set the same in your" +
					" Settings", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Utils.showProgressDialog(ImageSaveActivity.this, "Posting Event... ");
		ParseACL eventACL = new ParseACL(ParseUser.getCurrentUser());
		eventACL.setPublicReadAccess(true);
		eventACL.setPublicWriteAccess(true); //This is to accomodate splendid/razzledazzle/smiley updates for anonymous users in Tablet Version

		event = new Event();
		event.setACL(eventACL);
		event.setContent(edtPhotoDesc.getText().toString());
		event.setImage(file);
		event.setAuthor(ParseUser.getCurrentUser());
		event.setFamily(strFamily);

		event.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				Utils.hideProgressDialog();	
				if (e == null) {
					Log.v(TAG, "now add this event to the user ");
					ParseUser user = ParseUser.getCurrentUser();
					ParseRelation<ParseObject> events = user.getRelation("events");
					events.add(event);
					user.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							Utils.hideProgressDialog();
							if (e == null) {
								Log.v(TAG, "successfully added event here");
								Log.v(TAG, "now add this event to a family account");
								addEventToFamily();
								Bundle bundle = new Bundle();
								bundle.putString("parentActivity", TAG); 
								Intent myIntent = new Intent(ImageSaveActivity.this, GalleryActivity.class);
								myIntent.putExtras(bundle);
								startActivity(myIntent);
								finish();
							} else {
								Log.v(TAG, "Error saving new fart to current user : " + e.toString());
							}
						}

					});	
				} else {
					Log.v(TAG, "Error saving new fart : " + e.toString());
				}
			}
		});
	}

	private void captureImage(){
		Log.v(TAG, "Now creating output file directory for image captured");
		profileImageUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_IMAGE, getPackageName());

		SharedPrefMgr.setString(getApplicationContext(), "profileImageUri", profileImageUri.toString());

		Log.v(TAG, "profileImageUri = " +  profileImageUri);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, ImageSaveActivity.CAPTURE_CAMERA_CODE);
	}
	
	private void getGalleryImage(){
		Log.v(TAG, "Now creating output file directory for image captured");
		profileImageUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_IMAGE, getPackageName());

		SharedPrefMgr.setString(getApplicationContext(), "profileImageUri", profileImageUri.toString());

		Log.v(TAG, "profileImageUri = " +  profileImageUri);
		Intent pictureIntent = new Intent();
        pictureIntent.setType("image/*");
        pictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pictureIntent,
                "Select Picture"), ImageSaveActivity.GALLERY_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		Log.v(TAG, "onActivityResult profileImageUri = " +  profileImageUri);
		profileImageUri = null;
		if(resultCode == RESULT_OK){
			switch(requestCode) {
				case ImageSaveActivity.CAPTURE_CAMERA_CODE:
					String strProfileImageUri = SharedPrefMgr.getString(getApplicationContext(), "profileImageUri");
					profileImageUri = Uri.parse(strProfileImageUri);
					break;
				case ImageSaveActivity.GALLERY_CODE:
					Uri selectedImageUri = data.getData();
					String photoPath = CameraUtils.getPath(ImageSaveActivity.this,selectedImageUri);
					profileImageUri = Uri.parse(photoPath);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtForPhotoHolder:
			Log.v(TAG, "yes you clicked the txtphotoholder");
			edtTextHasFocus();
			break;

		case R.id.imgRotatePhoto:
			rotateImageView();
			break;

		default:
			Log.e("In ImageSaveActivity onClick method", "unimplemented click listener");
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	private void edtTextHasFocus() {
		edtPhotoDesc.requestFocusFromTouch();
		InputMethodManager lManager = (InputMethodManager)ImageSaveActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE); 
		lManager.showSoftInput(edtPhotoDesc, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
	}

	private void rotateImageView() {
		//Create object of new Matrix.
		Matrix matrix = new Matrix();

		//set image rotation value to 90 degrees in matrix.
		matrix.postRotate(90);

		//Create bitmap with new values.
		Bitmap bMapRotate = Bitmap.createBitmap(origbitmap, 0, 0, 
				origbitmap.getWidth(), origbitmap.getHeight(), matrix, true);

		origbitmap = bMapRotate;

		//put rotated image in ImageView.
		imgPhoto.setImageBitmap(bMapRotate);
	}

	private void saveApprovedRotatedImage() {
		Log.v(TAG, "scale image first");
		scaleImage();

		try {
			Utils.showProgressDialog(ImageSaveActivity.this, "Saving Photo ...");
			Log.v(TAG, "convert bmp to byte array");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			origbitmapResized.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			file = new ParseFile( "file.jpg", byteArray);
			file.saveInBackground(new SaveCallback() {

				@Override
				public void done(com.parse.ParseException e) {
					Utils.hideProgressDialog();
					if (e == null) {
						Log.v(TAG, "now ready to save event huzzah!!!!");
						saveToParse();
					} else {
						Log.v(TAG, "Error saving user with new group : " + e.toString());
					}
				}

			});

		} catch (Exception e) {
			Log.e(TAG, "Error: " + e.getMessage());
		}


	}

	private void scaleImage() {
		Log.v(TAG, "original height = " + origbitmap.getHeight());
		Log.v(TAG, "original width = " + origbitmap.getWidth());

		int REQUIRED_SIZE = 150; //either its width or height should be within twice of this
		int width_tmp=origbitmap.getWidth(), height_tmp=origbitmap.getHeight();
		int scale=1;
		while(true){
			if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
				break;
			width_tmp/=2;
			height_tmp/=2;
			scale*=2;
		}

		Log.v(TAG, "height or width should not be less than 300 here unless it originally was");
		Log.v(TAG, "new reduced height = " + height_tmp);
		Log.v(TAG, "new reduced width = " + width_tmp);
		origbitmapResized = Bitmap.createBitmap(origbitmap);

//		origbitmapResized = Bitmap.createScaledBitmap(origbitmap, width_tmp, height_tmp, true);

	}

	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);                
		Canvas c = new Canvas(b);
		v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
		v.draw(c);
		return b;
	}

	private void addEventToFamily() {
		Family family = new Family();
		ParseRelation<ParseObject> relation = family.getRelation("events");
		relation.add(event);
		family.saveInBackground();
	}

	public String attemptToGetFamily() {
		String familyAccount = " ";
		showDialog();
		return familyAccount; 
	
	}
	
	public void showDialog() {
		LayoutInflater li = LayoutInflater.from(ImageSaveActivity.this);
		View promptsView = li.inflate(R.layout.dialog_create_family, null);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ImageSaveActivity.this);
		alertDialogBuilder.setView(promptsView);

		
		edtFamilyName = (EditText) promptsView
				.findViewById(R.id.edtFamilyName);
		edtPassCode = (EditText) promptsView
				.findViewById(R.id.edtPassCode);
		txtFamilyLabel = (TextView) promptsView
				.findViewById(R.id.txtFamilyLabel);
		final Switch switchFamily = (Switch) promptsView
				.findViewById(R.id.switchFamily);
		
		switchFamily.setChecked(false);
		switchFamily.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					txtFamilyLabel.setText("Family Account to Create");
					isCreateNewAccount = true;
				}else{
					txtFamilyLabel.setText("Family Account to Join");
					isCreateNewAccount = false;
				}

			}
		});


		// set dialog message
		alertDialogBuilder
		.setCancelable(false)
		.setNegativeButton("Go",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				/** DO THE METHOD HERE WHEN PROCEED IS CLICKED*//*
				String user_text = (userInput.getText()).toString();

				*//** CHECK FOR USER'S INPUT **//*
				if (user_text.equals("oeg"))
				{
					Log.d(user_text, "HELLO THIS IS THE MESSAGE CAUGHT :)");
					Search_Tips(user_text); 

				}
				else{
					Log.d(user_text,"string is empty");
					String message = "The password you have entered is incorrect." + " \n \n" + "Please try again!";
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Error");
					builder.setMessage(message);
					builder.setPositiveButton("Cancel", null);
					builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							showDialog();
						}
					});
					builder.create().show();

				}*/
			}
		})
		.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.dismiss();
			}

		}

				);

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}
}
