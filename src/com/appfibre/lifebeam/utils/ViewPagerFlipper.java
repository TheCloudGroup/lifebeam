package com.appfibre.lifebeam.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class ViewPagerFlipper extends ViewPager{
	private static final int DEFAULT_INTERVAL = 4000;
	private static final int DEFAULT_UPDATE_INTERVAL = 30000;
	
	private OnUpdatePages mUpdateHandler;
	
	private static final String TAG = "ViewPagerFlipper";
    private int mFlipInterval  = DEFAULT_INTERVAL;
    private int mUpdateInterval  = DEFAULT_UPDATE_INTERVAL;
    private boolean mAutoStart = false;
    private boolean mRunning   = false;
    private boolean mStarted   = false;
    
    private boolean mUpdateRunning   = false;
    private boolean mUpdateStarted   = false;
    
    private boolean LOGD = false;
    
	public ViewPagerFlipper(Context context) {
		super(context);
	}
	
	public ViewPagerFlipper(Context context, AttributeSet attrs) {
		   super(context, attrs);
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            boolean mUserPresent;
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mUserPresent = false;
                updateRunning();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                mUserPresent = true;
                updateRunning();
            }
        }
    };
    
	private boolean mVisible;
	private boolean mUserPresent;
	private Object mWhichChild;
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // Listen for broadcasts related to user-presence
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        getContext().registerReceiver(mReceiver, filter);

        if (mAutoStart) {
            // Automatically start when requested
            startFlipping();
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;

        getContext().unregisterReceiver(mReceiver);
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    /**
     * How long to wait before flipping to the next view
     *
     * @param milliseconds
     *            time in milliseconds
     */
    public void setFlipInterval(int milliseconds) {
        mFlipInterval = milliseconds;
    }
    
    /**
     * How long to wait before updating data
     *
     * @param milliseconds
     *            time in milliseconds
     */
    public void setUpdateInterval(int milliseconds){
    	mUpdateInterval = milliseconds;
    }
    
    public void setUpdateHandler( OnUpdatePages updater){
    	mUpdateHandler = updater;
    }

    /**
     * Start a timer to cycle through child views
     */
    public void startFlipping() {
        mStarted = true;
        updateRunning();
    }

    /**
     * No more flips
     */
    public void stopFlipping() {
        mStarted = false;
        updateRunning();
    }
    
    public void startUpdate(){
    	mUpdateStarted = true;
    	updateData();
    }
    
    public void stopUpdate(){
    	mUpdateStarted = false;
    	updateData();
    }
    
    private void updateData(){
    	boolean running = mVisible && mUpdateStarted;// && mUserPresent;
        if (running != mUpdateRunning) {
            if (running) {
                //showOnly(mWhichChild);
                Message msg = mHandler.obtainMessage(UPDATE_MSG);
                mHandler.sendMessageDelayed(msg, mFlipInterval);
            } else {
                mHandler.removeMessages(UPDATE_MSG);
            }
            mUpdateRunning = running;
        }
    }

    /**
     * Internal method to start or stop dispatching flip {@link Message} based
     * on {@link #mRunning} and {@link #mVisible} state.
     */
    private void updateRunning() {
        boolean running = mVisible && mStarted;// && mUserPresent;
        if (running != mRunning) {
            if (running) {
                //showOnly(mWhichChild);
                Message msg = mHandler.obtainMessage(FLIP_MSG);
                mHandler.sendMessageDelayed(msg, mFlipInterval);
            } else {
                mHandler.removeMessages(FLIP_MSG);
            }
            mRunning = running;
        }
    }

    /**
     * Returns true if the child views are flipping.
     */
    public boolean isFlipping() {
        return mStarted;
    }

    /**
     * Set if this view automatically calls {@link #startFlipping()} when it
     * becomes attached to a window.
     */
    public void setAutoStart(boolean autoStart) {
        mAutoStart = autoStart;
    }

    /**
     * Returns true if this view automatically calls {@link #startFlipping()}
     * when it becomes attached to a window.
     */
    public boolean isAutoStart() {
        return mAutoStart;
    }

    private final int FLIP_MSG = 1;
    private final int UPDATE_MSG = 2;
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch(msg.what){
        		case FLIP_MSG:
        			if (mRunning) {
                        showNext();
                        msg = obtainMessage(FLIP_MSG);
                        sendMessageDelayed(msg, mFlipInterval);
                    }
        			break;
        		case UPDATE_MSG:
        			if(mUpdateHandler != null){
        				mUpdateHandler.onUpdate();
        				msg = obtainMessage(UPDATE_MSG);
        				sendMessageDelayed(msg, mUpdateInterval);
        			}
        			break;
    			default:
        		    //?		
        	}            
        }
    };
    
    private void showNext(){
    	int currentItem = getCurrentItem();
    	PagerAdapter adapter = getAdapter();
    	
    	if(currentItem >= 0 && adapter != null){
    		int size        = getAdapter().getCount();
	
        	if(currentItem + 1 >= size){
        		setCurrentItem(0, false);
        	} else {
        		setCurrentItem(currentItem + 1, true);
        	}
    	}
    	
    	
    }

    public static abstract class OnUpdatePages{
        public abstract void onUpdate();
    }

}
