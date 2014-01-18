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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appfibre.lifebeam.utils.MyContactItem3;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author REBUCAS RENANTE
 *
 */
public class InviteActivity extends Activity  implements OnClickListener{


	private String TAG = "InviteActivity";

	private boolean isSelected = false;
	private MyContactsAdapter adapter;
	private PullToRefreshListView mainListView;
	private ArrayList<MyContactItem3> Contacts = new ArrayList<MyContactItem3>();
	private ArrayList<MyContactItem3> ContactsHolder = new ArrayList<MyContactItem3>();
	private int iListPage;
	private final static int ITEM_PER_LIST_PAGE = 15;

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
		mainListView = (PullToRefreshListView) findViewById(R.id.lstInviteView);
		mainListView.setMode(Mode.BOTH);

		// Set a listener to be invoked when the list should be refreshed.
		mainListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				// Do work to refresh the list here.
				new GetDataTask().execute();

			}
		});

		// Set our custom array adapter as the ListView's adapter.
		adapter = new MyContactsAdapter(InviteActivity.this, Contacts);

		//adapter.loadData();

		mainListView.setAdapter(adapter);
		mainListView.setEmptyView(findViewById(R.id.lstInvitesEmpty));

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
			if (adapter.getCheckedCount() == 0) {
				Toast.makeText(InviteActivity.this, "You need to select a contact to invite.", Toast.LENGTH_LONG).show();
				return true;
			} else {
				Log.v(TAG, "loop thru recepients here");
				int iPosition = 0;
				for (Boolean checkState : adapter.checkboxstate) {
					if(checkState) {
						Log.v(TAG, "now used this position = " + iPosition + " for tagging contacts");
						MyContactItem3 contact = (MyContactItem3) adapter.getItem(iPosition);

						if ("".equals(recepient)) {
							recepient = contact.getContactNumber();
						} else {
							recepient += "," + contact.getContactNumber();	
						}
					}
					iPosition++;
				}

				//Toast.makeText(InviteActivity.this, "You are about to invite: " + recepient,
				//		Toast.LENGTH_LONG).show();

			}

			sendEmail(InviteActivity.this, new String[]{recepient}, 
					"Lifebeam Invitation", 	//title 
					"You have just been invited to a Post in Lifebeam",  		//subject   
					"Please download lifebeam in your smartphone or" +
					"tablet and get connected.");       //body 
			break;

		default:
			break;
		}

		return true;
	}

	/*	private void saveFile() {
		Utils.showProgressDialog(ImageSaveActivity.this, "Saving Image...");
		File f = new File(outputFileUri.getPath());
		byte[] reader = null;
		try {
			reader = FileUtils.readFileToByteArray(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Log.v(TAG, "reader byte array size is = " + reader.length);
		file = new ParseFile("eventImage", reader);

		file.saveInBackground((new SaveCallback() {

			@Override
			public void done(ParseException e) {
				Utils.hideProgressDialog();	
				if (e == null) {
					Log.v(TAG, "audiofile save as parsefile proceeding to savetoparse now");
					saveToParse();
				} else {
					Log.v(TAG, "Error saving soundfile: " + e.toString());
					Toast.makeText(ImageSaveActivity.this, "Error saving image. Please try again later.", Toast.LENGTH_LONG).show();
				}
			}
		}));
	}*/

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtForPhotoHolder:
			break;

		default:
			Log.e("In ImageSaveActivity onClick method", "unimplemented click listener");
			break;
		}
	}

	class LoadContacts extends AsyncTask<Void, MyContactItem3, Void> {

		private int iCountDisplayedListItems = 0;
		private ProgressDialog progressDialog;

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(InviteActivity.this,
					"Processing", "Loading contacts", true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {

			//------------------------
			//String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
			//String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";

			String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
			//String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";

			Cursor cursor = null;
			Cursor phones = null;
			Cursor emails = null;
			//String phoneNumber = "";
			//interest = new String[500];
			try {
				ContentResolver cr = getContentResolver();
				cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

				while (cursor.moveToNext()) {           
					String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
					String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

					/*phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
					while (phones.moveToNext()) {               
						phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
						phoneNumber = phoneNumber.replaceAll("\\s", "");
						Log.v(TAG, "username = " + name + 
								" mobilephone = " + phoneNumber + 
								" contactid = " + contactId);
						MyContactItem3 contact = new MyContactItem3(name, phoneNumber, contactId, !isSelected);
						if (iCountDisplayedListItems < ITEM_PER_LIST_PAGE) {
							publishProgress(contact);
							iCountDisplayedListItems++;
						} else {
							iListPage = 1;
						}
						ContactsHolder.add(contact);

					}*/   

					emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
					while (emails.moveToNext()) { 
						// This would allow you get several email addresses 
						String emailAddress = emails.getString( 
								emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
						Log.v(TAG, "username = " + name + 
								" emailAddress = " + emailAddress + 
								" contactid = " + contactId);
						MyContactItem3 contact = new MyContactItem3(name, emailAddress, contactId, !isSelected);
						if (iCountDisplayedListItems < ITEM_PER_LIST_PAGE) {
							publishProgress(contact);
							iCountDisplayedListItems++;
						} else {
							iListPage = 1;
						}
						ContactsHolder.add(contact);
					} 

					/*phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,null, null);
					while (phones.moveToNext()) {               
						phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
						phoneNumber = phoneNumber.replaceAll("\\s", "");
						Log.v(TAG, "username = " + name + 
								" mobilephone = " + phoneNumber + 
								" contactid = " + contactId);
						MyContactItem3 contact = new MyContactItem3(name, emailAddress, contactId, !isSelected);
						if (iCountDisplayedListItems < ITEM_PER_LIST_PAGE) {
							publishProgress(contact);
							iCountDisplayedListItems++;
						} else {
							iListPage = 1;
						}
						ContactsHolder.add(contact);

					}*/


				}  
			} catch (NullPointerException npe) {
				Log.e(TAG, "Error trying to get Contacts.");
			} finally {
				if (phones != null) {
					phones.close();
				}
				if (cursor != null) {
					cursor.close();
				}           
			} 

			Log.v(TAG, "Stored Contacts = " + ContactsHolder.size());
			Log.v(TAG, "getcount of contactsize = " + adapter.getCount());
			Log.v(TAG, "resetting all checkbox states here");
			adapter.resetCheckBoxesState();
			return null;
		}

		@Override
		protected void onProgressUpdate(MyContactItem3... contact) {
			adapter.add(contact[0]);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Void unused) {
			// stop the loading animation or something
			try {
				progressDialog.dismiss();
				progressDialog = null;
			} catch (Exception e) {
				Log.v(TAG, "Error in dismissing progressdialog in onPostExecute of LoadContacts TAsk");
			}
		}
	}

	public class MyContactsAdapter extends BaseAdapter {

		private Context context;
		private List<MyContactItem3> contacts;
		private MyContactItem3 contact;
		private String TAG = "MyContactsAdapter";
		private ArrayList<Boolean> checkboxstate;
		private List<MyContactItem3> checkedContacts;
		private List<Integer> checkedPositions;


		public MyContactsAdapter(Context context, ArrayList<MyContactItem3> contacts) {
			this.context = context;
			this.contacts = contacts;
		}


		/*public MyContactsAdapter() {
			// TODO Auto-generated constructor stub
		}*/


		/*private view holder class*/
		private class ViewHolder {
			CheckBox checkBox1;
			TextView txtName;
			TextView txtNumber;

		}

		public int getCheckedCount(){
			int iCount = 0;
			for (Boolean checkState : checkboxstate) {
				if(checkState) {
					iCount++;
				}
			}
			return iCount;
		}

		public List<Integer> getCheckedPositions(){
			checkedPositions = new ArrayList<Integer>();

			Log.v(TAG, "getting the positions of checked contact here");
			int iPosition = 0;

			for (Boolean checkState : checkboxstate) {
				if(checkState) {
					Log.v(TAG, "now used this position = " + iPosition + " for tagging contacts");
					//checkedContacts.add((MyContactItem) this.getItem(iPosition));
					checkedPositions.add(iPosition);
					//Log.v(TAG, )
				}
				iPosition++;
			}

			return checkedPositions;
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

			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.invite_list_item, null);
				holder = new ViewHolder();
				holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
				holder.txtNumber = (TextView) convertView.findViewById(R.id.txtNumber);
				holder.checkBox1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final MyContactItem3 contactitem = (MyContactItem3) getItem(position);
			holder.txtName.setText(contactitem.getContactName());
			holder.txtNumber.setText(contactitem.getContactNumber());

			holder.checkBox1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(((CheckBox)v).isChecked()){
						checkboxstate.set(position,true);
						Log.v(TAG, "you checked " + contactitem.getContactName().toString());
						Log.v(TAG, "with email " + contactitem.getContactNumber().toString());
						contactitem.setSelected(true);
					}else{
						checkboxstate.set(position,false);
						contactitem.setSelected(false);
					}
					Log.v(TAG, "selected total = " + adapter.getCheckedCount());
					Log.v(TAG, "selected total = " + adapter.getCheckedPositions().size());
				}
			});
			return convertView;
		}

		/**
		 * Loads the data. 
		 */
		public void loadData() {

			// Here add your code to load the data for example from a webservice or DB
			if (adapter.getCount() < ContactsHolder.size()) {
				Log.v(TAG, "adapter.getcount = " + adapter.getCount());
				int iLoopStart = iListPage*ITEM_PER_LIST_PAGE;
				int iLoopEnd = (iListPage + 1)*ITEM_PER_LIST_PAGE > ContactsHolder.size() ? ContactsHolder.size() : (iListPage + 1)*ITEM_PER_LIST_PAGE; 
				Log.v(TAG, "loop start = " + iLoopStart);
				Log.v(TAG, "end loop = " + iLoopEnd);

				for (int i = iLoopStart; i < iLoopEnd; i++) {
					Log.v(TAG, "i = " + i);
					Log.v(TAG, "add this contactsholder = " + ContactsHolder.get(i).toString());
					adapter.add(ContactsHolder.get(i));
				}	
				iListPage++;
				adapter.resetCheckBoxesState();
			}
		}

		public void resetCheckBoxesState() {
			checkboxstate = new ArrayList<Boolean>();
			for (int i = 0; i < getCount(); i++) {
				checkboxstate.add(i, false);
			}
		}

		public void add(MyContactItem3 myContactItem3) {
			contacts.add(myContactItem3);

		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			adapter.loadData();
			return null;
		}

		@Override
		protected void onPostExecute(Void ret) {
			adapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mainListView.onRefreshComplete();
			super.onPostExecute(null);
		}
	}

	private void LoadContactsTest() {
		Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null); 
		while (cursor.moveToNext()) { 
			String contactId = cursor.getString(cursor.getColumnIndex( 
					ContactsContract.Contacts._ID)); 
			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
			if (Boolean.parseBoolean(hasPhone)) { 
				// You know it has a number so now query it like this
				Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
				while (phones.moveToNext()) { 
					String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));                 
					Log.v(TAG, "phoneNumber = " + phoneNumber);
				} 
				phones.close(); 
			}

			Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null); 
			while (emails.moveToNext()) { 
				// This would allow you get several email addresses 
				String emailAddress = emails.getString( 
						emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				Log.v(TAG, "emailAddress = " + emailAddress);
			} 
			emails.close();
		}
		cursor.close(); 
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