/**
 * 
 */
package com.appfibre.lifebeam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.CameraUtils;
import com.appfibre.lifebeam.utils.ImageLoader2;
import com.appfibre.lifebeam.utils.MyImageItem;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
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

	private static final int CREATE_EVENT_CODE  = 1338;
	private Uri profileImageUri = null;

	private String TAG = "GalleryActivity";
	private ArrayList<MyImageItem> Images;
	private ArrayList<Event> EventS;

	private ParseFile file;
	private ParseUser currentUser;
	private ParseRelation<ParseObject> myEvents;
	private List<ParseObject> ScratchMyEvents;
	private int selectedIndex;
	private PullToRefreshListView mainListView;
	private MyImageAdapter mainListViewAdapter;
	
	private String eventIdToDelete;
    private String updatedEventId;
    private int updatedEventIdIndex;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		ParseAnalytics.trackAppOpened(getIntent());
		
		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		//ab.setDisplayUseLogoEnabled(false);

		// Find the ListView resource.
		mainListView = (PullToRefreshListView) findViewById(R.id.listEvents);
		mainListView.setEmptyView(findViewById(R.id.txtEventsEmpty));
		// Set a listener to be invoked when the list should be refreshed.
		mainListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				retrieveEvents(true);
			}
		});
		
		EventS = new ArrayList<Event>();
		currentUser = ParseUser.getCurrentUser();
		String userId = "";
		if (currentUser.getObjectId() != null &&  !"".equals(currentUser.getObjectId())) {
			userId = currentUser.getObjectId();
		}

		Log.v(TAG, "and the userid is = " + userId);

		retrieveEvents(false);
				
		ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
		parseInstallation.put("userId",ParseUser.getCurrentUser().getObjectId());
	    parseInstallation.saveInBackground();		
	}
	
	private void checkMarkedEvents(){
		updatedEventId = SharedPrefMgr.getString(GalleryActivity.this, "updatedEventId");
		
		if(updatedEventId != null && updatedEventId.length() > 0 && mainListViewAdapter != null){
			SharedPrefMgr.removeData(GalleryActivity.this, "updatedEventId");
			final ListView lv = mainListView.getRefreshableView();
			for(int i = 0; i < mainListViewAdapter.getCount(); i++){
				MyImageItem item = (MyImageItem)mainListViewAdapter.getItem(i);
				if(item.getId().equals(updatedEventId)){
					updatedEventIdIndex = i;
					Log.i(getClass().getName(),"Event position to scroll to: " + updatedEventIdIndex);
					break;
				}
			}
			
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
			    @Override
			    public void run() {
			    	updatedEventIdIndex = updatedEventIdIndex + 1;
			    	lv.setSelection(updatedEventIdIndex);
			    }
			}, 500);
			
		}
	}
	
	private void retrieveEvents(final boolean fromPullRefresh){
		if(!fromPullRefresh){
			Utils.showProgressDialog(this, "Loading...");
		}
		final ParseQuery<ParseUser> innerQuery = ParseUser.getQuery();
		innerQuery.whereEqualTo("family", currentUser.get("family"));
		
		ParseQuery<Event> queryEvents = new ParseQuery<Event>("Event");
		queryEvents.whereMatchesQuery("author", innerQuery);
		queryEvents.orderByDescending("createdAt");
		queryEvents.include("author");
		queryEvents.findInBackground(new FindCallback<Event>() {
			public void done(List<Event> Events, ParseException e) {
				EventS.clear();
				if (e == null) {
					for (Event event : Events) {
						//Log.v(TAG, "just printing contents of events here content = " + event.getContent());
						EventS.add(event);
					}
					loadEventsInListView();
					if(!fromPullRefresh){
						Utils.hideProgressDialog();
					} else {
						mainListViewAdapter.notifyDataSetChanged();
						mainListView.onRefreshComplete();
					}					
				} else {
					if(!fromPullRefresh){
						Utils.hideProgressDialog();
					}
					Toast.makeText(getApplicationContext(),
							"Error: " + e.getMessage(), Toast.LENGTH_LONG)
							.show();
					Log.v(TAG, "Error: " + e.getMessage());
				}
				checkMarkedEvents();
			}
		});	
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
			case R.id.menuAddEvent:
		    	Intent intent = new Intent(GalleryActivity.this, CreateEventActivity.class);
        		startActivityForResult(intent, CREATE_EVENT_CODE);
		    	break;
			case R.id.menuRefresh:
				Log.v(TAG, "selected refresh...");
				Toast.makeText(this, "Menu refresh selected", Toast.LENGTH_SHORT).show();
				break;
	
			case R.id.menuInvite:
				startActivity(new Intent(GalleryActivity.this, InviteActivity.class));
				break;
	
			case R.id.menuSettings:
				Log.v(TAG, "selected settings...");
				startActivity(new Intent(GalleryActivity.this,SettingsPhone.class));
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
				Session session = Session.getInstance();
				session.reset(getApplicationContext());
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
			TextView txtRazzleCount;
			TextView txtSplendidCount;
			

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
				holder.txtRazzleCount = (TextView) convertView.findViewById(R.id.txtRazzleCount);
				holder.txtSplendidCount = (TextView) convertView.findViewById(R.id.txtSplendidCount);
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
			holder.txtRazzleCount.setText(String.valueOf(image.getMessagecount()));
			holder.txtSplendidCount.setText(String.valueOf(image.getLiked()));
			
			if(image.getURL() != null){
				holder.imgPix.setVisibility(View.VISIBLE);
				holder.imgClose.setVisibility(View.VISIBLE);
				holder.imgPix.setTag(image.getURL());
				holder.imgClose.setTag(image.getId());
				imageLoader.DisplayImage(image.getURL(), holder.imgPix);
			} else{
				holder.imgPix.setVisibility(View.GONE);
				holder.imgClose.setVisibility(View.GONE);
			}
			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if( resultCode == RESULT_OK){
			switch( requestCode ){
			    case CREATE_EVENT_CODE:
			    	Log.i(getClass().getName(),"Created new event");
			    	retrieveEvents(false);
			        break;
		    	default:
			    	break;
			}
		}
				
	}
	
	private void loadEventsInListView() {
		if(Images != null){
			Images.clear();
		} else{
			Images = new ArrayList<MyImageItem>();	
		}
		
		Log.v(TAG, "Eventsize here = " + EventS.size());		

		for (Event event : EventS) {
			
			String Id = event.getObjectId();
			String URL;
			if(event.getImage() != null){
				URL = event.getImage().getUrl();
			} else {
				URL = null;
			}
			
			String message = event.getContent();
			Log.v(TAG, "check if user is via facebook");
			
			String owner = "";
			
			Log.v(TAG, "authdata =" + currentUser.getString("authData"));
			
			//owner = (currentUser.getString("name") == null || "".equals(currentUser.getString("name"))) ? 
			//		currentUser.getString("firstName") + " " + currentUser.getString("lastName") : currentUser.getString("name");

			ParseObject author = event.getAuthor();
			owner = author.getString("firstName") + " " + author.getString("lastName");
			
			String family = ""; //hardcoded for now
			
			Date datE = event.getCreatedAt();
			SimpleDateFormat dfDate = Utils.getDateFormat();
			SimpleDateFormat dfTime = Utils.getTimeFormat();
			String date = dfDate.format(datE);
			String time = dfTime.format(datE);
			
			int razzleCount = 0;
			try {
				razzleCount = event.getRazzleCount();
			} catch (Exception e) {
				Log.v(TAG, "Error in extracting razzeCount: " + e.getMessage());
			}
			
			int splendidCount = 0;
			try {
				splendidCount = event.getSplendidCount();
			} catch (Exception e) {
				Log.v(TAG, "Error in extracting splendidCount: " + e.getMessage());
			}

			MyImageItem image = new MyImageItem(Id, URL, message, owner, family, date, time,
					razzleCount, splendidCount);
					
			Images.add(image);
			
		}

		// Set our custom array adapter as the ListView's adapter.
		//MyImageAdapter adapter = new MyImageAdapter(GalleryActivity.this, Images);
		if(mainListViewAdapter == null){
			mainListViewAdapter = new MyImageAdapter(GalleryActivity.this, Images);
			mainListView.setAdapter(mainListViewAdapter);
		}		
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
	}
}
