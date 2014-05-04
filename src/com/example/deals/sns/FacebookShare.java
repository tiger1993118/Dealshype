package com.example.deals.sns;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class FacebookShare implements ISnsShare{
	private Activity activity;
	private UiLifecycleHelper uiHelper;	
	private static FacebookShare facebookShare;
	private ShareResultListner listner;
	
	private FacebookShare() { }
	
	public static FacebookShare getInstance(){
		if(facebookShare == null)
			facebookShare = new FacebookShare();
		return facebookShare;
	}
	
	private Session.StatusCallback sessionCallback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
		    onSessionStateChanged(session, state, exception);
		}
	};
	
	private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
        if(state.isOpened())
        	Toast.makeText(activity, "TEST - Facebook : Logged In", Toast.LENGTH_SHORT).show();
    }
	
	public void setActivity(Activity activity){
		this.activity = activity;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onCreate(Bundle savedInstanceState){
		uiHelper = new UiLifecycleHelper(activity, sessionCallback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public boolean isLoggedOn() {
		Session session = Session.getActiveSession();
		if(session != null && session.isOpened())
			return true;
		else
			return false;
	}

	@Override
	public void requestLogIn() {
		requestLogIn(false);
	}
	
	public void requestLogIn(boolean isSessionCached){
		Session session;
		if(isSessionCached){
			session = Session.openActiveSessionFromCache(activity);
		}else{
			session = Session.getActiveSession();
			
			if(session.getState() == SessionState.CREATED_TOKEN_LOADED){
				session.closeAndClearTokenInformation();
			}
			
			Session.openActiveSession(activity, true, sessionCallback);	//ask for log-in
		}
	}

	@Override
	public void updateStatus(Bitmap bitmap, String text) {
		updateStatus(bitmap, text, "");
	}
	
	public void updateStatus(Bitmap bitmap, String text, String fbId){
		
		Session session = Session.getActiveSession();
	    Request reqPost = Request.newUploadPhotoRequest(session, bitmap, null);
		
	    /**
	     * Parameters-info can be found on https://developers.facebook.com/docs/reference/api/post/
	     */
	    Bundle postParams = reqPost.getParameters();
	    postParams.putString("message", text);	//message that the user enters
	    
	    if(fbId.matches("[0-9]+"))	//facebook ID must be numeric.
	    	postParams.putString("place", fbId);	//facebook ID for location of the store / restaurant
	    
	    reqPost.setParameters(postParams);
	    reqPost.setCallback(new Callback() {
			@Override
			public void onCompleted(Response response) {
				if(response.getError() == null){
					if(listner != null)
						listner.onSuccessfullyUpdated(ISnsShare.FACEBOOK);
				}else{
					if(listner != null)
						listner.onUpdateFailed(ISnsShare.FACEBOOK);
				}	
			}
		});
		reqPost.executeAsync();
	}

	@Override
	public void setShareResultListner(ShareResultListner listner) {
		this.listner = listner;
	}
	
	public void closeCurrentSession(){
		Session.setActiveSession(null);
	}
	
	/**
	 * This method should be called when the user no longer wants Auto-LogIn
	 */
	public void deleteCachedSession(){
		Session.getActiveSession().closeAndClearTokenInformation();
	}
}
