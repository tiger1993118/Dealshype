package com.example.deals.twitterfeed;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import twitter4j.Status;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.deals.R;

public class TwitterFeedListAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private List<Status> mTweets;
	private ArrayList<Bitmap> mBitmaps;
	
	@SuppressWarnings("unchecked")
	public TwitterFeedListAdapter(List<Status> tweets, Activity act){
		mTweets = tweets;
		mInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mBitmaps = new ArrayList<Bitmap>();
		new DownloadProfileImg(mBitmaps).execute(mTweets);
	}

	@Override
	public int getCount() {
		return mTweets.size();
	}

	@Override
	public Object getItem(int position) {
		return mTweets.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.item_list_tweet, parent, false);
			viewHolder = new ViewHolder();
			
			viewHolder.ivUserImg = (ImageView) convertView.findViewById(R.id.ivTwitterUserImg);
			viewHolder.tvUser = (TextView) convertView.findViewById(R.id.tvTwitterUser);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvTwitterDate);
			viewHolder.tvTweet = (TextView) convertView.findViewById(R.id.tvTwitterTweet);
			
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder)convertView.getTag();
		
		viewHolder.tvUser.setText(mTweets.get(position).getUser().getName());
		String date = new SimpleDateFormat("dd/MMM/yy HH:mm", Locale.CANADA).format(mTweets.get(position).getCreatedAt());
		viewHolder.tvDate.setText(date);
		viewHolder.tvTweet.setText(mTweets.get(position).getText());
		if(mBitmaps.size() > position)
		viewHolder.ivUserImg.setImageBitmap(mBitmaps.get(position));
		
		return convertView;
	}
	
	private class DownloadProfileImg extends AsyncTask<List<Status>, Void, Void>{
		private ArrayList<Bitmap> bitmaps;
		
		public DownloadProfileImg(ArrayList<Bitmap> bitmaps){
			this.bitmaps = bitmaps;
		}

		@Override
		protected Void doInBackground(List<twitter4j.Status>... params) {
			Bitmap userImg = null;
			InputStream is = null;
			try {
				for(twitter4j.Status status : params[0]){
					is = new URL(status.getUser().getProfileImageURL()).openStream();
					userImg = BitmapFactory.decodeStream(is);
					bitmaps.add(userImg);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				try {
					if(is != null) is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(bitmaps != null)
				notifyDataSetChanged();
		}
	}

	static class ViewHolder{
		ImageView ivUserImg;
		TextView tvUser;
		TextView tvDate;
		TextView tvTweet;
	}
}
