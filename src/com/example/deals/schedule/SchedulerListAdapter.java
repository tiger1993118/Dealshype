package com.example.deals.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.deals.R;
import com.example.deals.schedule.data.Day;

public class SchedulerListAdapter extends BaseAdapter{
	private Day mDay;
	private LayoutInflater mInflater;
	private HashMap<String, Boolean> map;

	public SchedulerListAdapter(Day day, Activity act){
		mDay = day;
		mInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		JSONArray jArr;
		map = new HashMap<String, Boolean>();
		try {
			jArr = new JSONArray(readJson(act));
			for(int i = 0 ; i < jArr.length() ; i++){
				JSONObject jTmp = (JSONObject) jArr.get(i);
				String title = jTmp.getString("title");
				map.put(title, true);
			}
		} catch (Exception e) {	}
		
		for(int i = 0 ; i < mDay.getEventsCount() ; i++){
			if(map.get(mDay.getEvent(i).getTitle()) != null )
				mDay.getEvent(i).setHighlighted(true);
		}
	}

	@Override
	public int getCount() {
		return mDay.getEventsCount();
	}

	@Override
	public Object getItem(int position) {
		return mDay.getEvent(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.item_list_event, parent, false);
			viewHolder = new ViewHolder();

			viewHolder.layoutHighlight = (LinearLayout) convertView.findViewById(R.id.highlight);
			viewHolder.tvHeader = (TextView) convertView.findViewById(R.id.tvHeader_event);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle_event);
			viewHolder.tvStartTime = (TextView) convertView.findViewById(R.id.tvStartTime_event);
			viewHolder.tvEndTime = (TextView) convertView.findViewById(R.id.tvEndTime_event);
			
			convertView.setTag(viewHolder);
		}else
			viewHolder = (ViewHolder) convertView.getTag();
			
		if(position == 0 || !mDay.getEvent(position).getTimeStart().equals(mDay.getEvent(position-1).getTimeStart())){
			viewHolder.tvHeader.setVisibility(View.VISIBLE);
			viewHolder.tvHeader.setText(mDay.getEvent(position).getTimeStart());
		}
		
		int color = ( (mDay.getEvent(position).isHighlighted() == true) ? Color.RED : Color.GRAY );
		viewHolder.layoutHighlight.setBackgroundColor(color);
			
		viewHolder.tvTitle.setText(mDay.getEvent(position).getTitle());
		viewHolder.tvStartTime.setText(mDay.getEvent(position).getTimeStart());
		viewHolder.tvEndTime.setText(mDay.getEvent(position).getTimeEnd());
		
		return convertView;
	}
	
	static class ViewHolder{
		LinearLayout layoutHighlight;
		TextView tvHeader;
		TextView tvTitle;
		TextView tvStartTime;
		TextView tvEndTime;
	}
	
	private String readJson(Activity act) throws IOException{
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		try{
			is = act.openFileInput("highlight.txt");
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			sb = new StringBuilder();
			String line;
		
			line = br.readLine();
			while(line != null){
				sb.append(line);
				line = br.readLine();
			}
		}finally{
			if(br != null) br.close();
			if(isr != null) isr.close();
			if(is != null) is.close();
		}
		
		return sb.toString();
	}
}
