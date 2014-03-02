package com.example.deals.sns;

import android.graphics.Bitmap;

public interface ISnsShare {
	public boolean isLoggedOn();
	public void requestLogIn();
	public void updateStatus(Bitmap bitmap, String text);

}
