package com.example.deals.sns;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.Toast;

import com.example.deals.R;
import com.example.deals.twitterfeed.TwitterFeedListAdapter;

/**
 * Activity should be set w/ setActivity(Activity activity) method before using TwitterShare.
 *
 */
public class TwitterShare implements ISnsShare{
	private final String CONSUMER_KEY = "OJaWa0AJERkJhdPEruARcw"; //for test app
	private final String CONSUMER_SECRET = "yC1mL6eEBTrdKZa69k0VOuEE7GfTsjOBxtE1hWiHM"; //for test app
	private final static String CALLBACK_URL = "oauth://activity_share";

	private Activity activity;
	private Twitter twitter;
	private RequestToken reqToken;
	private AccessToken accessToken;
	
	private static TwitterShare twitterShare;
	private ShareResultListner listner;
	
	private TwitterShare() { }

	public static TwitterShare getInstance(){
		if(twitterShare == null)
			twitterShare = new TwitterShare();
		return twitterShare;
	}
	
	private void initialize() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
				
				try {
					reqToken = twitter.getOAuthRequestToken(CALLBACK_URL);
					String authUrl = reqToken.getAuthenticationURL();
					
					Context context = activity;
					Intent intent = new Intent();
					intent.setClass(context, TwitterSignInActivity.class);
					intent.putExtra("url", authUrl);
					context.startActivity(intent);
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();		
	}
	
	private void setAccessToken(final Activity act, String uriString){
		Uri uri = Uri.parse(uriString);
		final String verifier = uri.getQueryParameter("oauth_verifier");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					/**
					 * After you acquired the AccessToken for the user,
					 * the RequestToken is not required anymore. You can persist the AccessToken to any kind of persistent store
					*/
					accessToken = twitter.getOAuthAccessToken(reqToken, verifier);
					twitter.setOAuthAccessToken(accessToken);
					runCallback(new TwitCallback() {
						
						@Override
						public void run() {
							Toast.makeText(activity, "Test - Twitter : Logged In", Toast.LENGTH_SHORT).show();
							act.finish();
						}
					});
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private interface TwitCallback{
		void run();
	}
	
	private void runCallback(final TwitCallback callback){
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				callback.run();
			}
		});
	}
	
	public void setActivity(Activity activity){
		this.activity = activity;
	}

	@Override
	public boolean isLoggedOn(){
		return (twitter != null && accessToken != null) ? true : false;
	}

	@Override
	public void requestLogIn(){
		initialize();
	}
	
	public void updateStatus(String text){
		updateStatus(null, text);
	}

	@Override
	public void updateStatus(final Bitmap bitmap, final String text){
		final StatusUpdate toUpdate = new StatusUpdate(text);
		
		if(bitmap != null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] imgBytes = baos.toByteArray();
			Base64.encodeToString(imgBytes, Base64.DEFAULT);
			final ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
			toUpdate.setMedia("img", bais);
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Status status = twitter.updateStatus(toUpdate);
					
					if(status != null){	//when successfully posted on twitter
						if(listner != null){
							runCallback(new TwitCallback() {
								
								@Override
								public void run() {
										listner.onSuccessfullyUpdated(ISnsShare.TWITTER);
								}
							});
						}
					}
				} catch (TwitterException e) {
					if(listner != null){
						runCallback(new TwitCallback() {
							
							@Override
							public void run() {
								listner.onUpdateFailed(ISnsShare.TWITTER);
							}
						});
					}
					e.printStackTrace();
				}	
			}
		}).start();
	}
	
	public void search(String query){
		final Query myQuery = new Query(query);
		myQuery.count(30);	//sets the number of tweets to return per page, up to a max of 100
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					final QueryResult result = twitter.search(myQuery);
					
					if(result != null){	//when successfully posted on twitter
						runCallback(new TwitCallback() {
							
							@Override
							public void run() {
								
								ArrayList<Status> tweetResult = new ArrayList<Status>();
								for(Status status : result.getTweets()){
									tweetResult.add(status);
								}
								
								ListView lvTweet = (ListView) activity.findViewById(R.id.lvTweets);
								TwitterFeedListAdapter tAdapter = new TwitterFeedListAdapter(tweetResult, activity);
								lvTweet.setAdapter(tAdapter);
								tAdapter.notifyDataSetChanged();
							}
						});
					}
				} catch (TwitterException e) {
					runCallback(new TwitCallback() {
						@Override
						public void run() {
							Toast.makeText(activity, "Error while searching", Toast.LENGTH_SHORT).show();
						}
					});
				}	
			}
		}).start();
	}
	
	
	@Override
	public void setShareResultListner(ShareResultListner listner) {
		this.listner = listner;
	}

	public static class TwitterSignInActivity extends Activity{
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_browser);
			
			String url = getIntent().getStringExtra("url");
			
			WebView webview = (WebView)findViewById(R.id.browser);
			webview.setWebViewClient(new WebViewClient(){
				
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, final String url) {
					if(url.startsWith(CALLBACK_URL)){
						if(url.contains("denied")){
							finish();
						}
						else
							twitterShare.setAccessToken(TwitterSignInActivity.this, url);
						return true;
					}
					return false;
				}
			});
			webview.loadUrl(url);
		}
		
		public void back(View view) {
			finish();
		}
	}

}
