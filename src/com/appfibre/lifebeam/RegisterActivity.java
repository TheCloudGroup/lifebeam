package com.appfibre.lifebeam;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RegisterActivity extends Activity implements OnClickListener  {

	private TextView txt_Whatislifebeam,txt_lifebeam_is,txt_lifebeam_desc,txt_how,txt_register,txt_register1,txt_toafamilytree,txt_connect,txt_toaloveone,txt_continue,txt_registration;
	private ImageView img_familytree,img_smith,img_familytree1;
	private Button btn_RepeatIntro;
	private static final String TAG = "RegisterActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		//1st register page
		txt_Whatislifebeam = (TextView) findViewById(R.id.txtWhatislifebeam);
		txt_Whatislifebeam.setOnClickListener(this);
		// 2nd register page
		txt_lifebeam_is = (TextView) findViewById(R.id.txtlifebeam_is);
		txt_lifebeam_is.setOnClickListener(this);
		txt_lifebeam_desc = (TextView) findViewById(R.id.txtlifebeam_desc);
		txt_lifebeam_desc.setOnClickListener(this);
		//3rd register page
		txt_how = (TextView) findViewById(R.id.txthow);
		txt_how.setOnClickListener(this);
		//4th register page
		txt_register = (TextView) findViewById(R.id.txtregister);
		txt_register.setOnClickListener(this);
		img_familytree = (ImageView) findViewById(R.id.imglistfamilytree);
		img_familytree.setOnClickListener(this);
		//5th register
		txt_register1 = (TextView) findViewById(R.id.txtregister1);
		txt_register1.setOnClickListener(this);
		txt_toafamilytree = (TextView) findViewById(R.id.txttoafamilyaccount);
		txt_toafamilytree.setOnClickListener(this);
		img_smith = (ImageView) findViewById(R.id.imgsmith);
		img_smith.setOnClickListener(this);
		//6th register
		txt_connect = (TextView) findViewById(R.id.txtconnect);
		txt_connect.setOnClickListener(this);
		txt_toaloveone = (TextView) findViewById(R.id.txttoaloveone);
		txt_toaloveone.setOnClickListener(this);
		img_familytree1 = (ImageView) findViewById(R.id.imgfamilytree1);
		img_familytree1.setOnClickListener(this);
		//7th register
		txt_continue =  (TextView) findViewById(R.id.txtcontinewith);
		txt_continue.setOnClickListener(this);
		txt_registration = (TextView) findViewById(R.id.txtregistration);
		txt_registration.setOnClickListener(this);
		btn_RepeatIntro = (Button) findViewById(R.id.btnRepeatIntro);
		btn_RepeatIntro.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// onClick is called when a view has been clicked.
	@Override
	public void onClick(View v){

		switch(v.getId()){

		//after click go to 2nd page
		case R.id.txtWhatislifebeam:
			txt_Whatislifebeam.setVisibility(View.GONE);
			txt_lifebeam_is.setVisibility(View.VISIBLE);
			txt_lifebeam_desc.setVisibility(View.VISIBLE);
			break;
			
			//after click go to 3rd page	
		case R.id.txtlifebeam_is:
		case R.id.txtlifebeam_desc:
			txt_lifebeam_is.setVisibility(View.GONE);
			txt_lifebeam_desc.setVisibility(View.GONE);
			txt_how.setVisibility(View.VISIBLE);
			break;
		
			//after click go to 4th page
		case R.id.txthow:
			txt_how.setVisibility(View.GONE);
			txt_register.setVisibility(View.VISIBLE);
			img_familytree.setVisibility(View.VISIBLE);
			break;

			//after click go to 5th page
		case R.id.txtregister:
		case R.id.imglistfamilytree:
			txt_register.setVisibility(View.GONE);
			img_familytree.setVisibility(View.GONE);
			txt_register1.setVisibility(View.VISIBLE);
			txt_toafamilytree.setVisibility(View.VISIBLE);
			img_smith.setVisibility(View.VISIBLE);
			break;

			//after click go to 6th page
		case R.id.txtregister1:
		case R.id.imgsmith:
		case R.id.txttoafamilyaccount:
			txt_register1.setVisibility(View.GONE);
			txt_toafamilytree.setVisibility(View.GONE);
			img_smith.setVisibility(View.GONE);
			txt_connect.setVisibility(View.VISIBLE);
			txt_toaloveone.setVisibility(View.VISIBLE);
			img_familytree1.setVisibility(View.VISIBLE);
			break;

			//after click go to 7th page
		case R.id.txtconnect:
		case R.id.txttoaloveone:
		case R.id.imgfamilytree1:
			txt_connect.setVisibility(View.GONE);
			txt_toaloveone.setVisibility(View.GONE);
			img_familytree1.setVisibility(View.GONE);
			txt_continue.setVisibility(View.VISIBLE);
			txt_registration.setVisibility(View.VISIBLE);
			btn_RepeatIntro.setVisibility(View.VISIBLE);
			break;
			
			//after click go to registration form page
		case R.id.txtcontinewith:
		case R.id.txtregistration:
		case R.id.btnRepeatIntro:	
			startActivity(new Intent(RegisterActivity.this,RegistrationFormActivity.class) );
			finish();
			break;
		
		default:
			Log.v(TAG, "Unimplmented click listener here..");
			break;

		}



	}

}
