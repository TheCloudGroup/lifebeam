/**
 * 
 */
package com.appfibre.lifebeam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.CameraUtils;
import com.appfibre.lifebeam.utils.ImageLoader2;
import com.appfibre.lifebeam.utils.MyContactItem3;
import com.appfibre.lifebeam.utils.MyImageItem;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * @author Angel Abellanosa Jr
 *
 */
public class GalleryActivity extends Activity {

	private static final int CAPTURE_CAMERA_CODE  = 1337;
	private Uri profileImageUri = null;

	private String TAG = "GalleryActivity";
	private ArrayList<MyImageItem> Images;
	private ArrayList<Event> EventS;

	private ParseFile file;
	private ParseUser currentUser;
	private ParseRelation<ParseObject> myEvents;
	private List<ParseObject> ScratchMyEvents;
	private int selectedIndex;
	private ListView mainListView;
	
	private String eventIdToDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		//ab.setDisplayUseLogoEnabled(false);


/*		Images = new ArrayList<MyImageItem>();

		Log.v(TAG, "hardcoding data initially");
		String URL = "http://cdn01.cdnwp.celebuzz.com/kourtney-kardashian/wp-content/blogs.dir/313/files/2012/11/27/Kourtney-Kardashian-Family-Beach-Day-Mason-Penelope-Scott-Disick-001.jpg";
		String Id = "123";
		String family = "Rebucas";
		String owner = "Renante Rebucas";
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
		String date = dfDate.format(today);
		String time = dfTime.format(today);
		String message = "Family Time at the Beach";
		int messageCount = 3;
		int likedCount = 2;

		MyImageItem image = new MyImageItem(Id, URL, message, owner, family, date, time,
				messageCount, likedCount);
		Images.add(image);

		URL = "http://4.bp.blogspot.com/-KrTorCPO8oQ/Th5CwulB8AI/AAAAAAAAFsE/ZnpskzXkNXw/s1600/Family%2BBeach_First%2BTrip%2Bto%2BOcean%2B2.jpg";
		Id = "123";
		family = "Rebucas";
		owner = "Renante Rebucas";
		date = dfDate.format(today);
		time = dfTime.format(today);
		message = "Family Time at the Beach";
		messageCount = 3;
		likedCount = 2;

		image = new MyImageItem(Id, URL, message, owner, family, date, time,
				messageCount, likedCount);
		Images.add(image);


		Log.v(TAG, "size of Images arraylist = " + Images.size());*/

		///////////////////--------------------- now softcoded?? hehe

		EventS = new ArrayList<Event>();
		currentUser = ParseUser.getCurrentUser();
		String userId = "";
		if (currentUser.getObjectId() != null &&  !"".equals(currentUser.getObjectId())) {
			userId = currentUser.getObjectId();
		}

		Log.v(TAG, "and the userid is = " + userId);


		final ParseQuery<ParseUser> innerQuery = ParseUser.getQuery();
		innerQuery.whereEqualTo("objectId", userId);

		// Show a progress spinner, and kick off a background task to
		Utils.showProgressDialog(this, "Loading...");

		ParseQuery<Event> queryEvents = new ParseQuery<Event>("Event");
		queryEvents.whereMatchesQuery("author", innerQuery);
		queryEvents.orderByAscending("createdAt");
		queryEvents.include("author");
		queryEvents.findInBackground(new FindCallback<Event>() {
			public void done(List<Event> Events, ParseException e) {
				Utils.hideProgressDialog();
				if (e == null) {
					for (Event event : Events) {
						Log.v(TAG, "just printing contents of events here content = " + event.getContent());
						EventS.add(event);
					}
					loadEventsInListView();
				} else {
					Toast.makeText(getApplicationContext(),
							"Error: " + e.getMessage(), Toast.LENGTH_LONG)
							.show();
					Log.v(TAG, "Error: " + e.getMessage());
				}
			}
		});	

