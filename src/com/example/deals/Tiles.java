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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Tiles extends Activity implements OnClickListener,
		OnEditorActionListener {

	ImageButton bFavor, bSearch;
	ImageView ivStore;
	EditText etSearch;
	static int[] idA = { R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6,
			R.id.t7, R.id.t8, R.id.t9, R.id.t10, R.id.t11, R.id.t12, R.id.t13,
			R.id.t14, R.id.t15, R.id.t16, R.id.t17, R.id.t18 };
	static JSONArray tilesjsonArray;
	static Map<String, JSONObject> mapKeyToTileObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tiles);
		setup();
		MyTimerTask myTask = new MyTimerTask();
		Timer myTimer = new Timer();
		myTimer.schedule(myTask, 0, 300000);
		setUpVariables();
	}

	private void setUpVariables() {
		bFavor = (ImageButton) findViewById(R.id.bFavor);
		bSearch = (ImageButton) findViewById(R.id.bSearch);
		ivStore = (ImageView) findViewById(R.id.ivStore);
		etSearch = (EditText) findViewById(R.id.etSearch);
		bFavor.setOnClickListener(this);
		bSearch.setOnClickListener(this);
		etSearch.setOnEditorActionListener(this);

	}

	private void setup() {
		try {
			InputStream is = null;
			is = openFileInput("tiles.txt");
			new BufferedReader(new InputStreamReader(is));
			getFiles(false);
			new checkVersion().execute();
		} catch (FileNotFoundException e2) {
			getFiles(true);
		}
	}

	class MyTimerTask extends TimerTask {
		public void run() {
			new checkVersion().execute();
		}
	}

	public class checkVersion extends AsyncTask<Void, Boolean, String> {

		@Override
		protected String doInBackground(Void... bool) {
			URLConnection url = null;
			try {
				url = new URL(
						"http://dealshype.azurewebsites.net/staticFiles/jsonVersion.txt")
						.openConnection();
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						url.getInputStream()));
				String sCurrVersion = null, line;
				while ((line = rd.readLine()) != null) {
					sCurrVersion = line;
					// imporve
				}
				return sCurrVersion;
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				return "0";
			}
			return null;

		}

		protected void onPostExecute(String sNewVersion) {

			String sOldVersion = null;
			InputStream is = null;
			int oldVersion;
			int newVersion = Integer.parseInt(sNewVersion);
			try {
				is = openFileInput("jsonVersion.txt");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				try {
					sOldVersion = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				oldVersion = Integer.parseInt(sOldVersion);
				if (newVersion > oldVersion) {
					getFiles(true);
				}
			} catch (FileNotFoundException e2) {
				try {
					FileOutputStream outputStream = openFileOutput(
							"jsonVersion.txt", Context.MODE_PRIVATE);
					outputStream.write(sNewVersion.getBytes());
					outputStream.close();
					is = openFileInput("jsonVersion.txt");
					getFiles(true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public class getFile extends AsyncTask<Void, Void, String> {
		String sUrl;
		String filename;

		protected getFile(String url, String filename) {
			this.sUrl = url;
			this.filename = filename;
		}

		@Override
		protected String doInBackground(Void... v) {
			URLConnection url = null;
			try {
				url = new URL(sUrl).openConnection();
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						url.getInputStream()));
				String line = null, s = null;
				StringBuilder sb = new StringBuilder();
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				s = sb.toString();
				return s;
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String s) {
			FileOutputStream fs = null;
			InputStream is = null;
			try {
				is = openFileInput(filename);
				is.close();
				fs = openFileOutput(filename, Context.MODE_PRIVATE);
				fs.write(s.getBytes());
				fs.close();
				tilesjsonArray = getTileJsonArray();
				setLayout(tilesjsonArray);
			} catch (FileNotFoundException e) {
				try {
					fs = openFileOutput(filename, Context.MODE_PRIVATE);
					try {
						fs.write(s.getBytes());
						fs.close();
						tilesjsonArray = getTileJsonArray();
						setLayout(tilesjsonArray);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public class saveDownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		String picName;

		public saveDownloadImageTask(ImageView bmImage, String picName) {
			this.bmImage = bmImage;
			this.picName = picName;
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
				fos = Tiles.this.openFileOutput(picName, Context.MODE_PRIVATE);
				result.compress(CompressFormat.PNG, 100, fos);
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}
	}

	private JSONArray getTileJsonArray() {
		InputStream is = null;
		try {
			is = openFileInput("tiles.txt");
			String result = convertIsToString(is);
			JSONArray tileJsonArray;
			try {
				tileJsonArray = new JSONArray(result);
				return tileJsonArray;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e2) {
			getFiles(true);
		}
		return null;
	}

	private void getFiles(Boolean b) {
		if (b) {
			new getFile(
					"http://dealshype.azurewebsites.net/staticFiles/tiles.txt",
					"tiles.txt").execute();
			new getFile(
					"http://dealshype.azurewebsites.net/staticFiles/coupons.txt",
					"coupons.txt").execute();
		} else {
			tilesjsonArray = getTileJsonArray();
			setLayout(tilesjsonArray);
		}
	}

	private void setLayout(JSONArray tilesjsonArray) {
		int i;
		for (i = 0; i < tilesjsonArray.length(); i++) {
			int index = i + 1;
			loadBitmap((ImageView) findViewById(idA[i]), Tiles.this, "tile"
					+ index + ".png", i, tilesjsonArray);
		}
		if (i < idA.length & (i % 2 == 1)) {
			((ImageView) findViewById(idA[i])).setVisibility(View.INVISIBLE);
		}
		for (i = i + 1; i < idA.length; i++) {
			((ImageView) findViewById(idA[i])).setVisibility(View.GONE);
		}
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
			JSONObject currentTileJson;
			try {
				currentTileJson = (JSONObject) tilesjsonArray.get(i);
				new saveDownloadImageTask((ImageView) findViewById(idA[i]),
						picName).execute(currentTileJson.getString("Image"));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bmImage.setImageBitmap(b);
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

	public Map<String, String> couponToTile() {
		Map<String, String> mapCouponToTile = new HashMap<String, String>();
		mapKeyToTileObject = new HashMap<String, JSONObject>();
		for (int i = 0; i < tilesjsonArray.length(); i++) {
			JSONObject currentTileJsonObject;
			try {
				currentTileJsonObject = (JSONObject) tilesjsonArray.get(i);
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

	public void toCoupon(View view) {
		int id = view.getId();
		for (int index = 0; index < idA.length; index++) {
			if (id == idA[index]) {
				try {
					JSONObject clickedTileJson = (JSONObject) tilesjsonArray
							.get(index);
					if (clickedTileJson.getString("Link").split("-")[0]
							.equals("coupon")) {
						Intent iCoupon = new Intent("com.example.deals.Coupon");
						iCoupon.putExtra("idindex",
								clickedTileJson.getString("Link").split("-")[1]);
						iCoupon.putExtra("idjsonobject",
								clickedTileJson.toString());
						startActivity(iCoupon);
					} else {
						Intent iWebView = new Intent(
								"com.example.deals.Browser");
						iWebView.putExtra("idurl",
								clickedTileJson.getString("Link").split("-")[1]);
						startActivity(iWebView);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		String keyWord = etSearch.getText().toString();
		Intent iTiles = new Intent("com.example.deals.Search");
		iTiles.putExtra("KeyWord", keyWord);
		startActivity(iTiles);
		return false;
	}

	public String convertIsToString(InputStream is) {
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

	public void onRestart() {
		super.onRestart();
		setContentView(R.layout.activity_tiles);
		getFiles(false);
		new checkVersion().execute();
		bFavor = (ImageButton) findViewById(R.id.bFavor);
		bSearch = (ImageButton) findViewById(R.id.bSearch);
		ivStore = (ImageView) findViewById(R.id.ivStore);
		etSearch = (EditText) findViewById(R.id.etSearch);
		bFavor.setOnClickListener(this);
		bSearch.setOnClickListener(this);
		etSearch.setOnEditorActionListener(this);
	}

}
