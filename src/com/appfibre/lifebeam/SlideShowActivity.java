/**
 * 
 */
package com.appfibre.lifebeam;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.ImageLoader;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * @author Angel Abellanosa Jr
 *
 */
public class SlideShowActivity extends Activity implements OnClickListener{

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private ViewFlipper mViewFlipper;	
	private Context mContext;
	public ArrayList<String> eventImageUrls = new ArrayList<String>();
	public List<Bitmap> eventBmps = new ArrayList<Bitmap>();	
	private static final String TAG = "SlideShowActivity";
	private int eventCount;

	//private final GestureDetector detector = new GestureDetector((OnGestureListener) new SwipeGestureDetector());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow);
		mContext = this;

		mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
		mViewFlipper.setOnClickListener((OnClickListener) this);

		((ImageView) findViewById(R.id.imgPlay)).setOnClickListener(this);

		/*mViewFlipper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				//detector.onTouchEvent(event);
				Log.v(TAG, "yes you touched me sir");
				return true;
			}
		});*/

		/*findViewById(R.id.imgPlay).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//sets auto flipping
				mViewFlipper.setAutoStart(true);
				mViewFlipper.setFlipInterval(4000);
				mViewFlipper.startFlipping();
			}
		});*/

		/*		findViewById(R.id.stop).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//stop auto flipping
				mViewFlipper.stopFlipping();
			}
		});*/

		//mViewFlipper.clearAnimation();
		//((RelativeLayout) findViewById(R.id.relViewFlipperContainer)).setVisibility(View.GONE);

		if(mViewFlipper.getChildCount() > 0) {
			mViewFlipper.removeAllViews();
		}



		setFlipperContent();
		mViewFlipper.setAutoStart(true);
		mViewFlipper.setFlipInterval(4000);
		mViewFlipper.startFlipping();


		/*		eventImageUrls = getIntent().getExtras().getStringArrayList("eventImageUrls");  
		if (eventImageUrls != null) {
			Log.v(TAG, "got your enventImageurls here sir");
			Utils.showProgressDialog(SlideShowActivity.this, "Loading event images from server");
			for (String eventurl : eventImageUrls) {
				Log.v(TAG, "eventurl = " + eventurl);
				ImageView imageView = new ImageView(SlideShowActivity.this);
				ImageLoader imageLoader = new ImageLoader(SlideShowActivity.this);
				imageLoader.DisplayImage(eventurl, imageView);
				mViewFlipper.addView(imageView);
			}
			Utils.hideProgressDialog();
			//new RetreiveEventImageTask().execute(eventImageUrls);
		}*/



		/*		if(eventBmps.size() > 0) {
			ImageView imageView = new ImageView(SlideShowActivity.this);
			ImageLoader imageLoader = new ImageLoader(SlideShowActivity.this);
			imageLoader.DisplayImage(offerLogoUrl, imageView);
		}*/

