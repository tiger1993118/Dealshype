package com.example.deals;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deals.sns.FacebookShare;
import com.example.deals.sns.TwitterShare;

public class Share extends Activity {
	int index;
	final static int cameraData = 1;
	JSONObject jsonObject = null;
	String sPhotoTextUnderCamera, sPhotoShareButton, sPhotoShare, sLink1,
			sHashtag, sShare, sFbId;
	Bitmap bitmap;
	ImageView ivPhoto;
	EditText etMsg;

	TwitterShare twitterShare;
	FacebookShare facebookShare;
	CheckBox cbFb, cbTwitter;
	final static String HASHTAG_DEALSHYPE = "#DealsHype";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share);
		Intent intent = getIntent();
		int image = intent.getIntExtra("idimage", 0);
		try {
			jsonObject = new JSONObject(intent.getStringExtra("idjsonobject"));
			sPhotoTextUnderCamera = jsonObject
					.getString("PhotoTextUnderCamera");
			sPhotoShareButton = jsonObject.getString("PhotoShareButtonText");
			sPhotoShare = jsonObject.getString("PhotoShareText");

			sLink1 = jsonObject.getString("Link1");
			sShare = sLink1.split("-")[0];
			sFbId = sLink1.split("-")[1].split("#")[0];
			sHashtag = "#" + sLink1.split("-")[1].split("#")[1];
		} catch (JSONException e) {
			e.printStackTrace();
		}
		TextView tvUnderCamera = (TextView) findViewById(R.id.tvUnderCamera);
		TextView tvUnderMessageBox = (TextView) findViewById(R.id.tvUnderMessageBox);
		TextView tvRedeemButton = (TextView) findViewById(R.id.tvRedeem);
		ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
		tvUnderCamera.setText(sPhotoTextUnderCamera);
		tvUnderMessageBox.setText(sPhotoShare);
		tvRedeemButton.setText(sPhotoShareButton);
		ivPhoto.setImageResource(image);

		cbFb = (CheckBox) findViewById(R.id.cbFacebook);
		cbTwitter = (CheckBox) findViewById(R.id.cbTwitter);
		etMsg = (EditText) findViewById(R.id.etMessageBox);

		twitterShare = TwitterShare.getInstance();
		twitterShare.setActivity(this);

		facebookShare = FacebookShare.getInstance();
		facebookShare.setActivity(this);
		facebookShare.onCreate(savedInstanceState);
	}

	public void takePhoto(View view) {
		Intent iCamera = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(iCamera, cameraData);
	}

	public void shareOnFacebook(View view) {
		if (cbFb.isChecked() && !facebookShare.isLoggedOn())
			facebookShare.requestLogIn();
	}

	public void shareOnTwitter(View view) {
		if (cbTwitter.isChecked() && !twitterShare.isLoggedOn())
			twitterShare.requestLogIn();
	}

	public void redeemOffer(View view) {
		boolean isFbChecked = cbFb.isChecked();
		boolean isTwitterChecked = cbTwitter.isChecked();
		if (!(isFbChecked || isTwitterChecked))
			Toast.makeText(getApplicationContext(),
					"Test - Please select SNS you want to post",
					Toast.LENGTH_SHORT).show();
		else {
			if (bitmap == null) {
				Toast.makeText(getApplicationContext(),
						"TEST : Please take a photo", Toast.LENGTH_SHORT)
						.show();
				return;
			}

			if (isFbChecked) {
				facebookShare.updateStatus(bitmap, etMsg.getText().toString()
						+ " " + sHashtag + " " + HASHTAG_DEALSHYPE, sFbId);
			}
			if (isTwitterChecked) {
				twitterShare.updateStatus(bitmap, etMsg.getText().toString()
						+ " " + sHashtag + " " + HASHTAG_DEALSHYPE);
			}

			// TODO define action after posting image & text
			Toast.makeText(getApplicationContext(),
					"TO DO : RedeemOffer action", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			bitmap = savedInstanceState.getParcelable("bitmap");
			ivPhoto.setImageBitmap(bitmap);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (bitmap != null)
			outState.putParcelable("bitmap", bitmap);
	}

	@Override
	protected void onResume() {
		super.onResume();
		cbTwitter.setChecked(twitterShare.isLoggedOn());
		cbFb.setChecked(facebookShare.isLoggedOn());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == cameraData && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			bitmap = (Bitmap) extras.get("data");
			ivPhoto.setImageBitmap(bitmap);
		}

		if (facebookShare != null) {
			facebookShare.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void back(View view) {
		finish();
	}
}