		/*// Find the ListView resource.
		ListView mainListView = (ListView) findViewById(R.id.listImages);
		//((TextView) findViewById(R.id.lstInvitesEmpty)).setText("You currently have no recorded farts yet.");

		// Set our custom array adapter as the ListView's adapter.
		MyImageAdapter adapter = new MyImageAdapter(GalleryActivity.this, Images);
		mainListView.setAdapter(adapter);*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_gallery, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menuCamera:
			Log.v(TAG, "selected camera...");
			startActivity(new Intent(GalleryActivity.this, ImageSaveActivity.class));
			//captureImage();
			break;

		case R.id.menuGallery:
			Log.v(TAG, "selected gallery...");
			Toast.makeText(this, "Menu Gallery selected", Toast.LENGTH_SHORT).show();
			break;	

		case R.id.menuRefresh:
			Log.v(TAG, "selected refresh...");
			Toast.makeText(this, "Menu refresh selected", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menuInvite:
			//Log.v(TAG, "selected invite...");
			//Toast.makeText(this, "Menu Invite selected", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(GalleryActivity.this, InviteActivity.class));
			break;

		case R.id.menuSettings:
			Log.v(TAG, "selected settings...");
			startActivity(new Intent(GalleryActivity.this,SettingsActivity.class));
			break;

		case R.id.menuHelp:
			Log.v(TAG, "selected help...");
			Toast.makeText(this, "Menu Help selected", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menuSignout:
			Log.v(TAG, "clicked logout...");
			if (ParseFacebookUtils.getSession() != null) {
				Log.v(TAG, "Now clearing tokens as there is an FB sessions");
				ParseFacebookUtils.getSession().closeAndClearTokenInformation();
			}
			// Log the user out
			ParseUser.logOut();
			
			Session.reset();
			SharedPrefMgr.setBool(GalleryActivity.this, "hasSetKeptLogin", false);

			// Go to the login view
			startActivity(new Intent(GalleryActivity.this, MainActivity.class));
			finish();
			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return true;
	}

	public class MyImageAdapter extends BaseAdapter {

		private Context context;
		private List<MyImageItem> images;
		private MyImageItem image;
		public ImageLoader2 imageLoader;
		private String TAG = "MyImageAdapter";

		public MyImageAdapter(Context context, ArrayList<MyImageItem> images) {
			this.context = context;
			this.images = images;
		}

		/*private view holder class*/
		private class ViewHolder {
			ImageView imgPix;
			ImageView imgClose;
			TextView txtOwner;
			TextView txtDate;
			TextView txtTime;
			TextView txtMessage;
			TextView txtMessageCount;
			TextView txtLikedCount;
			

		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return images.get(position);
		}

		@Override
		public long getItemId(int position) {
			return images.indexOf(getItem(position));
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader2(context);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.gallery_list_item, null);
				holder = new ViewHolder();
				holder.imgPix = (ImageView) convertView.findViewById(R.id.imgPix);
				holder.imgClose = (ImageView) convertView.findViewById(R.id.imgClose);
				holder.txtOwner = (TextView) convertView.findViewById(R.id.txtOwner);
				holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
				holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
				holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
				holder.txtMessageCount = (TextView) convertView.findViewById(R.id.txtMessageCount);
				holder.txtLikedCount = (TextView) convertView.findViewById(R.id.txtLikedCount);
				convertView.setTag(holder);
				
				holder.imgClose.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ImageView imgClose = (ImageView) v ; 
						eventIdToDelete = (String) imgClose.getTag();
						selectedIndex = position;
						
						//Toast.makeText(getApplicationContext(),
						//		"You selected for deletion = " + eventIdToDelete, Toast.LENGTH_LONG).show();
						
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(GalleryActivity.this);
						alertDialog.setTitle("Confirm Delete...");
						alertDialog.setMessage("Are you sure you want to delete this event?");
						alertDialog.setIcon(R.drawable.delete);

						alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								dialog.cancel();
								resetParseUserEvents();
								//resetListView(item, position -1,REMOVE_AS_USER_INTEREST);
								//hasInformedRemoveOnce = true;
							}
						});

						alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
						alertDialog.show();
					}
				});
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			image = (MyImageItem) getItem(position);

			holder.txtOwner.setText(image.getOwner());
			holder.txtDate.setText(image.getDate());
			holder.txtTime.setText(image.getTime());
			holder.txtMessage.setText(image.getMessage());
			holder.txtMessageCount.setText(String.valueOf(image.getMessagecount()));
			holder.txtLikedCount.setText(String.valueOf(image.getLiked()));

			holder.imgPix.setTag(image.getURL());
			imageLoader.DisplayImage(image.getURL(), holder.imgPix);
			
			holder.imgClose.setTag(image.getId());


			return convertView;
		}
	}

	private void captureImage(){
		Log.v(TAG, "Now creating output file directory for image captured");
		profileImageUri = CameraUtils.getOutputMediaFileUri(CameraUtils.MEDIA_TYPE_IMAGE, getPackageName());

		SharedPrefMgr.setString(getApplicationContext(), "profileImageUri", profileImageUri.toString());

		Log.v(TAG, "profileImageUri = " +  profileImageUri);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, profileImageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(intent, GalleryActivity.CAPTURE_CAMERA_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult=====>>>>>>>>>>");
		Log.v(TAG, "requestCode = " + requestCode);
		Log.v(TAG, "resultCode = " + resultCode);

		String strProfileImageUri = SharedPrefMgr.getString(getApplicationContext(), "profileImageUri");
		profileImageUri = Uri.parse(strProfileImageUri);

		Log.v(TAG, "onActivityResult profileImageUri = " +  profileImageUri);

		if (requestCode == GalleryActivity.CAPTURE_CAMERA_CODE) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(GalleryActivity.this, "Image Confirmed!", Toast.LENGTH_SHORT).show();
				if (profileImageUri != null) {
					try {
						file = new ParseFile( "file.jpg", CameraUtils.convertUriToBytes(GalleryActivity.this, profileImageUri));
						file.saveInBackground(new SaveCallback() {

							@Override
							public void done(com.parse.ParseException e) {
								if (e == null) {
									saveImage();
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

	private void saveImage(){
		Log.v(TAG, "Now attempt to save the image");
		Intent myIntent;
		Bundle bundle = new Bundle();
		bundle.putString("imgLink", "" + profileImageUri);
		myIntent = new Intent(GalleryActivity.this, ImageSaveActivity.class);
		myIntent.putExtras(bundle);
		startActivity(myIntent);
	}

	private void loadEventsInListView() {
		Images = new ArrayList<MyImageItem>();
		Log.v(TAG, "Eventsize here = " + EventS.size());

		// Find the ListView resource.
		mainListView = (ListView) findViewById(R.id.listEvents);
		mainListView.setEmptyView(findViewById(R.id.txtEventsEmpty));
		//((TextView) findViewById(R.id.lstInvitesEmpty)).setText("You currently have no recorded farts yet.");

		for (Event event : EventS) {
			
			String Id = event.getObjectId();
			String URL = event.getImage().getUrl();
			String message = event.getContent();
			Log.v(TAG, "check if user is via facebook");
			
			String owner = "";
			
			Log.v(TAG, "authdata =" + currentUser.getString("authData"));
			
			owner = (currentUser.getString("name") == null || "".equals(currentUser.getString("name"))) ? 
					currentUser.getString("firstName") + " " + currentUser.getString("lastName") : currentUser.getString("name");
			
			String family = ""; //hardcoded for now
			
			Date datE = event.getCreatedAt();
			SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
			String date = dfDate.format(datE);
			String time = dfTime.format(datE);
			
			int messageCount = 2;
			int likedCount = 1;
			
			MyImageItem image = new MyImageItem(Id, URL, message, owner, family, date, time,
					messageCount, likedCount);
					
			Images.add(image);
			
		}

		// Set our custom array adapter as the ListView's adapter.
		MyImageAdapter adapter = new MyImageAdapter(GalleryActivity.this, Images);
		mainListView.setAdapter(adapter);

	}
	
	private void resetParseUserEvents() {
		Utils.showProgressDialog(GalleryActivity.this, "Deleting this event in server...");
		myEvents = currentUser.getRelation("events");
		myEvents.getQuery().findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> MyEvents, ParseException e) {
				if (e == null) {
					deleteEvent(eventIdToDelete);
					Images.remove(selectedIndex);
					MyImageAdapter adapter = new MyImageAdapter(GalleryActivity.this, Images);
					mainListView.setAdapter(adapter);
				} else {
					Log.e(TAG, "There was an error trying to query MyInterests: " + e.getMessage());
				}
				Utils.hideProgressDialog();
			}
		});
	}
	
	private void deleteEvent(String selectedId) {
		//attempt to delete now:
		ParseObject.createWithoutData("Event", selectedId).deleteEventually();
/*		for (ParseObject myEvent : ScratchMyEvents) {
			if (selectedId.equalsIgnoreCase(myEvent.getObjectId().toString())){
				myEvents = currentUser.getRelation("events");
				myEvents.remove(myEvent);
				currentUser.saveEventually();
				Log.v(TAG, "removed an view.getTag() = " + selectedId);
			} else {
				Log.v(TAG, "not this one = " + myEvent.getString("content").toString());
			}
		}*/
	}
}
