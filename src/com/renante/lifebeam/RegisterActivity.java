package com.renante.lifebeam;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class RegisterActivity extends Activity implements OnClickListener  {
	
	private RelativeLayout RegisterIntro1, RegisterIntro2, RegisterIntro3, RegisterIntro4, RegisterIntro5, RegisterIntro6, RegisterIntro7;
	private Button btnRepeatIntro;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		RegisterIntro1 = (RelativeLayout) findViewById(R.id.RL_registerIntro1);
		RegisterIntro1.setOnClickListener(this);
		
		RegisterIntro2 = (RelativeLayout) findViewById(R.id.RL_registerIntro2);
		RegisterIntro2.setOnClickListener(this);
		
		RegisterIntro3 = (RelativeLayout) findViewById(R.id.RL_registerIntro3);
		RegisterIntro3.setOnClickListener(this);
		
		RegisterIntro4 = (RelativeLayout) findViewById(R.id.RL_registerIntro4);
		RegisterIntro4.setOnClickListener(this);
		
		RegisterIntro5 = (RelativeLayout) findViewById(R.id.RL_registerIntro5);
		RegisterIntro5.setOnClickListener(this);
		
		RegisterIntro6 = (RelativeLayout) findViewById(R.id.RL_registerIntro6);
		RegisterIntro6.setOnClickListener(this);
		
		RegisterIntro7 = (RelativeLayout) findViewById(R.id.RL_registerIntro7);
		RegisterIntro7.setOnClickListener(this);
		
		btnRepeatIntro = (Button) findViewById(R.id.btnRepeatIntro);
		btnRepeatIntro.setOnClickListener(this);
		
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
		
		case R.id.RL_registerIntro1:
			RegisterIntro1.setVisibility(View.GONE);
			RegisterIntro2.setVisibility(View.VISIBLE);
			break;
			
		case R.id.RL_registerIntro2:
			RegisterIntro2.setVisibility(View.GONE);
			RegisterIntro3.setVisibility(View.VISIBLE);
			break;
		case R.id.RL_registerIntro3:
			RegisterIntro3.setVisibility(View.GONE);
			RegisterIntro4.setVisibility(View.VISIBLE);
			break;
		case R.id.RL_registerIntro4:
			RegisterIntro4.setVisibility(View.GONE);
			RegisterIntro5.setVisibility(View.VISIBLE);
			break;
		case R.id.RL_registerIntro5:
			RegisterIntro5.setVisibility(View.GONE);
			RegisterIntro6.setVisibility(View.VISIBLE);
			break;
		case R.id.RL_registerIntro6:
			RegisterIntro6.setVisibility(View.GONE);
			RegisterIntro7.setVisibility(View.VISIBLE);
			break;
		case R.id.RL_registerIntro7:
			startActivity(new Intent(RegisterActivity.this,RegistrationFormActivity.class));
			finish();
			break;
		case R.id.btnRepeatIntro:
			RegisterIntro7.setVisibility(View.GONE);
			RegisterIntro1.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		
		}
		
	}

}
