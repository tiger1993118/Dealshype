package com.example.deals;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class Favorite extends Activity {
	int[] idA = { R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6,
			R.id.t7, R.id.t8, R.id.t9, R.id.t10, R.id.t11, R.id.t12 };
	JSONArray favoriteJsonArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_favor);
		setLayoutFromLocal();
	}

	private void setLayoutFromLocal() {
		try {
			InputStream inputStream = openFileInput("favorite.txt");
			String sFavoriteJsonArray = Tiles.convertIsToString(inputStream);
			favoriteJsonArray = new JSONArray(sFavoriteJsonArray);
			JSONObject currentTileJsonObject;
			Bitmap bitmap;
			int index;
			// Set tile pic for each position
			for (index = 0; index < favoriteJsonArray.length(); index++) {
				currentTileJsonObject = favoriteJsonArray.getJSONObject(index);
				String sOrder = currentTileJsonObject.getString("Order");
				inputStream = openFileInput("tile" + sOrder + ".png");
				bitmap = BitmapFactory.decodeStream(inputStream);
				ImageView imageView = (ImageView) findViewById(idA[index]);
				imageView.setImageBitmap(bitmap);
			}
			// Extra position to invisible
			if (index < 12 && index % 2 == 1) {
				ImageView imageView = (ImageView) findViewById(idA[index]);
				imageView.setVisibility(View.INVISIBLE);
				index += 1;
			}
			while (index < 12) {
				ImageView imageView = (ImageView) findViewById(idA[index]);
				imageView.setVisibility(View.GONE);
				index += 1;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void toCoupon(View view) {
		int clickedId = view.getId();
		JSONObject currentTileObject;
		for (int index = 0; index < idA.length; index++) {
			if (idA[index] == clickedId) {
				try {
					currentTileObject = favoriteJsonArray.getJSONObject(index);
					Intent iCoupon = new Intent("com.example.deals.Coupon");
					iCoupon.putExtra("idKey",
							currentTileObject.getString("Link").split("-")[1]);
					iCoupon.putExtra("idJsonObject",
							currentTileObject.toString());
					startActivity(iCoupon);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public void onRestart() {
		super.onRestart();
		setContentView(R.layout.activity_search_favor);
		setLayoutFromLocal();
	}

	public void back(View view) {
		finish();
	}
}