		/*ImageView imageView = new ImageView(SlideShowActivity.this);
		imageView.setImageBitmap(myBitmap);
		mViewFlipper.addView(imageView);*/

	}

	/*	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_left));					
					mViewFlipper.showNext();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.slide_out_right));
					mViewFlipper.showPrevious();
					return true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}
	}*/

	class RetreiveEventImageTask extends AsyncTask<ArrayList<String>, Void, Void> {
		private Exception exception;
		protected ProgressDialog progressDialog;


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(SlideShowActivity.this,
					"Processing", "Loading event images from server", true, false);
		}

		protected void onPostExecute() {
			Log.v(TAG, "reached onpostexecute for retrieveimage task");
			Log.v(TAG, "and trying to remove progressdialoghere ");

			try {
				progressDialog.dismiss();
				progressDialog = null;
			} catch (Exception e) {
				Log.e(TAG, "error in onpostexecute of RetrieveImageTasks");
			}

			/*	    	for (Bitmap eventbmps : EventBmps) {
	    		Log.v(TAG, "looping event bmp here =  " + eventbmps.toString());
	    		ImageView imageView = new ImageView(SlideShowActivity.this);
	    		imageView.setImageBitmap(eventbmps);
	    		mViewFlipper.addView(imageView);
			}*/
		}

		@Override
		protected Void doInBackground(ArrayList<String>... params) {
			Log.v(TAG, "reached doInbackground for retrieveimage task");
			ArrayList<String> EventUrls = params[0]; //get passed arraylist
			Log.v(TAG, "with size = " + EventUrls.size());

			for (String eventurls : EventUrls) {
				try {
					Log.v(TAG, "now converting this url = " + eventurls);
					URL url= new URL(eventurls);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream input = connection.getInputStream();
					Bitmap myBitmap = BitmapFactory.decodeStream(input);

					/*ImageView imageView = new ImageView(SlideShowActivity.this);
		    		imageView.setImageBitmap(myBitmap);
		    		mViewFlipper.addView(imageView);*/

					eventBmps.add(myBitmap);
					Log.v(TAG, "added a bitmap here");
					Log.v(TAG, "eventbmps size = " + eventBmps.size());
				} catch (Exception e) {
					this.exception = e;
					return null;
				}
			}
			return null;
		}
	}

	private void setFlipperContent() {
		Utils.showProgressDialog(SlideShowActivity.this, "Just a few clicks now..." );
		LifebeamApp globalVars = ((LifebeamApp)getApplication());
		List<Event> Events = globalVars.getEvents(); 

		eventCount = Events.size();

		for (Event event : Events) {
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.viewflippercontainer, null);

			String splendidCount = (String) ((event.getSplendidCount() == null) ? "0" : event.getSplendidCount());
			String razzleCount = (String) ((event.getRazzleCount() == null) ? "0" : event.getRazzleCount());
			((TextView) view.findViewById(R.id.txtSplendidCount)).setText(splendidCount);
			((TextView) view.findViewById(R.id.txtRazzleCount)).setText(razzleCount);

			((LinearLayout) view.findViewById(R.id.llySplendidHolder)).setTag(event.getObjectId());
			((LinearLayout) view.findViewById(R.id.llyRazzleHolder)).setTag(event.getObjectId());
			((TextView) view.findViewById(R.id.txtDeletePhoto)).setTag(event.getObjectId());

			String owner = "";

			ParseObject eventUser = event.getAuthor();
			Log.v(TAG, "monitoring eventUser here");
			Log.v(TAG, "eventUser = " + eventUser.toString());
			Log.v(TAG, "name = " + eventUser.getString("name"));
			Log.v(TAG, "firstName = " + eventUser.getString("firstName"));
			Log.v(TAG, "lastName = " + eventUser.getString("lastName"));

			//Log.v(TAG, "objId =" + eventUser.getObjectId());
			//Log.v(TAG, "authdata =" + eventUser.getString("name"));
			//Log.v(TAG, "authdata =" + eventUser.getString("firstName"));
			//Log.v(TAG, "authdata =" + eventUser.getString("lastName"));
			/*try {
				owner = eventUser.getString("name");
			} catch (NullPointerException npe){
				Log.v(TAG, "npe for name here");
				try {
					owner = eventUser.getString("firstName") + " " + eventUser.getString("lastName");
				} catch (NullPointerException npe2){ 
					Log.v(TAG, "npe for firstname and lastname here");
					owner = "";	
				}
			}*/

			owner = (eventUser.getString("name") == null) ? 
					eventUser.getString("firstName") + " " + eventUser.getString("lastName") : 
						eventUser.getString("name");

			((TextView) view.findViewById(R.id.eventAuthor)).setText(owner);
			((TextView) view.findViewById(R.id.eventTitle)).setText(event.getContent());

			Date datE = event.getCreatedAt();
			SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy");
			String date = dfDate.format(datE);

			((TextView) view.findViewById(R.id.eventDate)).setText(date);

			ImageView imageView = (ImageView) view.findViewById(R.id.imgeventPhoto);
			ImageLoader imageLoader = new ImageLoader(SlideShowActivity.this);
			imageLoader.DisplayImage(event.getImage().getUrl(), imageView);

			mViewFlipper.addView(view);
			Log.v(TAG, "just added a view here=============");

		}
		Utils.hideProgressDialog();
	}

	@Override
	public void onClick(final View v) {
		View thisFlipView = mViewFlipper.getCurrentView();
		int currentDisplayed = mViewFlipper.getDisplayedChild();
		switch (v.getId()) {
		case R.id.view_flipper:
			Log.v(TAG, "detected a click here and trying to stop flipping and show llynavigationholder here");
			mViewFlipper.stopFlipping();
			thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
			thisFlipView.findViewById(R.id.imgPlay).setOnClickListener(this);
			thisFlipView.findViewById(R.id.llySplendidHolder).setOnClickListener(this);
			thisFlipView.findViewById(R.id.llyRazzleHolder).setOnClickListener(this);
			thisFlipView.findViewById(R.id.txtDeletePhoto).setOnClickListener(this);
			thisFlipView.findViewById(R.id.imgGoToFirst).setOnClickListener(this);
			thisFlipView.findViewById(R.id.imgGoToLast).setOnClickListener(this);
			thisFlipView.findViewById(R.id.imgGoToPrev).setOnClickListener(this);
			thisFlipView.findViewById(R.id.imgGoToNext).setOnClickListener(this);
			break;

		case R.id.imgPlay:
			thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.GONE);
			mViewFlipper.setAutoStart(true);
			mViewFlipper.setFlipInterval(4000);
			mViewFlipper.startFlipping();
			break;

		case R.id.llySplendidHolder:
			Log.v(TAG, "event id for adding to splendid is = " + v.getTag());
			if (SharedPrefMgr.getBool(SlideShowActivity.this, 
					"hasSplendid_" + v.getTag().toString())) {
				Toast.makeText(getApplicationContext(),
						"You have already marked this as splendid.", Toast.LENGTH_LONG)
						.show();
				return;
			}
			ParseObject event = ParseObject.createWithoutData("Event", (String) v.getTag());
			event.increment("splendidCount");
			event.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						Toast.makeText(getApplicationContext(),
								"Just splenderized this event", Toast.LENGTH_LONG)
								.show();
						SharedPrefMgr.setBool(SlideShowActivity.this, 
								"hasSplendid_" + v.getTag().toString(), true);
						//Log.v(TAG, "new splendid count is now " + event.getSplendidCount());
					} else {
						Toast.makeText(getApplicationContext(),
								"Error in splenderizing this event. Error: " + e.getMessage(), Toast.LENGTH_LONG)
								.show();
					}
				}
			});
			break;

		case R.id.llyRazzleHolder:
			Log.v(TAG, "event id for adding to razzle is = " + v.getTag());
			break;

		case R.id.txtDeletePhoto:
			Log.v(TAG, "event id for deleting this event is  = " + v.getTag());
			break;

		case R.id.imgGoToFirst:
			Log.v(TAG, "now go to first event");
			//thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.GONE);
			mViewFlipper.setDisplayedChild(0);			
			mViewFlipper.stopFlipping();
			mViewFlipper.setAutoStart(false);
			//mViewFlipper.setFlipInterval(4000);
			//mViewFlipper.startFlipping();
			break;

		case R.id.imgGoToLast:
			Log.v(TAG, "now go to last event");
			//thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.GONE);
			mViewFlipper.setDisplayedChild(eventCount - 1);
			mViewFlipper.stopFlipping();
			mViewFlipper.setAutoStart(false);
			//mViewFlipper.setFlipInterval(4000);
			//mViewFlipper.startFlipping();
			break;

		case R.id.imgGoToPrev:
			//display previous image or go to the last image if first image is reached.
			mViewFlipper.stopFlipping();
			mViewFlipper.setAutoStart(false);						
			mViewFlipper.showPrevious();
			break;

		case R.id.imgGoToNext:
			//display next image or go to the first image if last image is reached 
			mViewFlipper.stopFlipping();
			mViewFlipper.setAutoStart(false);						
			mViewFlipper.showNext();
			break;	
		default:
			break;
		}
	}
}