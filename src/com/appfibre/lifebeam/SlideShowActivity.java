/**
 * 
 */
package com.appfibre.lifebeam;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import com.appfibre.lifebeam.utils.ImageLoader;
import com.appfibre.lifebeam.utils.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * @author Angel Abellanosa Jr
 *
 */
public class SlideShowActivity extends Activity {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private ViewFlipper mViewFlipper;	
	private Context mContext;
	public ArrayList<String> eventImageUrls = new ArrayList<String>();
	public List<Bitmap> eventBmps = new ArrayList<Bitmap>();	
	private static final String TAG = "SlideShowActivity";

	private final GestureDetector detector = new GestureDetector((OnGestureListener) new SwipeGestureDetector());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow);
		mContext = this;
		
		mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
		mViewFlipper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
		
		findViewById(R.id.play).setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View view) {
		        //sets auto flipping
		        mViewFlipper.setAutoStart(true);
		        mViewFlipper.setFlipInterval(4000);
		        mViewFlipper.startFlipping();
		    }
		});
		 
		findViewById(R.id.stop).setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View view) {
		        //stop auto flipping
		        mViewFlipper.stopFlipping();
		    }
		});
		
		eventImageUrls = getIntent().getExtras().getStringArrayList("eventImageUrls");  
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
		}
		
/*		if(eventBmps.size() > 0) {
			ImageView imageView = new ImageView(SlideShowActivity.this);
			ImageLoader imageLoader = new ImageLoader(SlideShowActivity.this);
			imageLoader.DisplayImage(offerLogoUrl, imageView);
		}*/
		
		/*ImageView imageView = new ImageView(SlideShowActivity.this);
		imageView.setImageBitmap(myBitmap);
		mViewFlipper.addView(imageView);*/
		
	}

	class SwipeGestureDetector extends SimpleOnGestureListener {
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
	}
	
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
	
	
}