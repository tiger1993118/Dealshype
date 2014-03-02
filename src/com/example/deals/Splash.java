package com.example.deals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent iLogin = new Intent("com.example.deals.Login");
					startActivity(iLogin);
				}
			}
		};
		timer.start();

	}

	protected void onPause() {
		super.onPause();
		finish();
	}

}
