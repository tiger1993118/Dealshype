package com.example.deals.sns;

import android.graphics.Bitmap;

public interface ISnsShare {
	final static int FACEBOOK = 1;
	final static int TWITTER = 2;
	
	public boolean isLoggedOn();
	public void requestLogIn();
	public void updateStatus(Bitmap bitmap, String text);
	public void setShareResultListner(ShareResultListner listner);
}
