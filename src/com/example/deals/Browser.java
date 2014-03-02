package com.example.deals;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Browser extends Activity {
	WebView webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_browser);
		webView = (WebView) findViewById(R.id.browser);
		webView.setWebViewClient(new OurViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		Intent intent = getIntent();
		String url = intent.getStringExtra("idurl");
		webView.loadUrl(url);
	}

	public void back(View view) {
		finish();
	}

	public class OurViewClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView wv, String url) {
			wv.loadUrl(url);
			return true;
		}
	}
}
