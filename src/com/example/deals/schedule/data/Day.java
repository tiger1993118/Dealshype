package com.example.deals.schedule.data;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Parcel;
import android.os.Parcelable;

public class Day implements Parcelable{
	private String date;
	private ArrayList<Event> events;
	
	public Day(Parcel source){
		readFromParcel(source);
	}
	
	private void readFromParcel(Parcel source) {
		date = source.readString();
		ArrayList<Event> tmp = null;
		source.readList(tmp, null);
		events = tmp;
	}

	public Day(JSONObject day){

		try {
			setDate(day);
		} catch (JSONException e) {
			e.printStackTrace();
			date = "";
		}
		
		events = new ArrayList<Event>();
		try {
			setEvents(day);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void setDate(JSONObject day) throws JSONException{
		date = day.getString("date");
	}
	
	public String getDate(){
		return date;
	}

	private void setEvents(JSONObject day) throws JSONException{
		JSONArray jEvents = day.getJSONArray("events");
		for(int i = 0; i < jEvents.length() ; i++){
			JSONObject jvent = (JSONObject) jEvents.get(i);
			Event tmpEvent = new Event(jvent);
			events.add(tmpEvent);
		}
	}
	
	public int getEventsCount(){
		return events.size();
	}
	
	public Event getEvent(int index){
		return events.get(index);
	}

	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(date);
		dest.writeList(events);
	}
	
	public static final Parcelable.Creator<Day> CREATOR = new Creator<Day>() {
		
		@Override
		public Day[] newArray(int size) {
			return new Day[size];
		}
		
		@Override
		public Day createFromParcel(Parcel source) {
			return new Day(source);
		}
	};
}
