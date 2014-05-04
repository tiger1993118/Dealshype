package com.example.deals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.deals.sns.FacebookShare;

public class Tiles extends Activity implements OnClickListener {

	ImageButton bFavor, bSearch;
	ImageView ivStore;
	EditText etSearch;
	int[] idA = { R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6,
			R.id.t7, R.id.t8, R.id.t9, R.id.t10, R.id.t11, R.id.t12, R.id.t13,
			R.id.t14, R.id.t15, R.id.t16, R.id.t17, R.id.t18 };
	public static JSONArray tilesjsonArray, couponJsonArray, searchJsonArray;
	public static String keyWord = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tiles);
		new CheckVersion().execute();
		// MyTimerTask myTask = new MyTimerTask();
		// Timer myTimer = new Timer();
		// myTimer.schedule(myTask, 0, 300000);
		setUpVariables();
	}

	protected void setUpVariables() {
		bFavor = (ImageButton) findViewById(R.id.bFavor);
		bSearch = (ImageButton) findViewById(R.id.bSearch);
		ivStore = (ImageView) findViewById(R.id.ivStore);
		etSearch = (EditText) findViewById(R.id.etSearch);
		bFavor.setOnClickListener(this);
		bSearch.setOnClickListener(this);
		Search();
	}

	protected void retrieve() {
		new ToLocalTile().execute();// retrive Tile to Local and set up
		createFavorite();// Create a new empty Favorite List for user
		new ToLocalCoupon().execute();
	}

	protected class ToLocalCoupon extends AsyncTask<Void, Void, List<Bitmap>> {

		@Override
		protected List<Bitmap> doInBackground(Void... v) {
			URLConnection urlConnection = null;
			try {
				urlConnection = new URL(
						"http://dealshype.azurewebsites.net/staticFiles/coupons.txt")
						.openConnection();// open URL
				InputStream inputStream = urlConnection.getInputStream();
				String sJsonArray = convertIsToString(inputStream);
				// Convert is to String
				FileOutputStream fileOutputStream = openFileOutput(
						"coupons.txt", Context.MODE_PRIVATE);
				// Create fos for coupons.txt
				fileOutputStream.write(sJsonArray.getBytes());
				// Write to external coupons.txt
				couponJsonArray = new JSONArray(sJsonArray);
				// write to couponJsonArray
				JSONObject currentCouponJson;
				List<Bitmap> BitmapList = new ArrayList<Bitmap>();
				Bitmap bitmap;
				URL imageURL;
				for (int index = 0; index < couponJsonArray.length(); index++) {
					currentCouponJson = couponJsonArray.getJSONObject(index);
					String sImageUrl = currentCouponJson.getString("Image");
					// sUrl[0]? current Tile Image URL
					imageURL = new URL(sImageUrl);
					bitmap = BitmapFactory.decodeStream(imageURL
							.openConnection().getInputStream());
					BitmapList.add(bitmap);
				}
				inputStream.close();
				return BitmapList;
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(List<Bitmap> BitmapList) {
			try {
				Bitmap bitmap;
				FileOutputStream fileOutputStream = null;
				for (int index = 0; index < BitmapList.size(); index++) {
					bitmap = BitmapList.get(index);
					// Current Image bitmap
					fileOutputStream = Tiles.this.openFileOutput("coupon"
							+ (index + 1) + ".png", Context.MODE_PRIVATE);
					// Write current image to external fos
					bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
				}
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	protected void createFavorite() {
		try {
			FileOutputStream fileOutputStream = openFileOutput("favorite.txt",
					Context.MODE_PRIVATE);
			fileOutputStream.write("[]".getBytes());
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected class ToLocalTile extends AsyncTask<Void, Void, List<Bitmap>> {
		ProgressDialog pDialog;

		protected void onPreExecute() {
			pDialog = new ProgressDialog(Tiles.this);
			pDialog.setTitle("Retrieving...");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.setIndeterminate(true);
			pDialog.show();
		}

		@Override
		protected List<Bitmap> doInBackground(Void... v) {
			URLConnection urlConnection = null;
			try {
				urlConnection = new URL(
						"http://dealshype.azurewebsites.net/staticFiles/tiles.txt")
						.openConnection();// open URL
				InputStream inputStream = urlConnection.getInputStream();
				String sFile = convertIsToString(inputStream);
				FileOutputStream fileOutputStream = openFileOutput("tiles.txt",
						Context.MODE_PRIVATE);
				fileOutputStream.write(sFile.getBytes());
				fileOutputStream.close();
				setProgress(10);
				// Convert is to String
				tilesjsonArray = new JSONArray(sFile);// write to JsonArray
				JSONObject currentTileJson;
				// add TileJsonArray is to isList
				URL imageURL = null;
				Bitmap bitmap;
				List<Bitmap> bitmapList = new ArrayList<Bitmap>();
				for (int index = 0; index < tilesjsonArray.length(); index++) {
					currentTileJson = tilesjsonArray.getJSONObject(index);
					String sImageUrl = currentTileJson.getString("Image");
					imageURL = new URL(sImageUrl);
					bitmap = BitmapFactory.decodeStream(imageURL
							.openConnection().getInputStream());
					FileOutputStream fileOutputStream2 = Tiles.this
							.openFileOutput("tile" + (index + 1) + ".png",
									Context.MODE_PRIVATE);
					// Write current image to external fos
					bitmap.compress(CompressFormat.PNG, 100, fileOutputStream2);
					bitmapList.add(bitmap);
				}
				return bitmapList;
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;

		}

		protected void onPostExecute(List<Bitmap> bitmapList) {
			pDialog.dismiss();
			int index;
			for (index = 0; index < bitmapList.size(); index++) {
				ImageView imageview = (ImageView) findViewById(idA[index]);
				imageview.setImageBitmap(bitmapList.get(index));
			}
			if (index < 18 && index % 2 == 1) {
				ImageView imageView = (ImageView) findViewById(idA[index]);
				imageView.setVisibility(View.INVISIBLE);
				index += 1;
			}
			while (index < 18) {
				ImageView imageView = (ImageView) findViewById(idA[index]);
				imageView.setVisibility(View.GONE);
				index += 1;
			}
		}
	}

	protected class MyTimerTask extends TimerTask {
		public void run() {
			new CheckVersion().execute();
		}
	}

	protected class CheckVersion extends AsyncTask<Void, Boolean, String> {
		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(Tiles.this);
			pDialog.setTitle("Checking For New Version...");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.setIndeterminate(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(Void... v) {
			try {

				URLConnection urlConnection = new URL(
						"http://dealshype.azurewebsites.net/staticFiles/jsonVersion.txt")
						.openConnection();// open Version URL
				BufferedReader bufferReader = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				String sNewVersion = bufferReader.readLine();
				// get the new Version String
				return sNewVersion;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String sNewVersion) {
			try {
				int newVersion = Integer.parseInt(sNewVersion);// new Version
				InputStream inputStream = openFileInput("jsonVersion.txt");
				// Open the oldVersion
				BufferedReader bufferReader = new BufferedReader(
						new InputStreamReader(inputStream));
				String sOldVersion = bufferReader.readLine();
				int oldVersion = Integer.parseInt(sOldVersion);// old version
				if (newVersion > oldVersion)
					retrieve();
				// if need updated, retrieve data from server
				else
					setLayoutFromLocal();
				pDialog.dismiss();
				// no need to update, just set up the page from storage
			} catch (FileNotFoundException e) {
				try {
					FileOutputStream outputStream = openFileOutput(
							"jsonVersion.txt", Context.MODE_PRIVATE);
					// Write new version to jsonVersion.txt
					outputStream.write(sNewVersion.getBytes());
					outputStream.close();
					pDialog.dismiss();
					retrieve();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected void setLayoutFromLocal() {
		readFromLocal();
		int index;
		for (index = 0; index < tilesjsonArray.length(); index++) {
			// Set tile pic for each position
			try {
				ImageView imageView = (ImageView) findViewById(idA[index]);
				InputStream inputStream = openFileInput("tile" + (index + 1)
						+ ".png");
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				inputStream.close();
				imageView.setImageBitmap(bitmap);
				imageView.setVisibility(View.VISIBLE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Extra position to invisible
		if (index < 18 && index % 2 == 1) {
			ImageView imageView = (ImageView) findViewById(idA[index]);
			imageView.setVisibility(View.INVISIBLE);
			index += 1;
		}
		while (index < 18) {
			ImageView imageView = (ImageView) findViewById(idA[index]);
			imageView.setVisibility(View.GONE);
			index += 1;
		}

	}

	protected void readFromLocal() {
		try {
			InputStream inputStream = openFileInput("tiles.txt");
			String sTileJsonArray = convertIsToString(inputStream);
			tilesjsonArray = new JSONArray(sTileJsonArray);
			inputStream = openFileInput("coupons.txt");
			String sCouponJsonArray = convertIsToString(inputStream);
			couponJsonArray = new JSONArray(sCouponJsonArray);
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bFavor:
			Intent iFavor = new Intent("com.example.deals.Favorite");
			startActivity(iFavor);
			break;
		case R.id.bSearch:
			ivStore.setVisibility(View.INVISIBLE);
			etSearch.setVisibility(View.VISIBLE);
			break;

		}

	}

	public void toCoupon(View view) {
		int id = view.getId();
		for (int index = 0; index < idA.length; index++) {
			if (id == idA[index]) {// Current id you clicked
				try {
					if (keyWord.length() == 0) {
						JSONObject clickedTileJson = tilesjsonArray
								.getJSONObject(index);// Current tile you
														// clicked
						if (clickedTileJson.getString("Link").split("-")[0]
								.equals("coupon")) {// You clicked on a coupon
							Intent iCoupon = new Intent(
									"com.example.deals.Coupon");
							iCoupon.putExtra("idKey", clickedTileJson
									.getString("Link").split("-")[1]);
							// Pass the key for tile to coupon
							iCoupon.putExtra("idJsonObject",
									clickedTileJson.toString());
							// Pass the current Tile Object(for favorite
							// function)
							startActivity(iCoupon);
						} else {// You clicked on a webview
							Intent iWebView = new Intent(
									"com.example.deals.Browser");
							// Open webview intent
							iWebView.putExtra("idurl", clickedTileJson
									.getString("Link").split("-")[1]);
							startActivity(iWebView);
						}
					} else {
						JSONObject clickedTileJson = searchJsonArray
								.getJSONObject(index);// Current tile you
														// clicked
						if (clickedTileJson.getString("Link").split("-")[0]
								.equals("coupon")) {// You clicked on a coupon
							Intent iCoupon = new Intent(
									"com.example.deals.Coupon");
							iCoupon.putExtra("idKey", clickedTileJson
									.getString("Link").split("-")[1]);
							// Pass the key for tile to coupon
							iCoupon.putExtra("idJsonObject",
									clickedTileJson.toString());
							// Pass the current Tile Object(for favorite
							// function)
							startActivity(iCoupon);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static String convertIsToString(InputStream is) {
		BufferedReader bufferReader = null;
		try {
			bufferReader = new BufferedReader(
					new InputStreamReader(is, "UTF-8"), 8);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		StringBuilder stringBuilder = new StringBuilder();
		String result = null;
		String line;
		try {
			while ((line = bufferReader.readLine()) != null) {
				stringBuilder.append(line);
			}
			result = stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (Exception e) {
				}
		}
		return result;
	}

	protected void onRestart() {
		super.onRestart();
	}

	private void Search() {
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int start, int before,
					int count) {
				if (cs.length() == 0) {
					setLayoutFromLocal();
					keyWord = "";
				} else {
					keyWord = cs.toString().toLowerCase(Locale.getDefault());
					searchJsonArray = new JSONArray();
					JSONObject currentCouponJsonObject;
					JSONObject currentTileJsonObject;
					for (int index1 = 0; index1 < couponJsonArray.length(); index1++) {
						try {
							currentCouponJsonObject = couponJsonArray
									.getJSONObject(index1);
							String sTile = currentCouponJsonObject
									.getString("Title");
							String sDetails = currentCouponJsonObject
									.getString("Details");
							String sMain = currentCouponJsonObject
									.getString("Main Discount");
							if (haveKeyWord(keyWord, sTile, sDetails, sMain)) {
								for (int indexTile = 0; indexTile < tilesjsonArray
										.length(); indexTile++) {
									currentTileJsonObject = tilesjsonArray
											.getJSONObject(indexTile);
									if (currentTileJsonObject.getString("Link")
											.split("-")[1]
											.equals(currentCouponJsonObject
													.get("Image"))) {
										searchJsonArray
												.put(currentTileJsonObject);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					int index2;
					InputStream inputStream;
					Bitmap bitmap;
					for (index2 = 0; index2 < searchJsonArray.length(); index2++) {
						try {
							currentTileJsonObject = searchJsonArray
									.getJSONObject(index2);
							String sOrder = currentTileJsonObject
									.getString("Order");
							inputStream = openFileInput("tile" + sOrder
									+ ".png");
							bitmap = BitmapFactory.decodeStream(inputStream);
							ImageView imageView = (ImageView) findViewById(idA[index2]);
							imageView.setVisibility(View.VISIBLE);
							imageView.setImageBitmap(bitmap);
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					if (index2 < 18 && index2 % 2 == 1) {
						ImageView imageView = (ImageView) findViewById(idA[index2]);
						imageView.setVisibility(View.INVISIBLE);
						index2 += 1;
					}
					while (index2 < 18) {
						ImageView imageView = (ImageView) findViewById(idA[index2]);
						imageView.setVisibility(View.GONE);
						index2 += 1;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	private boolean haveKeyWord(CharSequence keyWord, String sTile,
			String sDetails, String sMain) {
		if ((sTile.toLowerCase(Locale.getDefault())).contains(keyWord)
				|| (sDetails.toLowerCase(Locale.getDefault())).contains(keyWord)
				|| (sMain.toLowerCase(Locale.getDefault())).contains(keyWord)) {
			return true;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		FacebookShare fbShare = FacebookShare.getInstance();
		if(fbShare.isLoggedOn())	//close current FB session before closing the application
			fbShare.closeCurrentSession();
		
		super.onBackPressed();
	}
}
