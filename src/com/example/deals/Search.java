package com.example.deals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class Search extends Activity {
	static int[] idA = { R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6,
			R.id.t7, R.id.t8, R.id.t9, R.id.t10, R.id.t11, R.id.t12 };
	ImageView[] ivs;
	String keyWord;
	JSONArray tilesJsonArray = Tiles.tilesjsonArray;
	JSONArray searchJsonArray;
	Map<String, JSONObject> mapKeyToTileObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_favor);
		Intent iTiles = getIntent();
		keyWord = iTiles.getStringExtra("KeyWord");
		couponToTile();
		setLayout();
	}

	private void setLayout() {
		JSONArray searchJsonArray = getSearchJsonArray();
		int i;
		for (i = 0; i < searchJsonArray.length(); i++) {
			try {
				JSONObject currentCouponJsonObject = (JSONObject) searchJsonArray
						.get(i);
				JSONObject currentTileJsonObject = mapKeyToTileObject
						.get(currentCouponJsonObject.getString("Image"));

				loadBitmap((ImageView) findViewById(idA[i]), Search.this,
						"tile" + currentTileJsonObject.get("Order") + ".png");
			} catch (JSONException e) {
				Log.v("tile", "error");
			}
		}
		ImageView iv = (ImageView) findViewById(idA[i]);
		iv.setVisibility(View.INVISIBLE);
		for (int restI = i + 1; restI < 12; restI++) {
			iv = (ImageView) findViewById(idA[restI]);
			iv.setVisibility(View.GONE);
		}
	}

	public void loadBitmap(ImageView bmImage, Context context, String picName) {
		Bitmap b = null;
		InputStream is;
		try {
			is = openFileInput(picName);
			b = BitmapFactory.decodeStream(is);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bmImage.setImageBitmap(b);
	}

	public Map<String, String> couponToTile() {
		Map<String, String> mapCouponToTile = new HashMap<String, String>();
		mapKeyToTileObject = new HashMap<String, JSONObject>();
		for (int i = 0; i < tilesJsonArray.length(); i++) {
			JSONObject currentTileJsonObject;
			try {
				currentTileJsonObject = (JSONObject) tilesJsonArray.get(i);
				mapCouponToTile.put(currentTileJsonObject.getString("Link")
						.split("-")[1], currentTileJsonObject
						.getString("Image"));
				mapKeyToTileObject.put(currentTileJsonObject.getString("Link")
						.split("-")[1], currentTileJsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return mapCouponToTile;
	}

	private JSONArray getSearchJsonArray() {
		InputStream is = null;
		try {
			is = openFileInput("coupons.txt");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
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
		try {
			JSONArray jsonArray = new JSONArray(result);
			searchJsonArray = new JSONArray();
			int i;
			for (i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				if (((jsonObject.getString("Title").toLowerCase())
						.contains((keyWord.toLowerCase()))
						| (jsonObject.getString("Details").toLowerCase())
								.contains((keyWord.toLowerCase())) | (jsonObject
							.getString("Main Discount").toLowerCase())
						.contains((keyWord.toLowerCase())))
						& (mapKeyToTileObject.containsKey(jsonObject
								.getString("Image")))) {
					searchJsonArray.put(jsonObject);
				}
			}
			return searchJsonArray;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void toCoupon(View view) {
		int clickedId = view.getId();
		int index = 1;
		for (int i = 0; i < idA.length; i++) {
			if (idA[i] == clickedId) {
				index = i;
			}
		}

		JSONObject clickedJsonObject = null, clickedTileObject = null;
		String sImageUrl = null;
		try {
			clickedJsonObject = (JSONObject) searchJsonArray.get(index);
			sImageUrl = clickedJsonObject.getString("Image");
			clickedTileObject = mapKeyToTileObject.get(sImageUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Intent iCoupon = new Intent("com.example.deals.Coupon");
		iCoupon.putExtra("idindex", sImageUrl);
		iCoupon.putExtra("idjsonobject", clickedTileObject.toString());
		startActivity(iCoupon);

	}

	public void back(View view) {
		finish();
	}
}
