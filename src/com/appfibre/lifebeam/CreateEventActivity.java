package com.appfibre.lifebeam;

import java.io.ByteArrayOutputStream;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.classes.Family;
import com.appfibre.lifebeam.utils.CameraUtils;
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
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateEventActivity extends Activity  implements OnClickListener {

	private ImageView imgPhoto;
	private ImageView imgRotate;
	private ImageView imgDelete;
	private EditText edtPhotoDesc;
	private TextView txtLength;
	private Uri profileImageUri;
	private boolean hasNoImage; 
	private static final int CAPTURE_CAMERA_CODE  = 1337;
	private static final int GALLERY_CODE  = 1338;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagesave);

		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(true);
		ab.setDisplayUseLogoEnabled(false);

		hasNoImage = true;
		imgPhoto = (ImageView)findViewById(R.id.imgPhoto); 
		edtPhotoDesc = (EditText) findViewById(R.id.edtPhotoDesc);
		txtLength = (TextView) findViewById(R.id.txtLength);
		imgRotate = (ImageView)findViewById(R.id.imgRotatePhoto);
		imgDelete = (ImageView)findViewById(R.id.imgDelete);
		findViewById(R.id.imgRotatePhoto).setOnClickListener(this);
		findViewById(R.id.imgDelete).setOnClickListener(this);
		findViewById(R.id.txtForPhotoHolder).setOnClickListener(this);

		final TextWatcher mTextEditorWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
				txtLength.setText(String.valueOf(200 - s.length()));
			}

			@Override
			public void afterTextChanged(Editable s) {}	
	    };

		edtPhotoDesc.addTextChangedListener(mTextEditorWatcher);
		
		if(savedInstanceState != null) {
	        Bitmap bitmap = savedInstanceState.getParcelable("eventImage");
	        
	        if(bitmap != null){
	        	imgPhoto.setImageBitmap(bitmap);
	        	hasNoImage = false;
	        }
	        
	        String sPhotoDesc = savedInstanceState.getString("photoDesc");
		    if(sPhotoDesc != null){
		    	edtPhotoDesc.setText(sPhotoDesc);
		    }
		    
		    Uri imageUri = savedInstanceState.getParcelable("profileImageUri");
		    if(imageUri != null ){
		    	profileImageUri = imageUri;
		    }   
	     }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    BitmapDrawable img = (BitmapDrawable) imgPhoto.getDrawable();
	    Bitmap bitmap = img.getBitmap();
	    
	    if(bitmap != null){
	    	outState.putParcelable("eventImage", bitmap);		    	
	    }
	    
	    String sPhotoDesc = edtPhotoDesc.getText().toString();
	    if(sPhotoDesc != null && edtPhotoDesc.length() > 0){
	    	outState.putString("photoDesc", sPhotoDesc);
	    }
	    
	    if(profileImageUri != null ){
	    	outState.putParcelable("profileImageUri", profileImageUri);
	    }
	    super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_create_event, menu);
		return true;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgRotatePhoto:
			rotateImageView();
			break;
		case R.id.imgDelete:
			if(!hasNoImage){
				imgPhoto.setImageResource(R.drawable.no_image_avail);
				imgRotate.setVisibility(View.GONE);
				imgDelete.setVisibility(View.GONE);
				hasNoImage = true;
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuPost:
			if(hasNoImage){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateEventActivity.this);
			     
				alertDialogBuilder.setTitle("No Image");
				alertDialogBuilder.setMessage("Would you like to add an image to this event?");
				alertDialogBuilder.setIcon(R.drawable.delete);
				
				alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
				    	dialog.cancel();
				    	setEventImage(CAPTURE_CAMERA_CODE);
					}
				});
				
				alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						if(hasNoImage){
						    postEvent(null);
						} else {
							saveImage();
						}
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				
			} else {
				saveImage();
			}
			break;
		case R.id.menuCamera:
			setEventImage(CAPTURE_CAMERA_CODE);
			break;
		case R.id.menuGallery:
			setEventImage(GALLERY_CODE);
			break;
		default:
			break;
		}
		return true;
	}
   
	private void setEventImage(int source){
		Intent pictureIntent = null;
        switch(source){
            case CAPTURE_CAMERA_CODE:
        		profileImageUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_IMAGE, getPackageName());
        		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        		intent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
        		startActivityForResult(intent, CAPTURE_CAMERA_CODE);
                break;
            case GALLERY_CODE:
        		pictureIntent = new Intent();
                pictureIntent.setType("image/*");
                pictureIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pictureIntent,
                        "Select Picture"), GALLERY_CODE);
                break;
        }
	}
	
	private void saveImage(){
		try {
			Utils.showProgressDialog(CreateEventActivity.this, "Saving Photo ...");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    BitmapDrawable img = (BitmapDrawable) imgPhoto.getDrawable();
		    Bitmap bitmap = img.getBitmap();
		    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

            final ParseFile file = new ParseFile( "file.jpg", byteArray);
			file.saveInBackground(new SaveCallback() {

				@Override
				public void done(com.parse.ParseException e) {
					Utils.hideProgressDialog();
					if (e == null) {
						postEvent(file);
					} else {
						Log.e(getClass().getName(), "Error saving image: " + e.getMessage());
					}
				}

			});

		} catch (Exception e) {
			Log.e(getClass().getName(), "Error: " + e.getMessage());
		}
	}
	
	private void postEvent(ParseFile file){
		Log.v(getClass().getName(), "reconfirm that there is an associated family for this user");
		String strFamily = ParseUser.getCurrentUser().getString("family");
		if (strFamily == null) {
			Toast.makeText(CreateEventActivity.this, "It is required that you be associated with a " +
					" Family Account before you can share events.  Please set the same in your" +
					" Settings", Toast.LENGTH_SHORT).show();
		} else {
			Utils.showProgressDialog(CreateEventActivity.this, "Posting Event... ");
			ParseACL eventACL = new ParseACL(ParseUser.getCurrentUser());
			eventACL.setPublicReadAccess(true);
			eventACL.setPublicWriteAccess(true); //This is to accomodate splendid/razzledazzle/smiley updates for anonymous users in Tablet Version

			final Event event = new Event();
			event.setACL(eventACL);
			event.setContent(edtPhotoDesc.getText().toString());
			
			if(file != null){
				event.setImage(file);
			}
			
			event.setAuthor(ParseUser.getCurrentUser());
			event.setFamily(strFamily);

			event.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					Utils.hideProgressDialog();	
					if (e == null) {
						Log.v(getClass().getName(), "now add this event to the user ");
						ParseUser user = ParseUser.getCurrentUser();
						ParseRelation<ParseObject> events = user.getRelation("events");
						events.add(event);
						user.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								Utils.hideProgressDialog();
								if (e == null) {
									addEventToFamily(event);	
									setResult(RESULT_OK);
									finish();
								} else {
									Toast.makeText(CreateEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
									Log.v(getClass().getName(), "Error saving new fart to current user : " + e.toString());
								}
							}

						});	
					} else {
						Log.v(getClass().getName(), "Error saving new fart : " + e.toString());
					}
				}
			});
		}			
	}
	
	private void addEventToFamily(Event event) {
		Family family = new Family();
		ParseRelation<ParseObject> relation = family.getRelation("events");
		relation.add(event);
		family.saveInBackground();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		String imagePath = null;

		if(resultCode == RESULT_OK){
			switch(requestCode) {
				case GALLERY_CODE:
					Uri selectedImageUri = data.getData();
					imagePath = CameraUtils.getPath(CreateEventActivity.this,selectedImageUri);
					profileImageUri = Uri.parse(imagePath);
				case CAPTURE_CAMERA_CODE:
					if (profileImageUri != null) {
						hasNoImage = !CameraUtils.setImageToView(imgPhoto, profileImageUri.getPath());			
						if(!hasNoImage){
							imgRotate.setVisibility(View.VISIBLE);
							imgDelete.setVisibility(View.VISIBLE);
						} else {
							imgRotate.setVisibility(View.GONE);
							imgDelete.setVisibility(View.GONE);
						}	
					} else {
						hasNoImage = true;
						imgRotate.setVisibility(View.GONE);
						imgDelete.setVisibility(View.GONE);
					}
					break;
				default:
					break;
			}
		}
	}

	private void rotateImageView() {
	    BitmapDrawable img = (BitmapDrawable) imgPhoto.getDrawable();
	    if(img != null && img.getBitmap() != null){
			Matrix matrix = new Matrix();
			matrix.postRotate(90);

		    Bitmap bitmap = img.getBitmap();
			Bitmap bMapRotate = Bitmap.createBitmap(bitmap, 0, 0, 
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);

			imgPhoto.setImageBitmap(bMapRotate);
	    }
	}

}
