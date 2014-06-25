package com.example.deals;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deals.sns.FacebookShare;
import com.example.deals.sns.ISnsShare;
import com.example.deals.sns.ShareResultListner;
import com.example.deals.sns.TwitterShare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;


public class Share extends Activity implements ShareResultListner{
	int index;
	final static int cameraData = 1;
	JSONObject jsonObject = null;
	String sPhotoTextUnderCamera, sPhotoShareButton, sPhotoShare, sLink1,
			sHashtag, sShare, sFbId;
	Bitmap bitmap;
	ImageView ivPhoto;
	EditText etMsg;

	ProgressDialog pDialog;
	TwitterShare twitterShare;
	FacebookShare facebookShare;
	CheckBox cbFb, cbTwitter;
	final static String HASHTAG_DEALSHYPE = "#DealsHype";
	
	Button redButton;
	
	 private String imagepath=null;
	 private String imageTimestamp = null;
	 String serverName = null;
	 String serverUsr = null;
	 String serverPwd = null;
	 String mCurrentPhotoPath;
	
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
			
			serverName = jsonObject.getString("serverName") ;
			serverUsr = jsonObject.getString("serverUsr") ;
			serverPwd = jsonObject.getString("serverPwd") ;
			
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
		twitterShare.setShareResultListner(this);

		facebookShare = FacebookShare.getInstance();
		facebookShare.setActivity(this);
		facebookShare.setShareResultListner(this);
		facebookShare.onCreate(savedInstanceState);
		
		

	}

	public void takePhoto(View view) {
		Intent iCamera = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(iCamera, cameraData);
	}

	public void shareOnFacebook(View view) {
		if (cbFb.isChecked() && !facebookShare.isLoggedOn()){
			boolean isSessionCached = Login.getSettings(this).getBoolean(Login.SETTINGS_FB_LOGIN, false);
			facebookShare.requestLogIn(isSessionCached);
		}
	}

	public void shareOnTwitter(View view) {
		if (cbTwitter.isChecked() && !twitterShare.isLoggedOn())
			twitterShare.requestLogIn();
	}

	public void redeemOffer(View view)
	{
		boolean isFbChecked = cbFb.isChecked();
		boolean isTwitterChecked = cbTwitter.isChecked();
		
		
		if(pDialog == null)
		{
			pDialog = new ProgressDialog(Share.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.setIndeterminate(true);
		}

		if (!(isFbChecked || isTwitterChecked))
			Toast.makeText(getApplicationContext(),
					"Please select at least one social media you want to post",
					Toast.LENGTH_SHORT).show();
		else {
			// Invoking Image upload procedure in doinBacground()
			
			pDialog.show();
			 
			if (bitmap == null) {
				//create bitmap from PlaceHoldeImageLink when there's no photo selected by user
				try {
					String url = jsonObject.getString("PlaceholderImageLink");
					new RetrievePlaceHolderAsync().execute(url);
			
			    
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"Error : Please try again",
							Toast.LENGTH_SHORT).show();
				}
				return;
			}

			if (isFbChecked) {
				facebookShare.updateStatus(bitmap, etMsg.getText().toString()
						+ " " + sHashtag + " " + HASHTAG_DEALSHYPE, sFbId);
				new RetrievePlaceHolderAsync().execute();
			}
		
			
			
			if (isTwitterChecked) {
				twitterShare.updateStatus(bitmap, etMsg.getText().toString()
						+ " " + sHashtag + " " + HASHTAG_DEALSHYPE);
				new RetrievePlaceHolderAsync().execute();
				
			}
			
		
			// TODO define action after posting image & text on onSuccessfullyUpdated & onUpdateFailed
			
		
			
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
			
			File photoFile = null;
			try
			{
			 photoFile = createImageFile();	
			}
			catch(IOException ex)
			{
				
			}
			if(photoFile != null)
			  {
				
				System.out.println(Uri.fromFile(photoFile)); // Image path Output Example: content://media/external/images/media/17630
				 ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				  String path = Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
				System.out.println(Uri.parse(path));
				imagepath = getRealPathFromURI(Uri.parse(path)); 
				
			  }
			else
			{
				Toast.makeText(getBaseContext(), "Camera not available", Toast.LENGTH_LONG).show();
			}
			
		}

		if (facebookShare != null) {
			facebookShare.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	/**
	   * Called inside 
	   * Create Image file Example:(file:///storage/emulated/0/Pictures/JPEG_20140601_225242_-254788278.jpg)
	   *
	   **/
	private File createImageFile()  throws IOException{
		// Create an image file name
		imageTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + imageTimestamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
				);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}
	
	 /**
	   * Called inside onActivityResult(*)
	   * Returns Image Path of captured image
	   *
	   **/
	private String getRealPathFromURI(Uri ImageUri) {
		String res = null;
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(ImageUri, projection, null, null, null);
		if(cursor.moveToFirst())
		{
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return  res;
    }
	
	@Override
	public void onSuccessfullyUpdated(int social) {
		switch(social){
			case ISnsShare.FACEBOOK :
				Toast.makeText(getApplicationContext(), "Facebook : Update successfully done", Toast.LENGTH_SHORT).show();			
				break;
			case ISnsShare.TWITTER :
				Toast.makeText(getApplicationContext(), "Twitter : Update successfully done", Toast.LENGTH_SHORT).show();						
				break;
		}
		if(pDialog != null)
			pDialog.dismiss();
	}

	@Override
	public void onUpdateFailed(int social) {
		switch(social){
			case ISnsShare.FACEBOOK :
				Toast.makeText(getApplicationContext(), "Facebook : Update failed", Toast.LENGTH_SHORT).show();	
				break;
			case ISnsShare.TWITTER :
				Toast.makeText(getApplicationContext(), "Twitter : Update failed", Toast.LENGTH_SHORT).show();			
				break;
		}
		if(pDialog != null)
			pDialog.dismiss();
	}

	public void back(View view) {
		finish();
	}
	
	private class RetrievePlaceHolderAsync extends AsyncTask<String, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {


			try{
				
				
				FTPClient con = null;

				con = new FTPClient();
				con.connect("waws-prod-blu-003.ftp.azurewebsites.windows.net");

				if (con.login("dealshype", "zrHd8AeWDhZoabwqm0oA9RQaEcWxsFeKKFAKxRLYs1x5NYx0PedJM0jkQQSS"))
				{

					con.enterLocalPassiveMode(); 
					con.setFileType(FTP.BINARY_FILE_TYPE);

					FileInputStream in = new FileInputStream(new File(imagepath)); 
					con.storeFile("/uploadimg/DealsHypeAppTEST_"+ imageTimestamp +".jpg", in);

                  System.out.println("Image upload success");
                  
					in.close();

					con.logout();
					con.disconnect();
					
			}
			
		   /* URL url = new URL(params[0]);
			bitmap = BitmapFactory.decodeStream(url.openStream());*/

			}

			catch (Exception e)
			{

				System.out.println(e.getMessage());
			}									

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		
			/*if(bitmap!=null)
			{
				redeemOffer(null);
										
			}*/
			
		}
	}

	
	

}

