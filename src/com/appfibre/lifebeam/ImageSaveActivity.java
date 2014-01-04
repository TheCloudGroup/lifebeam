/**
 * 
 */
package com.appfibre.lifebeam;

import java.util.ArrayList;
import java.util.List;

import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.LogInCallback;
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        factoryOptions.inJustDecodeBounds = true; BitmapFactory.decodeFile(outputFileUri.getPath(),
                factoryOptions);
        int imageWidth = factoryOptions.outWidth; int imageHeight = factoryOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(imageWidth/width, imageHeight/height);
        // Decode the image file into a Bitmap sized to fill the View
        factoryOptions.inJustDecodeBounds = false; factoryOptions.inSampleSize = scaleFactor; factoryOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(outputFileUri.getPath(),
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
		ab.setDisplayShowTitleEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		
		imgPhoto = (ImageView)findViewById(R.id.imgPhoto); 
		
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			String imgLink = extras.getString("imgLink");
			outputFileUri = Uri.parse(imgLink);
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
		getMenuInflater().inflate(R.menu.menu_registration, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuRegister:
			Toast.makeText(this, "Action Register selected", Toast.LENGTH_SHORT)
			.show();
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

}
