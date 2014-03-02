package com.example.deals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class Favorite extends Activity {
	private int[] idA = { R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6,
			R.id.t7, R.id.t8, R.id.t9, R.id.t10, R.id.t11, R.id.t12 };
	JSONArray favoriteJsonArray;
	Map<String, JSONObject> mapKeyToTileObject = Tiles.mapKeyToTileObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_favor);
		setLayout();
	}

	private void setLayout() {
		InputStream is = null;
		try {
			is = openFileInput("favorite.txt");
		} catch (FileNotFoundException e2) {
			try {
				FileOutputStream outputStream = openFileOutput("favorite.txt",
						Context.MODE_PRIVATE);
				outputStream.write("[]".getBytes());
				outputStream.close();
				is = openFileInput("favorite.txt");
			} catch (IOException e) {

				e.printStackTrace();
			}

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
			int i = 0;
			JSONObject currentTileJson;
			favoriteJsonArray = new JSONArray(result);
			for (i = 0; i < favoriteJsonArray.length(); i++) {
				try {
					currentTileJson = (JSONObject) favoriteJsonArray.get(i);
					String index = currentTileJson.getString("Order");
					loadBitmap((ImageView) findViewById(idA[i]), Favorite.this,
							"tile" + index + ".png");

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			ImageView iv = (ImageView) findViewById(idA[i]);
			iv.setVisibility(View.INVISIBLE);
			for (int restI = i + 1; restI < 12; restI++) {
				iv = (ImageView) findViewById(idA[restI]);
				iv.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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

	public void toCoupon(View view) {
		JSONObject clickedTileObject = null;
		int clickedId = view.getId();
		int index;
		for (int i = 0; i < idA.length; i++) {
			if (idA[i] == clickedId) {
				index = i;

				try {
					clickedTileObject = (JSONObject) favoriteJsonArray
							.get(index);
					Intent iCoupon = new Intent("com.example.deals.Coupon");
					iCoupon.putExtra("idindex",
							clickedTileObject.getString("Link").split("-")[1]);
					iCoupon.putExtra("idjsonobject",
							clickedTileObject.toString());
					startActivity(iCoupon);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public void onRestart() {
		super.onRestart();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_favor);
		setLayout();
	}

	public void back(View view) {
		finish();
	}
}
