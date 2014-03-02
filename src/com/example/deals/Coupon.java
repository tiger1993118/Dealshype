package com.example.deals;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Coupon extends Activity {
	ImageView iv, ivFavoriteIt, ivCoupon;
	String index;
	int image;
	JSONObject currentCoupon, currentTileObject;
	public static JSONArray FavoriteJsonArray, tilesjsonArray;
	TextView tvMain, tvDetail, tvText1, tvFavoriteIt;
	String sMain, sDetail, sText1;
	int order;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_coupon);
		Intent intent = getIntent();
		index = intent.getStringExtra("idindex");
		try {
			currentTileObject = new JSONObject(
					intent.getStringExtra("idjsonobject"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		currentCoupon = getCoupon(index);
		SetLayout(index);

	}

	private void SetLayout(String index) {
		ivCoupon = (ImageView) findViewById(R.id.ivc);
		ivFavoriteIt = (ImageView) findViewById(R.id.ivFavor);
		loadBitmap(ivCoupon, Coupon.this, "coupon" + order + ".png", order,
				tilesjsonArray);
		tvFavoriteIt = (TextView) findViewById(R.id.tvFavor);
		FavoriteJsonArray = getFavoriteArray();
		if (!notAlreadyFavorited(index, FavoriteJsonArray)) {
			ivFavoriteIt.setImageResource(R.drawable.removefavbutton);
			tvFavoriteIt.setText("Remove Favorite");
		}
		String sMain = null, sDetail = null, sText1 = null;
		try {
			sMain = currentCoupon.getString("Main Discount");
			sDetail = currentCoupon.getString("Details");
			sText1 = currentCoupon.getString("Text1");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		tvMain = (TextView) findViewById(R.id.tvMainDis);
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		tvText1 = (TextView) findViewById(R.id.tvShare);
		tvMain.setText(sMain);
		tvDetail.setText(sDetail);
		tvText1.setText(sText1);

	}

	public void loadBitmap(ImageView bmImage, Context context, String picName,
			int i, JSONArray tilesjsonArray) {
		Bitmap b = null;
		InputStream is;
		try {
			is = openFileInput(picName);
			b = BitmapFactory.decodeStream(is);
			is.close();
		} catch (FileNotFoundException e) {
			try {
				new saveDownloadImageTask(bmImage, i, picName)
						.execute(currentCoupon.getString("Image"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		} catch (IOException e) {
			Log.v("error", "io exception");
			e.printStackTrace();
		}
		bmImage.setImageBitmap(b);
		bmImage.setVisibility(View.VISIBLE);
	}

	public class saveDownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		int index;
		String picname;

		public saveDownloadImageTask(ImageView bmImage, int i, String picname) {
			this.bmImage = bmImage;
			this.index = i;
			this.picname = picname;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
			FileOutputStream fos;
			try {
				fos = Coupon.this.openFileOutput(picname, Context.MODE_PRIVATE);
				result.compress(CompressFormat.PNG, 100, fos);
				try {
					fos.close();
				} catch (IOException e) {
					Toast.makeText(Coupon.this,
							"please connect to the internet",
							Toast.LENGTH_SHORT).show();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	@SuppressWarnings("resource")
	private JSONObject getCoupon(String key) {
		InputStream is = null;
		try {
			is = openFileInput("coupons.txt");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String result = null;
		try {
			while ((line = bReader.readLine()) != null) {
				stringBuilder.append(line);
			}
			result = stringBuilder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(result);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject currentObject = (JSONObject) jsonArray.get(i);
				if (key.equals(currentObject.getString("Image"))) {
					order = i;
					return currentObject;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void favoriteIt(View view) {
		FavoriteJsonArray = getFavoriteArray();
		if (notAlreadyFavorited(index, FavoriteJsonArray)) {
			FavoriteJsonArray = addFavorite(currentTileObject,
					FavoriteJsonArray);
			ivFavoriteIt.setImageResource(R.drawable.removefavbutton);
			tvFavoriteIt.setText("Remove Favorite");
		} else {
			FavoriteJsonArray = removeFavorite(currentTileObject,
					FavoriteJsonArray);
			ivFavoriteIt.setImageResource(R.drawable.favouritebutton);
			tvFavoriteIt.setText("Add To Favorite!");
		}
		String sNewFavorite = FavoriteJsonArray.toString();
		writeToFile(sNewFavorite);

	}

	private JSONArray getFavoriteArray() {
		InputStream is = null;
		try {
			is = openFileInput("favorite.txt");
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			FileOutputStream fs = null;
			try {
				fs = openFileOutput("favorite.txt", Context.MODE_PRIVATE);
				fs.write("[]".getBytes());
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			is = openFileInput("favorite.txt");
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
			return jsonArray;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean notAlreadyFavorited(String index, JSONArray favoriteArray) {
		for (int i = 0; i < favoriteArray.length(); i++) {
			JSONObject temJsonObject;
			try {
				temJsonObject = (JSONObject) favoriteArray.get(i);
				if (index.equals(temJsonObject.getString("Link").split("-")[1])) {
					return false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private JSONArray addFavorite(JSONObject currentTile,
			JSONArray favoriteArray) {
		favoriteArray.put(currentTile);
		Toast.makeText(Coupon.this, "Add To Favorite", Toast.LENGTH_SHORT)
				.show();
		return favoriteArray;
	}

	private JSONArray removeFavorite(JSONObject currentTile,
			JSONArray favoriteArray) {
		JSONArray newFavoriteArray = new JSONArray();
		for (int i = 0; i < favoriteArray.length(); i++) {
			JSONObject temJsonObject;
			try {
				temJsonObject = (JSONObject) favoriteArray.get(i);
				if (!currentTile.getString("Order").equals(
						temJsonObject.getString("Order"))) {
					newFavoriteArray.put(temJsonObject);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Toast.makeText(Coupon.this, "Remove Favorite", Toast.LENGTH_SHORT)
				.show();
		return newFavoriteArray;
	}

	private void writeToFile(String sFavorite) {
		FileOutputStream fs = null;
		try {
			fs = openFileOutput("favorite.txt", Context.MODE_PRIVATE);
			fs.write(sFavorite.getBytes());
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void back(View view) {
		finish();
	}

	public void sharePhoto(View view) {
		Intent iShare = new Intent("com.example.deals.Share");
		iShare.putExtra("idjsonobject", currentCoupon.toString());
		iShare.putExtra("idimage", image);
		startActivity(iShare);
	}

}
