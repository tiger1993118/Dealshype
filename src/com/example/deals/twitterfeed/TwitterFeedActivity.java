package com.example.deals.twitterfeed;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.example.deals.R;
import com.example.deals.sns.TwitterShare;

public class TwitterFeedActivity extends Activity implements OnClickListener{

	Button btnLogIn, btnTweet, btnSearch;
	EditText etTweet;
	LinearLayout layout;
	TwitterShare twitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitterfeed);
		
		twitter = TwitterShare.getInstance();
		twitter.setActivity(this);
		
		btnLogIn = (Button) findViewById(R.id.btnLoginTweet);
		btnTweet = (Button) findViewById(R.id.btnTweet);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		etTweet  = (EditText) findViewById(R.id.etTweet);
		layout = (LinearLayout) findViewById(R.id.layoutTwitter);
		
		btnLogIn.setOnClickListener(this);
		btnTweet.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		if(twitter.isLoggedOn()){
			btnLogIn.setVisibility(View.GONE);
			layout.setVisibility(View.VISIBLE);
			
			//for test - search keyword on initial page loading
			String keyword = "DealsHype";
			twitter.search(keyword);
			etTweet.setText(" #" + keyword);
			
			etTweet.requestFocus();
			etTweet.setSelection(0);

			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}else{
			btnLogIn.setVisibility(View.VISIBLE);
			layout.setVisibility(View.GONE);
		}
		
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btnLoginTweet :
				twitter.requestLogIn();
				break;
			case R.id.btnTweet :
				twitter.updateStatus(etTweet.getText().toString());
				break;
			case R.id.btnSearch :
				twitter.search(etTweet.getText().toString());
				break;
		}
	}
	
}
