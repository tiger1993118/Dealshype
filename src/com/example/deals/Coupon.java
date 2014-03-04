package com.example.deals;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Coupon extends Activity {
	int image, order;
	public static JSONObject currentCouponJsonObject, currentTileObject;
	public static JSONArray favoriteJsonArray;
	ImageView ivCoupon;
	TextView tvMain, tvDetail;
	Button bRedeem, bFavor, bStore;
	String key, sMain, sDetail, sRedeem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_coupon);
		Intent intent = getIntent();
		key = intent.getStringExtra("idKey");
		// Key of tile to coupon
		try {
			currentTileObject = new JSONObject(
					intent.getStringExtra("idJsonObject"));
			// Get the clicked current tile object from Tiles
		} catch (JSONException e) {
			e.printStackTrace();
		}
		getCurrentCouponJsonObject(key);
		// Get the current coupon json object by using the key
		setLayoutFromLocal();
		// Set layout from local coupon picture
	}

	protected void getCurrentCouponJsonObject(String key) {
		try {
			JSONObject currentJsonObject;
			for (int index = 0; index < Tiles.couponJsonArray.length(); index++) {
				currentJsonObject = Tiles.couponJsonArray.getJSONObject(index);
				if (key.equals(currentJsonObject.getString("Image"))) {
					// Corresponding Coupon Json Object (link - image)
					order = index + 1;
					currentCouponJsonObject = currentJsonObject;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setLayoutFromLocal() {
		ivCoupon = (ImageView) findViewById(R.id.ivc);
		tvMain = (TextView) findViewById(R.id.tvMainDis);
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		bRedeem = (Button) findViewById(R.id.bRedeem);
		bStore = (Button) findViewById(R.id.bStore);
		bFavor = (Button) findViewById(R.id.bFavor);
		InputStream inputStream;
		try {
			inputStream = openFileInput("coupon" + order + ".png");
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			// Get the coupon pic bitmap
			inputStream.close();
			ivCoupon.setImageBitmap(bitmap);
			// Set image to coupon bitmap
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			sMain = currentCouponJsonObject.getString("Main Discount");
			sDetail = currentCouponJsonObject.getString("Details");
			sRedeem = currentCouponJsonObject.getString("Text1");
			// Get corresponding text field
		} catch (JSONException e) {
			e.printStackTrace();
		}
		tvMain.setText(sMain);
		tvDetail.setText(sDetail);
		bRedeem.setText(sRedeem);
		// Set corresponding text field
		getFavoriteArray();
		// Get Favorite Json Array from Local
		if (alreadyFavorited()) {// Set the favorite button image
			bFavor.setBackgroundResource(R.drawable.removefavbutton);
			bFavor.setText("   Remove Favorite");
		}
	}

	public void favoriteIt(View view) {
		if (alreadyFavorited()) {// Remove from favorite if already existeds
			removeFromFavorite();
			bFavor.setBackgroundResource(R.drawable.favouritebutton);
			bFavor.setText("    Add Favorite!");
		} else {// Add to favorite if not ex
			addToFavorite();
			bFavor.setBackgroundResource(R.drawable.removefavbutton);
			bFavor.setText("   Remove Favorite!");
		}
		String sNewFavoriteJsonArray = favoriteJsonArray.toString();
		try {
			FileOutputStream outputStream = openFileOutput("favorite.txt",
					Context.MODE_PRIVATE);
			outputStream.write(sNewFavoriteJsonArray.getBytes());
			// Save the new favorite Json Array to file
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getFavoriteArray() {
		try {
			InputStream inputStream = openFileInput("favorite.txt");
			String sFavoriteJsonArray = Tiles.convertIsToString(inputStream);
			// Get the Favorite Json Array From local
			favoriteJsonArray = new JSONArray(sFavoriteJsonArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private boolean alreadyFavorited() {
		for (int index = 0; index < favoriteJsonArray.length(); index++) {
			JSONObject currentJsonObject;
			try {
				currentJsonObject = favoriteJsonArray.getJSONObject(index);
				if (key.equals(currentJsonObject.getString("Link").split("-")[1])) {
					// Current Tile Json Object already in Favorite Json Array
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		// Current Tile Json Object not in Favorite Json Array
		return false;
	}

	private void addToFavorite() {
		// Put current Tile Object into favorite Json Array
		favoriteJsonArray.put(currentTileObject);
		Toast.makeText(Coupon.this, "Add To Favorite", Toast.LENGTH_SHORT)
				.show();
	}

	private void removeFromFavorite() {
		JSONArray newFavoriteJsonArray = new JSONArray();
		JSONObject currentJsonObject;
		for (int index = 0; index < favoriteJsonArray.length(); index++) {
			try {
				currentJsonObject = favoriteJsonArray.getJSONObject(index);
				if (!currentTileObject.getString("Order").equals(
						currentJsonObject.getString("Order"))) {
					// Put old Favorite Tile Object back into favorite Json
					// Array
					newFavoriteJsonArray.put(currentJsonObject);
				}
				// Current Tile Object not put into favorite Json Array
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Toast.makeText(Coupon.this, "Remove From Favorite", Toast.LENGTH_SHORT)
				.show();
		favoriteJsonArray = newFavoriteJsonArray;
	}

	public void back(View view) {
		finish();
	}

	public void sharePhoto(View view) {
		Intent iShare = new Intent("com.example.deals.Share");
		iShare.putExtra("idjsonobject", currentCouponJsonObject.toString());
		iShare.putExtra("idimage", image);
		startActivity(iShare);
	}

}
