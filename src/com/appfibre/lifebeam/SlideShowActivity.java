/**
 * 
 */
package com.appfibre.lifebeam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.ImageLoader;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
			
			((TextView) view.findViewById(R.id.txtSplendidCount)).setText(event.getSplendidCount().toString());
			((TextView) view.findViewById(R.id.txtRazzleCount)).setText( event.getRazzleCount().toString());

			llySplendidHolder.setTag(event.getObjectId());
			llyRazzleHolder.setTag(event.getObjectId());
			txtDeletePhoto.setTag(event.getObjectId());

			String owner = "";

			ParseObject eventUser = event.getAuthor();
			
			owner = (eventUser.getString("name") == null) ? 
					eventUser.getString("firstName") + " " + eventUser.getString("lastName") : 
						eventUser.getString("name");

			((TextView) view.findViewById(R.id.eventAuthor)).setText(owner);
			((TextView) view.findViewById(R.id.eventTitle)).setText(event.getContent());

			Date datE = event.getCreatedAt();
			SimpleDateFormat dfDate = Utils.getDateFormat();
			String date = dfDate.format(datE);

			((TextView) view.findViewById(R.id.eventDate)).setText(date);

			ImageView imageView = (ImageView) view.findViewById(R.id.imgeventPhoto);
			ImageLoader imageLoader = new ImageLoader(SlideShowActivity.this);
			imageLoader.DisplayImage(event.getImage().getUrl(), imageView);

			mViewFlipper.addView(view);
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
		final View thisFlipView = mViewFlipper.getCurrentView();
		LifebeamApp app = (LifebeamApp)getApplication();					
	    final Event event = app.getEvent((String)v.getTag());
		
		switch (v.getId()) {
			case R.id.view_flipper:
				thisFlipView.findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
				mViewFlipper.stopFlipping();
				break;
	
			case R.id.imgPlay:
				//set all viewFlipper child view's llyNavigationHolder to View.GONE
				setNavigationHolderVisbility(View.GONE);
				mViewFlipper.startFlipping();				
				break;
			case R.id.llySplendidHolder:
                if(event != null){
                	if(event.getSplendidCount() > 0){
                		Toast.makeText(getApplicationContext(), "You have already marked this as splendid.", Toast.LENGTH_SHORT).show();
                	} else {
                		event.increment("splendidCount");
                		Utils.showProgressDialog(SlideShowActivity.this, "Marking as splendid.");
						event.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e != null) {
									Toast.makeText(getApplicationContext(), "Error in splenderizing this event. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
								} else {
									((TextView)thisFlipView.findViewById(R.id.txtSplendidCount)).setText("1");																		
									
									ParseObject eventUser = event.getAuthor();																		
									
									HashMap<String, String> params =  new HashMap<String, String>();
									String userId = eventUser.getObjectId();
									
									params.put("userId", userId);
									params.put("message", "Your Event has been marked as splendid.");

									ParseCloud.callFunctionInBackground("notifyUser", params, new FunctionCallback<String>() {
									  public void done(String result, ParseException e) {
									    if (e == null) {
									      Log.v(getClass().getName(), "Notification sent.");
									    } else{
									      Log.e(getClass().getName(), e.getMessage());	
									    }
									  }
									});
									
								}
								Utils.hideProgressDialog();
							}
						});
                	}
                }						
				break;
			case R.id.llyRazzleHolder:
				if(event != null){
                	if(event.getRazzleCount() > 0){
                		Toast.makeText(getApplicationContext(), "You have already Razzle Dazzled this event.", Toast.LENGTH_SHORT).show();
                	} else {
                		event.increment("razzleCount");
                		Utils.showProgressDialog(SlideShowActivity.this, "Razzle Dazzling.");
						event.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e != null) {
									Toast.makeText(getApplicationContext(), "Error in razzle dazzling this event. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
								} else {
									((TextView)thisFlipView.findViewById(R.id.txtRazzleCount)).setText("1");
									
									HashMap<String, String> params =  new HashMap<String, String>();

									ParseObject eventUser = event.getAuthor();																											
									String userId = eventUser.getObjectId();
									
									params.put("userId", userId);
									params.put("message", "Your Event has been razzle dazzled.");

									ParseCloud.callFunctionInBackground("notifyUser", params, new FunctionCallback<String>() {
									  public void done(String result, ParseException e) {
									    if (e == null) {
									      Log.v(getClass().getName(), "Notification sent.");
									    } else{
									      Log.e(getClass().getName(), e.getMessage());	
									    }
									  }
									});

								}
								Utils.hideProgressDialog();
							}
						});
                	}
                }				
				break;	
			case R.id.txtDeletePhoto:
				Log.v(TAG, "event id for deleting this event is  = " + v.getTag());
				break;
	
			case R.id.imgGoToFirst:
				setNavigationHolderVisbility(View.VISIBLE);
				mViewFlipper.setDisplayedChild(0);			
				mViewFlipper.stopFlipping();
				mViewFlipper.setAutoStart(false);
				break;
	
			case R.id.imgGoToLast:
				setNavigationHolderVisbility(View.VISIBLE);
				mViewFlipper.setDisplayedChild(eventCount - 1);
				mViewFlipper.stopFlipping();
				mViewFlipper.setAutoStart(false);
				break;
	
			case R.id.imgGoToPrev:
				setNavigationHolderVisbility(View.VISIBLE);
				mViewFlipper.stopFlipping();
				mViewFlipper.setAutoStart(false);						
				mViewFlipper.showPrevious();
				break;
	
			case R.id.imgGoToNext:
				setNavigationHolderVisbility(View.VISIBLE);
				mViewFlipper.setAutoStart(false);						
				mViewFlipper.showNext();
				break;	
			default:
				break;
		}
	}
}