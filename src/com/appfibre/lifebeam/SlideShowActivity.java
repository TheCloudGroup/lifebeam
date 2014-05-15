package com.appfibre.lifebeam;

import java.util.ArrayList;
import java.util.List;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.Utils;
import com.appfibre.lifebeam.utils.ViewPagerFlipper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class SlideShowActivity extends FragmentActivity implements
		OnClickListener {
    private ViewPagerFlipper mPager;    
    private List<SlideShowEventItem> pagerFragments;
    private PagerAdapter mPagerAdapter;
    private LinearLayout llyNavigationHolder;
    private static final int TRANSITION_TIME = 4000;
    private static final int EVENT_UPDATE_TIME = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPager = (ViewPagerFlipper) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(3);
        mPager.setFlipInterval(TRANSITION_TIME);
        mPager.setUpdateInterval(EVENT_UPDATE_TIME);
        mPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int position) {
				SlideShowEventItem eventFragment = pagerFragments.get(position);
				eventFragment.updateNavigationView();
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}			
			@Override
			public void onPageScrollStateChanged(int state) {}
		});
        
        mPager.setUpdateHandler( new ViewPagerFlipper.OnUpdatePages() {			
			@Override
			public void onUpdate() {
				final ParseQuery<ParseUser> innerQuery = ParseUser.getQuery();
				String family = Session.getInstance().getUserFamilyAccount(SlideShowActivity.this);
				innerQuery.whereEqualTo("family", family);
				final int pagerFragmentsNum = pagerFragments.size();
				
				ParseQuery<Event> queryEvents = new ParseQuery<Event>("Event");
				queryEvents.setLimit(1000);
				queryEvents.whereMatchesQuery("author", innerQuery);
				queryEvents.orderByDescending("createdAt");
				queryEvents.include("author");
				
				List<Event> foundEvents;
                
				queryEvents.findInBackground(new FindCallback<Event>() {
					@Override
					public void done(List<Event> foundEvents, ParseException exc) {
						if(exc == null && foundEvents.size() != pagerFragmentsNum){
							pagerFragments.clear();
							LifebeamApp app = (LifebeamApp)getApplication();
							app.setEvents(foundEvents);
							for(Event event: foundEvents){
								SlideShowEventItem slideshowItem = new SlideShowEventItem();
								slideshowItem.setEvent(event);
								pagerFragments.add(slideshowItem);
							}
			                mPagerAdapter.notifyDataSetChanged();
			                Toast.makeText(SlideShowActivity.this, "Events updated", Toast.LENGTH_LONG).show();
						}
					}	
				});
			}
		});
		
        retrieveEvent();
    }
    
    public void pauseFlip(){
    	mPager.stopFlipping();
    }
    
    public void unPauseFlip(){
    	mPager.startFlipping();
    }
        
    private void retrieveEvent(){
    	Utils.showProgressDialog(SlideShowActivity.this, "Loading Events." );
		
		final ParseQuery<ParseUser> innerQuery = ParseUser.getQuery();
		String family = Session.getInstance().getUserFamilyAccount(SlideShowActivity.this);
		innerQuery.whereEqualTo("family", family);
		
		ParseQuery<Event> queryEvents = new ParseQuery<Event>("Event");
		queryEvents.setLimit(1000);
		queryEvents.whereMatchesQuery("author", innerQuery);
		queryEvents.orderByDescending("createdAt");
		queryEvents.include("author");
		queryEvents.findInBackground(new FindCallback<Event>() {
			public void done(List<Event> events, ParseException e) {
				Log.d(getClass().getName(), "Found " + events.size() + " events");
				if(e == null){
					pagerFragments = new ArrayList<SlideShowEventItem>();
					LifebeamApp app = (LifebeamApp)getApplication();
					app.setEvents(events);
					for(Event event: events){
						SlideShowEventItem slideshowItem = new SlideShowEventItem();
						slideshowItem.setEvent(event);
						pagerFragments.add(slideshowItem);
					}
			        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),pagerFragments);
			        int pageLimit = (int)(pagerFragments.size() * 0.25);
			        mPager.setAdapter(mPagerAdapter);
			        
				} else {
					Toast.makeText(SlideShowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				Utils.hideProgressDialog();
				initNavigation();
				//startFlip();
				mPager.startFlipping();
				mPager.startUpdate();
				//startUpdateEventThread();
			}
		});	
    }
    
    private void initNavigation(){
    	llyNavigationHolder = (LinearLayout)findViewById(R.id.llyNavigationHolder);
    	
        ImageView imgPlay = (ImageView)findViewById(R.id.imgPlay);
        imgPlay.setOnClickListener(this);
        
        ImageView imgGoToFirst = (ImageView)findViewById(R.id.imgGoToFirst);
		imgGoToFirst.setOnClickListener(this);
		
		ImageView imgGoToLast = (ImageView)findViewById(R.id.imgGoToLast);
		imgGoToLast.setOnClickListener(this);
		
		ImageView imgGoToPrev = (ImageView)findViewById(R.id.imgGoToPrev);
		imgGoToPrev.setOnClickListener(this);
		
		ImageView imgGoToNext = (ImageView)findViewById(R.id.imgGoToNext);
		imgGoToNext.setOnClickListener(this);	
		
		TextView txtSettings = (TextView)findViewById(R.id.txtSettings);
		txtSettings.setOnClickListener(this);
    }
    
	@Override
	public void onClick(View v) {
		int currentDisplayedIndex = mPager != null ? mPager.getCurrentItem() : -1;
		switch (v.getId()){
			case R.id.imgPlay:
				llyNavigationHolder.setVisibility(View.GONE);
			    mPager.startFlipping();
				break;
			case R.id.imgGoToFirst:
				if(mPager != null){
					mPager.setCurrentItem(0, true);
				}
				break;
			case R.id.imgGoToLast:
				if(mPager != null && pagerFragments != null && pagerFragments.size() > 0){
					mPager.setCurrentItem(pagerFragments.size() - 1, true);
				}
				break;
			case R.id.imgGoToPrev:				
				if(mPager != null && pagerFragments != null && pagerFragments.size() > 0){
					if(currentDisplayedIndex > 0){
						mPager.setCurrentItem(currentDisplayedIndex - 1, true);
					}
				}
				break;
			case R.id.imgGoToNext:
				if(mPager != null && pagerFragments != null && pagerFragments.size() > 0){
					if(currentDisplayedIndex < pagerFragments.size()){
						mPager.setCurrentItem(currentDisplayedIndex + 1, true);
					}
				}
				break;
			case R.id.txtSettings:
				startActivity(new Intent(SlideShowActivity.this, SettingsTablet.class));
				break;
			default:
				
		}
	}

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    	private List<SlideShowEventItem> fragments;
        public ScreenSlidePagerAdapter(FragmentManager fm, List<SlideShowEventItem> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public void destroyItem(View collection, int position, Object o) {        	
            View view = (View)o;
            ((ViewPagerFlipper) collection).removeView(view);
            view = null;
        }     
        
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }
        
        @Override
        public int getItemPosition(Object object){
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    @Override
    public void onPause(){
    	super.onPause();
    	mPager.stopFlipping();
    	mPager.stopUpdate();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	mPager.startFlipping();
    	mPager.startUpdate();
    }
    
    public void removeEvent(SlideShowEventItem eventItem){
    	if(eventItem != null && pagerFragments != null && pagerFragments.size() > 0 && mPagerAdapter != null){
    	    pagerFragments.remove(eventItem);
    	    mPagerAdapter.notifyDataSetChanged();
    	}
    }
}
