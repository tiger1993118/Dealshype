package com.example.deals;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

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

public class Search extends Activity {
	int[] idA = { R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6,
			R.id.t7, R.id.t8, R.id.t9, R.id.t10, R.id.t11, R.id.t12 };
	String keyWord;
	JSONArray searchJsonArray = new JSONArray();
	Map<String, JSONObject> mapKeyToTileObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_favor);
		Intent iTiles = getIntent();
		keyWord = iTiles.getStringExtra("KeyWord").toLowerCase();
		getSearchArray();
		setLayout();
	}

	private void getSearchArray() {
		JSONObject currentCouponJsonObject;
		JSONObject currentTileJsonObject;
		for (int index = 0; index < Tiles.couponJsonArray.length(); index++) {
			try {
				currentCouponJsonObject = Tiles.couponJsonArray
						.getJSONObject(index);
				String sTile = currentCouponJsonObject.getString("Title");
				String sDetails = currentCouponJsonObject.getString("Details");
				String sMain = currentCouponJsonObject
						.getString("Main Discount");
				if (haveKeyWord(sTile, sDetails, sMain)) {
					for (int indexTile = 0; indexTile < Tiles.tilesjsonArray
							.length(); indexTile++) {
						currentTileJsonObject = Tiles.tilesjsonArray
								.getJSONObject(indexTile);
						if (currentTileJsonObject.getString("Link").split("-")[1]
								.equals(currentCouponJsonObject.get("Image"))) {
							searchJsonArray.put(currentTileJsonObject);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean haveKeyWord(String sTile, String sDetails, String sMain) {
		if ((sTile.toLowerCase()).contains(keyWord)
				|| (sDetails.toLowerCase()).contains(keyWord)
				|| (sMain.toLowerCase()).contains(keyWord)) {
			return true;
		}
		return false;
	}

	private void setLayout() {
		JSONObject currentJsonObject;
		InputStream inputStream;
		Bitmap bitmap;
		int index;
		for (index = 0; index < searchJsonArray.length(); index++) {
			try {
				currentJsonObject = searchJsonArray.getJSONObject(index);
				String sOrder = currentJsonObject.getString("Order");
				inputStream = openFileInput("tile" + sOrder + ".png");
				bitmap = BitmapFactory.decodeStream(inputStream);
				ImageView imageView = (ImageView) findViewById(idA[index]);
				imageView.setImageBitmap(bitmap);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
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
	}

	public void toCoupon(View view) {
		int clickedId = view.getId();
		JSONObject currentTileJsonObject;
		for (int index = 0; index < searchJsonArray.length(); index++) {
			if (idA[index] == clickedId) {
				try {
					currentTileJsonObject = searchJsonArray
							.getJSONObject(index);
					Intent iCoupon = new Intent("com.example.deals.Coupon");
					iCoupon.putExtra(
							"idKey",
							currentTileJsonObject.getString("Link").split("-")[1]);
					iCoupon.putExtra("idJsonObject",
							currentTileJsonObject.toString());
					startActivity(iCoupon);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

	}

	public void back(View view) {
		finish();
	}
}
