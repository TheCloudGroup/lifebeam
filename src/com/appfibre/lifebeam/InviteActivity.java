/**
 * 
 */
package com.appfibre.lifebeam;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appfibre.lifebeam.utils.LoadMoreListView;
import com.appfibre.lifebeam.utils.LoadMoreListView.OnLoadMoreListener;
import com.appfibre.lifebeam.utils.MyContactItem3;
import com.parse.ParseUser;

/**
 * @author REBUCAS RENANTE
 *
 */
public class InviteActivity extends Activity  implements OnClickListener{

	private static int numContactsQueried = 0;
    	
	private String TAG = "InviteActivity";
	private boolean isSelected = false;
	private MyContactsAdapter mainListViewAdapter;
	private LoadMoreListView contactListView;
	private ArrayList<MyContactItem3> contactData = new ArrayList<MyContactItem3>();
    private int contactsCursorOffset = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);

		ActionBar ab = getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#3fc1c6"));     
		ab.setBackgroundDrawable(colorDrawable);
		ab.setDisplayHomeAsUpEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(true);
		ab.setDisplayUseLogoEnabled(false);

		// Find the ListView resource.

		// Set our custom array adapter as the ListView's adapter.
		mainListViewAdapter = new MyContactsAdapter(InviteActivity.this, contactData);

		//adapter.loadData();
		contactListView = (LoadMoreListView)findViewById(R.id.lstInviteView);
		contactListView.setAdapter(mainListViewAdapter);
		contactListView.setEmptyView(findViewById(R.id.lstInvitesEmpty));
		contactListView.setOnLoadMoreListener(new OnLoadMoreListener() {			
			@Override
			public void onLoadMore() {
				new LoadContacts().execute();
			}
		});

		Log.v(TAG, "now implementing contacts loading in a separate thread");
		new LoadContacts().execute();

		//LoadContactsTest();

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.v(TAG, "now in onStart method here =================");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_invite, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuInvite:
			Log.v(TAG, "Invites yeah via email???");
			String recepient = "";

			//email now
			if (mainListViewAdapter.getCheckedPositions() == 0) {
				Toast.makeText(InviteActivity.this, "You need to select a contact to invite.", Toast.LENGTH_LONG).show();
				return true;
			} else {
				Log.v(TAG, "loop thru recepients here");
				for(int i=0; i<mainListViewAdapter.getCount(); i++){
					MyContactItem3 contactitem = (MyContactItem3) mainListViewAdapter.getItem(i);
					if(contactitem.isSelected()){
						if ("".equals(recepient)) {
							recepient = contactitem.getContactNumber();
						} else {
							recepient += "," + contactitem.getContactNumber();	
						}
					}
				}
			}

			ParseUser user = ParseUser.getCurrentUser();
			String senderName = "";

			if (user.getString("firstName") != null && user.getString("lastName") != null) {
				senderName = user.getString("firstName") + " " + user.getString("lastName");	
			} else {
				senderName = user.getString("name");
			}

			sendEmail(InviteActivity.this, new String[]{recepient}, 
					"Lifebeam Invitation", 	//title 
					"Lifebeam Invitation",  		//subject   
					"Hello, \n\n" +
					senderName + " has sent you this invite to create a lifebeam account, so that you too can share moments with [   ]. " +
					"\n\n" +
					"Click this link : https://www.google.com" +
					"\n\n" +
					"If you have mistakenly received this email, please disregard it and have a lovely day. " +
					"However, if you are Interested in finding out more what lifebeam is all about, then visit https://www.google.com");       //body 
			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtForPhotoHolder:
			break;
		default:
			break;
		}
	}

	class LoadContacts extends AsyncTask<Void, MyContactItem3, Void> {
		//private ProgressDialog progressDialog;        
       
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//progressDialog = ProgressDialog.show(InviteActivity.this,
			//		"Processing", "Loading contacts", true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
			
			Cursor photos = null;
			Cursor emails = null;
			Cursor contactsCursor = null;
			try {
			    ContentResolver cr = getContentResolver();
			    contactsCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC LIMIT 20 OFFSET " + contactsCursorOffset);
								
				while (contactsCursor.moveToNext()) {           
					String contactId = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
					String name = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));  

					emails = getContentResolver().query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null, 
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, 
							null, null); 

					String emailAddress = "";
					while (emails.moveToNext()) {
						emailAddress = emails.getString( 
								emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					}


					Uri person = null;
					Uri imageURI = null;
					try {
						Log.v(TAG, "An image URI trace for username = " + name);
						photos = getContentResolver().query(
								ContactsContract.Data.CONTENT_URI,
								null,
								ContactsContract.Data.CONTACT_ID + " = " + contactId + " AND "
										+ ContactsContract.Data.MIMETYPE + "='"
										+ ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", 
										null, null);

						if (photos != null) {
							if (!photos.moveToFirst()) {
								imageURI = null; // no photo
							}
							person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
									.parseLong(contactId));
							imageURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
							Log.v(TAG, "An image URI trace for imageUri = " + imageURI);
						} else {
							imageURI = null; // error in cursor process
						}
					} catch (Exception e) {
						e.printStackTrace();
						imageURI = null;
					}
					
					Log.v(TAG, "username = " + name + 
							" emailAddress = " + emailAddress + 
							" contactid = " + contactId +
							" imageUri = " + imageURI);
					MyContactItem3 contact = new MyContactItem3(name, emailAddress, contactId, imageURI, isSelected);
				    publishProgress(contact);
				}  
			} catch (NullPointerException npe) {
				Log.e(TAG, "Error trying to get Contacts.");
			} finally {
				if (emails != null) {
					emails.close();
				}
				if (contactsCursor != null) {
					contactsCursor.close();
				}     
				if (photos != null) {
					photos.close();
				}   
			} 

			//Log.v(TAG, "Stored Contacts = " + ContactsHolder.size());
			Log.v(TAG, "getcount of contactsize = " + mainListViewAdapter.getCount());
			Log.v(TAG, "resetting all checkbox states here");
			return null;
		}

		@Override
		protected void onProgressUpdate(MyContactItem3... contact) {
			mainListViewAdapter.add(contact[0]);
			mainListViewAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Void unused) {
			contactsCursorOffset+=20;
			contactListView.onLoadMoreComplete();
			// stop the loading animation or something
			/*try {
				progressDialog.dismiss();
				progressDialog = null;
			} catch (Exception e) {
				Log.v(TAG, "Error in dismissing progressdialog in onPostExecute of LoadContacts TAsk");
			}*/
		}
	}

	public class MyContactsAdapter extends BaseAdapter {

		private Context context;
		private List<MyContactItem3> contacts;
		private String TAG = "MyContactsAdapter";


		public MyContactsAdapter(Context context, ArrayList<MyContactItem3> contacts) {
			this.context = context;
			this.contacts = contacts;
		}

		/*private view holder class*/
		private class ViewHolder {
			CheckBox checkBox1;
			TextView txtName;
			TextView txtNumber;
			ImageView imgPhoto;
		}

		public int getCheckedPositions(){

			int numberOfCheckedItems = 0;

			for(int i=0; i < getCount(); i++){
				MyContactItem3 contactitem = (MyContactItem3) getItem(i);
				if(contactitem.isSelected()){
					numberOfCheckedItems++;
				}
			}
			
			return numberOfCheckedItems;
		}

		@Override
		public int getCount() {
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			return contacts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return contacts.indexOf(getItem(position));
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			Log.v("ConvertView", String.valueOf(position));

			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.invite_list_item, null);
				holder = new ViewHolder();
				holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
				holder.txtNumber = (TextView) convertView.findViewById(R.id.txtNumber);
				holder.checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
				holder.imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto);
				convertView.setTag(holder);

				holder.checkBox1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v ; 
						MyContactItem3 contact = (MyContactItem3) cb.getTag();						
						contact.setSelected(cb.isChecked());
					}
				});

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			MyContactItem3 contactitem = (MyContactItem3) getItem(position);
			holder.txtName.setText(contactitem.getContactName());
			holder.txtNumber.setText(contactitem.getContactNumber());
			if (contactitem.getContactImageURI() != null) {
				holder.imgPhoto.setImageURI(contactitem.getContactImageURI());
			}
			holder.checkBox1.setChecked(contactitem.isSelected());
			holder.checkBox1.setTag(contactitem);

			return convertView;
		}
		
		public void add(MyContactItem3 myContactItem3) {
			contacts.add(myContactItem3);

		}
	}

	public static void sendEmail(Context context, String[] recipientList,
			String title, String subject, String body) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
		String recipients = "";
		for (String recipient : recipientList) {
			recipients += recipient;
		}		
		emailIntent.setType("text/plain");
		emailIntent.setData(Uri.parse("mailto:" + recipients));
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//Log.v(TAG, "is the set type being set at text/plain style ?");
		context.startActivity(Intent.createChooser(emailIntent, title));

	}
}
