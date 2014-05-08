package com.appfibre.lifebeam;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.appfibre.lifebeam.classes.Event;
import com.appfibre.lifebeam.utils.Utils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SlideShowEventItem extends Fragment implements OnClickListener{
	private Event event;
	
	public SlideShowEventItem(){}
	
	public void setEvent(Event event){
		this.event = event;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup) inflater.inflate(
                R.layout.viewflippercontainer, container, false);   
        
        
        String owner = "";

		ParseObject eventUser = event.getAuthor();
		
		owner = (eventUser.getString("name") == null) ? 
				eventUser.getString("firstName") + " " + eventUser.getString("lastName") : 
					eventUser.getString("name");

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
			Picasso.with(getActivity())
			       .load(imgUrl)
			       .into(imageView, new Callback(){                   
						@Override
						public void onError() {
							((ProgressBar) view.findViewById(R.id.loadingImg)).setVisibility(View.GONE);
						}
	
						@Override
						public void onSuccess() {
							((ProgressBar) view.findViewById(R.id.loadingImg)).setVisibility(View.GONE);
						}			    	   
			       });
		} else {
			imageView.setImageDrawable(null);
			((ProgressBar) view.findViewById(R.id.loadingImg)).setVisibility(View.GONE);
		}
		
		//TextView txtSettings = (TextView)view.findViewById(R.id.txtSettings);
		//txtSettings.setOnClickListener((OnClickListener) this.activity);
        
        
		view.setOnClickListener(this);
        return view;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.relViewFlipperContainer: //show hide buttons
				Log.d(getClass().getName(), this.event.getId());
				//getView().findViewById(R.id.llyNavigationHolder).setVisibility(View.VISIBLE);
				break;
			default:
					
		}
	}
}
