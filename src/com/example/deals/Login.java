package com.example.deals;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.deals.sns.FacebookShare;

public class Login extends Activity implements OnEditorActionListener {
	EditText etEmail;
	FacebookShare fbShare;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etEmail.setHintTextColor(Color.parseColor("#FAFAFA"));
		etEmail.setTextColor(Color.parseColor("#FAFAFA"));
		etEmail.setOnEditorActionListener(this);

		fbShare = FacebookShare.getInstance();
		fbShare.setActivity(this);
		fbShare.onCreate(savedInstanceState);
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		Intent iTiles = new Intent("com.example.deals.Tiles");
		startActivity(iTiles);
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(fbShare.isLoggedOn()){
			Intent iTiles = new Intent("com.example.deals.Tiles");
			startActivity(iTiles);
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fbShare.onActivityResult(requestCode, resultCode, data);
	}

	public void signInFb(View view){
		fbShare.requestLogIn();
	}
}
