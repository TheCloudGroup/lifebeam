package com.appfibre.lifebeam;

import java.util.ArrayList;
import java.util.List;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class SlideShowActivity2 extends FragmentActivity {
    private ViewPager mPager;    
    private List<Fragment> pagerFragments;
    private PagerAdapter mPagerAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow2);

        mPager = (ViewPager) findViewById(R.id.pager);
        
        retrieveEvent();
    }
        
    private void retrieveEvent(){
    	Utils.showProgressDialog(SlideShowActivity2.this, "Just a few clicks now..." );
		
		final ParseQuery<ParseUser> innerQuery = ParseUser.getQuery();
		String family = Session.getInstance().getUserFamilyAccount(SlideShowActivity2.this);
		innerQuery.whereEqualTo("family", family);
		
		ParseQuery<Event> queryEvents = new ParseQuery<Event>("Event");
		queryEvents.whereMatchesQuery("author", innerQuery);
		queryEvents.orderByDescending("createdAt");
		queryEvents.include("author");
		queryEvents.findInBackground(new FindCallback<Event>() {
			public void done(List<Event> events, ParseException e) {
				if(e == null){
					pagerFragments = new ArrayList<Fragment>();
					LifebeamApp app = (LifebeamApp)getApplication();
					app.setEvents(events);
					for(Event event: events){
						SlideShowEventItem slideshowItem = new SlideShowEventItem();
						slideshowItem.setEvent(event);
						pagerFragments.add(slideshowItem);
					}
			        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),pagerFragments);
			        mPager.setAdapter(mPagerAdapter);
				} else {
					Toast.makeText(SlideShowActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				Utils.hideProgressDialog();
			}
		});	
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    	private List<Fragment> fragments;
        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
