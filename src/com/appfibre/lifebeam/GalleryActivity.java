/**
 * 
 */
package com.appfibre.lifebeam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appfibre.lifebeam.utils.ImageLoader2;
import com.appfibre.lifebeam.utils.MyImageItem;
import com.appfibre.lifebeam.utils.Session;
import com.appfibre.lifebeam.utils.SharedPrefMgr;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * @author Angel Abellanosa Jr
 *
 */
public class GalleryActivity extends Activity {

	private String TAG = "GalleryActivity";
	private ArrayList<MyImageItem> Images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);

		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		//ab.setDisplayUseLogoEnabled(false);


		Images = new ArrayList<MyImageItem>();

		Log.v(TAG, "hardcoding data initially");
		String URL = "http://cdn01.cdnwp.celebuzz.com/kourtney-kardashian/wp-content/blogs.dir/313/files/2012/11/27/Kourtney-Kardashian-Family-Beach-Day-Mason-Penelope-Scott-Disick-001.jpg";
		String Id = "123";
		String family = "Rebucas";
		String owner = "Renante Rebucas";
		Date today = Calendar.getInstance().getTime();
		SimpleDateFormat dfDate = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
		String date = dfDate.format(today);
		String time = dfTime.format(today);
		String message = "Family Time at the Beach";
		int messageCount = 3;
		int likedCount = 2;

		MyImageItem image = new MyImageItem(Id, URL, message, owner, family, date, time,
				messageCount, likedCount);
		Images.add(image);

		URL = "http://4.bp.blogspot.com/-KrTorCPO8oQ/Th5CwulB8AI/AAAAAAAAFsE/ZnpskzXkNXw/s1600/Family%2BBeach_First%2BTrip%2Bto%2BOcean%2B2.jpg";
		Id = "123";
		family = "Rebucas";
		owner = "Renante Rebucas";
		date = dfDate.format(today);
		time = dfTime.format(today);
		message = "Family Time at the Beach";
		messageCount = 3;
		likedCount = 2;

		image = new MyImageItem(Id, URL, message, owner, family, date, time,
				messageCount, likedCount);
		Images.add(image);


		Log.v(TAG, "size of Images arraylist = " + Images.size());
		// Find the ListView resource.
		ListView mainListView = (ListView) findViewById(R.id.listImages);
		//((TextView) findViewById(R.id.lstInvitesEmpty)).setText("You currently have no recorded farts yet.");

		// Set our custom array adapter as the ListView's adapter.
		MyImageAdapter adapter = new MyImageAdapter(GalleryActivity.this, Images);
		mainListView.setAdapter(adapter);

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
		
		case R.id.menuCamera:
			Log.v(TAG, "selected camera...");
			Toast.makeText(this, "Menu Camera selected", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.menuGallery:
			Log.v(TAG, "selected gallery...");
			Toast.makeText(this, "Menu Gallery selected", Toast.LENGTH_SHORT).show();
			break;	
			
		case R.id.menuRefresh:
			Log.v(TAG, "selected refresh...");
			Toast.makeText(this, "Menu refresh selected", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.menuInvite:
			Log.v(TAG, "selected invite...");
			Toast.makeText(this, "Menu Invite selected", Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.menuSettings:
			Log.v(TAG, "selected settings...");
			startActivity(new Intent(GalleryActivity.this,SettingsActivity.class));
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
			
			Session.setSessionId("");
			Session.setUserName("");
			Session.setUserPassword("");
			
			// Go to the login view
			startActivity(new Intent(GalleryActivity.this, LoginActivity.class));
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
			TextView txtOwner;
			TextView txtDate;
			TextView txtTime;
			TextView txtMessage;
			TextView txtMessageCount;
			TextView txtLikedCount;

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
				holder.txtOwner = (TextView) convertView.findViewById(R.id.txtOwner);
				holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
				holder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
				holder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
				holder.txtMessageCount = (TextView) convertView.findViewById(R.id.txtMessageCount);
				holder.txtLikedCount = (TextView) convertView.findViewById(R.id.txtLikedCount);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			image = (MyImageItem) getItem(position);

			holder.txtOwner.setText(image.getOwner());
			holder.txtDate.setText(image.getDate());
			holder.txtTime.setText(image.getTime());
			holder.txtMessage.setText(image.getMessage());
			holder.txtMessageCount.setText(String.valueOf(image.getMessagecount()));
			holder.txtLikedCount.setText(String.valueOf(image.getLiked()));

			holder.imgPix.setTag(image.getURL());
			imageLoader.DisplayImage(image.getURL(), holder.imgPix);


			return convertView;
		}
	}

}
