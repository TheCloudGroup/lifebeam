/**
 * 
 */
package com.appfibre.lifebeam;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.appfibre.lifebeam.classes.Event;
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author REBUCAS RENANTE
 *
 */
public class ImageSaveActivity extends Activity {

	private ImageView imgPhoto;
	private EditText edtPhotoDesc;
	private TextView txtLength;
	private Event event;
	private ParseFile file;
	
	private static final int CAPTURE_CAMERA_CODE  = 1337;
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
		factoryOptions.inJustDecodeBounds = true; BitmapFactory.decodeFile(profileImageUri.getPath(),
				factoryOptions);
		int imageWidth = factoryOptions.outWidth; int imageHeight = factoryOptions.outHeight;
		// Determine how much to scale down the image
		int scaleFactor = Math.min(imageWidth/width, imageHeight/height);
		// Decode the image file into a Bitmap sized to fill the View
		factoryOptions.inJustDecodeBounds = false; factoryOptions.inSampleSize = scaleFactor; factoryOptions.inPurgeable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(profileImageUri.getPath(),
				factoryOptions);
		imgPhoto.setImageBitmap(bitmap);
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
		
		captureImage();
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
			saveToParse();
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

/*	private void saveFile() {
		Utils.showProgressDialog(ImageSaveActivity.this, "Saving Image...");
		File f = new File(outputFileUri.getPath());
		byte[] reader = null;
		try {
			reader = FileUtils.readFileToByteArray(f);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Log.v(TAG, "reader byte array size is = " + reader.length);
		file = new ParseFile("eventImage", reader);
		
		file.saveInBackground((new SaveCallback() {

			@Override
			public void done(ParseException e) {
				Utils.hideProgressDialog();	
				// TODO Auto-generated method stub
				if (e == null) {
					Log.v(TAG, "audiofile save as parsefile proceeding to savetoparse now");
					saveToParse();
				} else {
					Log.v(TAG, "Error saving soundfile: " + e.toString());
					Toast.makeText(ImageSaveActivity.this, "Error saving image. Please try again later.", Toast.LENGTH_LONG).show();
				}
			}
		}));
	}*/
	
	private void saveToParse() {
		Utils.showProgressDialog(ImageSaveActivity.this, "Saving To Parse Now...");
		ParseACL eventACL = new ParseACL(ParseUser.getCurrentUser());
		eventACL.setPublicReadAccess(true);

		event = new Event();
		event.setACL(eventACL);
		event.setContent(edtPhotoDesc.getText().toString());
		event.setImage(file);
		event.setAuthor(ParseUser.getCurrentUser());
		
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
								Log.v(TAG, "Now we call library after saving and display all saved farts");
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult=====>>>>>>>>>>");
		Log.v(TAG, "requestCode = " + requestCode);
		Log.v(TAG, "resultCode = " + resultCode);
		
		String strProfileImageUri = SharedPrefMgr.getString(getApplicationContext(), "profileImageUri");
		profileImageUri = Uri.parse(strProfileImageUri);
		
		Log.v(TAG, "onActivityResult profileImageUri = " +  profileImageUri);

		if (requestCode == ImageSaveActivity.CAPTURE_CAMERA_CODE) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(ImageSaveActivity.this, "Image Confirmed!", Toast.LENGTH_SHORT).show();
				if (profileImageUri != null) {
					try {
						file = new ParseFile( "file.jpg", CameraUtils.convertUriToBytes(ImageSaveActivity.this, profileImageUri));
						file.saveInBackground(new SaveCallback() {

							@Override
							public void done(com.parse.ParseException e) {
								if (e == null) {
									Log.v(TAG, "now ready to save event huzzah!!!!");
									//saveToParse();
								} else {
									Log.v(TAG, "Error saving user with new group : " + e.toString());
								}
							}

						});
						
					} catch (Exception e) {
						Log.e(TAG, "Error: " + e.getMessage());
					}
						
				}
				Log.v(TAG, "profileImageUri = " + profileImageUri);
			} else {
				profileImageUri = null;
			}
		}
	}
}