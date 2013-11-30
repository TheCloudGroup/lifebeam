package com.appfibre.lifebeam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		final Button btnRegisterFacebook = (Button) findViewById(R.id.btnRegisterUsingFacebook);
		final Button btnLogin = (Button) findViewById(R.id.btnLogin);
		final TextView txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
		
		txtForgotPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this,LostPasswordActivity.class));
				finish();
			}
		});
		
		btnRegisterFacebook.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), 
                        "Button Register Facebook selected ", Toast.LENGTH_SHORT).show();
			}
		});
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), 
                        "Button Login selected ", Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
