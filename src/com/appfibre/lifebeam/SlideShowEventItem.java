package com.appfibre.lifebeam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SlideShowEventItem extends Fragment implements OnClickListener{
	private Event event;
	private LinearLayout llyNavigationHolder;
	private SlideShowActivity2 parentActivity;
	private LinearLayout llySplendidHolder;
	private TextView txtSplendidCount;
	
	private LinearLayout llyRazzleHolder;
	private TextView txtRazzleCount;

	
	private TextView txtRemoveEvent;
	private View thisView;
	
	public SlideShowEventItem(){}
	
	public void setEvent(Event event){
		this.event = event;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup) inflater.inflate(
                R.layout.viewflippercontainer, container, false);   
        
        Log.d(getClass().getName(), "onCreateView: " + this.event.getId());
        
        String owner = "";

		ParseObject eventUser = event.getAuthor();
		
		owner = (eventUser.getString("name") == null) ? 
				eventUser.getString("firstName") + " " + eventUser.getString("lastName") : 
					eventUser.getString("name");
				
		parentActivity      = (SlideShowActivity2)getActivity();
		llyNavigationHolder = (LinearLayout)parentActivity.findViewById(R.id.llyNavigationHolder);
		
		llySplendidHolder   = (LinearLayout)parentActivity.findViewById(R.id.llySplendidHolder);
		txtSplendidCount    = (TextView)parentActivity.findViewById(R.id.txtSplendidCount);
		llySplendidHolder.setOnClickListener(this);
		txtSplendidCount.setText(this.event.getSplendidCount().toString());
		
		llyRazzleHolder     = (LinearLayout)parentActivity.findViewById(R.id.llyRazzleHolder);
		txtRazzleCount      = (TextView)parentActivity.findViewById(R.id.txtRazzleCount);
		llyRazzleHolder.setOnClickListener(this);
		txtRazzleCount.setText(this.event.getRazzleCount().toString());
		
		txtRemoveEvent      = (TextView)parentActivity.findViewById(R.id.txtRemoveEvent);
		txtRemoveEvent.setOnClickListener(this);
		
		((TextView) view.findViewById(R.id.eventAuthor)).setText(owner);
		((TextView) view.findViewById(R.id.eventTitle)).setText(event.getContent());
        ((ProgressBar) view.findViewById(R.id.loadingImg)).setVisibility(View.VISIBLE);
        
		Date datE = event.getCreatedAt();
		SimpleDateFormat dfDate = Utils.getDateFormat();
		SimpleDateFormat dfTime = Utils.getTimeFormat();
		String date = dfDate.format(datE);
		String time = dfTime.format(datE);

		((TextView) view.findViewById(R.id.eventDate)).setText(date + " " + time);

		ImageView imageView = (ImageView) view.findViewById(R.id.imgeventPhoto);
		ParseFile imageFile = event.getImage();
		if(imageFile != null){
			String imgUrl = imageFile.getUrl();
			//imageLoader.DisplayImage(imgUrl, imageView);
			((SlideShowActivity2)getActivity()).stopFlip();
			Picasso.with(getActivity())
			       .load(imgUrl)
			       .into(imageView, new Callback(){                   
						@Override
						public void onError() {
							((ProgressBar) view.findViewById(R.id.loadingImg)).setVisibility(View.GONE);
							if(llyNavigationHolder.getVisibility() != View.VISIBLE){
								parentActivity.startFlip();
							}
						}
	
						@Override
						public void onSuccess() {
							((ProgressBar) view.findViewById(R.id.loadingImg)).setVisibility(View.GONE);
							if(llyNavigationHolder.getVisibility() != View.VISIBLE){
								parentActivity.startFlip();
							}
						}			    	   
			       });
		} else {
			imageView.setImageDrawable(null);
			((ProgressBar) view.findViewById(R.id.loadingImg)).setVisibility(View.GONE);
		}
		
		//TextView txtSettings = (TextView)view.findViewById(R.id.txtSettings);
		//txtSettings.setOnClickListener((OnClickListener) this.activity);
        
        
		view.setOnClickListener(this);
		this.thisView = view;
        return view;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.relViewFlipperContainer: //show hide buttons
				parentActivity.stopFlip();
				llyNavigationHolder.setVisibility(View.VISIBLE);
				break;
			case R.id.txtRemoveEvent:				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
			     
				alertDialogBuilder.setTitle("Remove Event");
				alertDialogBuilder.setMessage("Are you sure you want to remove this event?");
				alertDialogBuilder.setIcon(R.drawable.delete);
				
				alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
				    	dialog.cancel();
				        parentActivity.removeEventView(thisView);
				        Utils.showProgressDialog(parentActivity, "Removing Event");
						ParseObject.createWithoutData("Event", event.getId()).deleteInBackground(new DeleteCallback() {
							
							@Override
							public void done(ParseException arg0) {
								Utils.hideProgressDialog();
								if(arg0 == null){
									parentActivity.removeEventView(thisView);
								} else {
									Toast.makeText(parentActivity, arg0.getMessage(), Toast.LENGTH_SHORT).show();
								}
								
							}
						});
					}
				});
				
				alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				break;
			case R.id.llySplendidHolder:
				if(this.event != null){
                	if(event.getSplendidCount() > 0){
                		Toast.makeText(parentActivity, "You have already marked this as splendid.", Toast.LENGTH_SHORT).show();
                	} else {
                		event.increment("splendidCount");
                		Utils.showProgressDialog(parentActivity, "Marking as splendid.");
						event.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e != null) {
									Toast.makeText(parentActivity, "Error in splenderizing this event. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
								} else {
									txtSplendidCount.setText("1");																		
									ParseObject eventUser = event.getAuthor();																		
									HashMap<String, String> params =  new HashMap<String, String>();
									String userId = eventUser.getObjectId();
									
									params.put("userId", userId);
									params.put("message", "Your Event has been marked as splendid.");
									params.put("eventId", event.getObjectId());
									params.put("action", "com.appfibre.lifebeam.NOTIFY_EVENT_RATED");

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
				if(this.event != null){
                	if(event.getRazzleCount() > 0){
                		Toast.makeText(parentActivity, "You have already Razzle Dazzled this event.", Toast.LENGTH_SHORT).show();
                	} else {
                		event.increment("razzleCount");
                		Utils.showProgressDialog(parentActivity, "Razzle Dazzling.");
						event.saveInBackground(new SaveCallback() {
							@Override
							public void done(ParseException e) {
								if (e != null) {
									Toast.makeText(parentActivity, "Error in razzle dazzling this event. Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
								} else {
									txtRazzleCount.setText("1");
									
									HashMap<String, String> params =  new HashMap<String, String>();

									ParseObject eventUser = event.getAuthor();																											
									String userId = eventUser.getObjectId();
									
									params.put("userId", userId);
									params.put("message", "Your Event has been razzle dazzled.");
									params.put("eventId", event.getObjectId());
									params.put("action", "com.appfibre.lifebeam.NOTIFY_EVENT_RATED");

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
			default:
					
		}
	}
}
