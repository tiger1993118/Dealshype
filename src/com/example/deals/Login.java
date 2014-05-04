package com.example.deals;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.deals.sns.FacebookShare;

public class Login extends Activity implements OnEditorActionListener, OnClickListener {
	EditText etEmail;
	FacebookShare fbShare;
	
	public static final String SETTINGS_FB_LOGIN = "SettingsFbLogin";
	private static final String PREF_SETTINGS = "PrefSettings";
	private boolean isFbSessionCached;
	
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
		
		isFbSessionCached = getSettings(this).getBoolean(SETTINGS_FB_LOGIN, false);

		//check box for canceling Auto Login to FB
		CheckBox cbFbAutoLogin = (CheckBox)findViewById(R.id.cbFbAutoLogin);
		cbFbAutoLogin.setChecked(isFbSessionCached);
		if(!isFbSessionCached)
			cbFbAutoLogin.setVisibility(View.INVISIBLE);
		cbFbAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!isChecked){
					getSettings(Login.this).edit().putBoolean(SETTINGS_FB_LOGIN, isChecked).commit();
					fbShare.deleteCachedSession();
					buttonView.setVisibility(View.INVISIBLE);
					
					//restart the app
					Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					finish();
				}
			}
		});
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
		fbShare.setActivity(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		fbShare.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_OK && fbShare.isLoggedOn()){
			//show AlertDialog to ask if the user wants auto-login feature for facebook
			AlertDialog dialogAutoLogIn = createAutoLogInDialog();
			dialogAutoLogIn.show();
		}
	}

	
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which){
		case AlertDialog.BUTTON_POSITIVE :
			getSettings(this).edit().putBoolean(SETTINGS_FB_LOGIN, true).commit();
			break;
		case AlertDialog.BUTTON_NEGATIVE :
			getSettings(this).edit().putBoolean(SETTINGS_FB_LOGIN, false).commit();
			break;
		}
		
		startTilesActivity();
	}

	public void signInFb(View view){
		fbShare.requestLogIn(isFbSessionCached);
		if(isFbSessionCached){	//logs in to Fb without user interaction After auto-login feature is on, 
			startTilesActivity();	//So, manually open Tiles Activity
		}
	}
	
	public static SharedPreferences getSettings(Activity activity){
		SharedPreferences settings = activity.getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
		return settings;
	}
	
	private AlertDialog createAutoLogInDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
		builder.setTitle(R.string.app_name);
		
		builder.setMessage("Do you want to save your Log In Information?")
		.setCancelable(false)
		.setPositiveButton("Yes", this)
		.setNegativeButton("No", this);
		
		return builder.create();
	}
	
	private void startTilesActivity(){
		Intent iTiles = new Intent("com.example.deals.Tiles");
		startActivity(iTiles);
		finish();
	}

}
