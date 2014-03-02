package com.example.deals;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Login extends Activity implements OnEditorActionListener {
	EditText etEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etEmail.setHintTextColor(Color.parseColor("#FAFAFA"));
		etEmail.setTextColor(Color.parseColor("#FAFAFA"));
		etEmail.setOnEditorActionListener(this);

	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		Intent iTiles = new Intent("com.example.deals.Tiles");
		startActivity(iTiles);
		return false;
	}
}
