package com.example.deals.sns;

public interface ShareResultListner {
	public void onSuccessfullyUpdated(int social);
	public void onUpdateFailed(int social);
}
