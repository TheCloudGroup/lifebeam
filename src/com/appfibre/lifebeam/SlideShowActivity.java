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
import android.view.animation.Animation;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow);
		mContext = this;

		mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
		mViewFlipper.setOnClickListener((OnClickListener) this);

		if(mViewFlipper.getChildCount() > 0) {
			mViewFlipper.removeAllViews();
		}

		setFlipperContent();
		//mViewFlipper.setAutoStart(true);
		mViewFlipper.setFlipInterval(4000);
		mViewFlipper.startFlipping();
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
			
			LinearLayout llySplendidHolder = (LinearLayout)view.findViewById(R.id.llySplendidHolder);
			llySplendidHolder.setOnClickListener(this);
			
			LinearLayout llyRazzleHolder = (LinearLayout)view.findViewById(R.id.llyRazzleHolder);
			llyRazzleHolder.setOnClickListener(this);
			
			TextView txtDeletePhoto = (TextView)view.findViewById(R.id.txtDeletePhoto);
			txtDeletePhoto.setOnClickListener(this);
			
			ImageView imgPlay = (ImageView)view.findViewById(R.id.imgPlay);
			imgPlay.setOnClickListener(this);
			
			ImageView imgGoToFirst = (ImageView)view.findViewById(R.id.imgGoToFirst);
			imgGoToFirst.setOnClickListener(this);
			
			ImageView imgGoToLast = (ImageView)view.findViewById(R.id.imgGoToLast);
			imgGoToLast.setOnClickListener(this);
			
			ImageView imgGoToPrev = (ImageView)view.findViewById(R.id.imgGoToPrev);
			imgGoToPrev.setOnClickListener(this);
			
			ImageView imgGoToNext = (ImageView)view.findViewById(R.id.imgGoToNext);
			imgGoToNext.setOnClickListener(this);			
			
			String splendidCount = (String) ((event.getSplendidCount() == null) ? "0" : event.getSplendidCount());
			String razzleCount = (String) ((event.getRazzleCount() == null) ? "0" : event.getRazzleCount());
			((TextView) view.findViewById(R.id.txtSplendidCount)).setText(splendidCount);
			((TextView) view.findViewById(R.id.txtRazzleCount)).setText(razzleCount);

			llySplendidHolder.setTag(event.getObjectId());
			llyRazzleHolder.setTag(event.getObjectId());
			txtDeletePhoto.setTag(event.getObjectId());

			String owner = "";

			ParseObject eventUser = event.getAuthor();
			Log.v(TAG, "monitoring eventUser here");
			Log.v(TAG, "eventUser = " + eventUser.toString());
			Log.v(TAG, "name = " + eventUser.getString("name"));
			Log.v(TAG, "firstName = " + eventUser.getString("firstName"));
			Log.v(TAG, "lastName = " + eventUser.getString("lastName"));

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

	private void setNavigationHolderVisbility(int visibility){
		int numChild = mViewFlipper.getChildCount();
		for(int i = 0; i< numChild; i++){
			View flipperChild = mViewFlipper.getChildAt(i);
			if(flipperChild != null){
				flipperChild.findViewById(R.id.llyNavigationHolder).setVisibility(visibility);
			}
		}
	}
	@Override
	public void onClick(final View v) {
		View thisFlipView = mViewFlipper.getCurrentView();
		switch (v.getId()) {
			case R.id.view_flipper:
				Log.v(TAG, "detected a click here and trying to stop flipping and show llynavigationholder here");
				thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
				mViewFlipper.stopFlipping();
				break;
	
			case R.id.imgPlay:
				//set all viewFlipper child view's llyNavigationHolder to View.GONE
				setNavigationHolderVisbility(View.GONE);
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
				setNavigationHolderVisbility(View.VISIBLE);
				Log.v(TAG, "now go to first event");
				//thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
				mViewFlipper.setDisplayedChild(0);			
				mViewFlipper.stopFlipping();
				mViewFlipper.setAutoStart(false);
				//mViewFlipper.setFlipInterval(4000);
				//mViewFlipper.startFlipping();
				break;
	
			case R.id.imgGoToLast:
				setNavigationHolderVisbility(View.VISIBLE);
				Log.v(TAG, "now go to last event");
				//thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
				mViewFlipper.setDisplayedChild(eventCount - 1);
				mViewFlipper.stopFlipping();
				mViewFlipper.setAutoStart(false);
				//mViewFlipper.setFlipInterval(4000);
				//mViewFlipper.startFlipping();
				break;
	
			case R.id.imgGoToPrev:
				setNavigationHolderVisbility(View.VISIBLE);
				//display previous image or go to the last image if first image is reached.
				//thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
				mViewFlipper.stopFlipping();
				mViewFlipper.setAutoStart(false);						
				mViewFlipper.showPrevious();
				break;
	
			case R.id.imgGoToNext:
				setNavigationHolderVisbility(View.VISIBLE);
				//display next image or go to the first image if last image is reached 
				//thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
				mViewFlipper.stopFlipping();
				mViewFlipper.setAutoStart(false);						
				mViewFlipper.showNext();
				break;	
			default:
				break;
		}
	}
}