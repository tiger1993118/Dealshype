package com.example.deals.schedule.data;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable{
	private boolean isHighlighted;
	private String timeStart;
	private String timeEnd;
	private String title;
	private String detail;
	private String address;
	
	private JSONObject event;
	
	public Event(Parcel source){
		readFromParcel(source);
	}
	
	private void readFromParcel(Parcel source) {
		isHighlighted = ( source.readInt() == 1 ? true : false);
		timeStart = source.readString();
		timeEnd = source.readString();
		title = source.readString();
		detail = source.readString();
		address = source.readString();
	}

	public Event(JSONObject event){
		this.event = event;
		this.isHighlighted = false;
		setTimeStart();
		setTimeEnd();
		setTitle();
		setDetail();
		setAddress();
	}
	
	public boolean isHighlighted() {
		return isHighlighted;
	}

	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

	public String getTimeStart() {
		return timeStart;
	}
	
	private void setTimeStart( ) {
		String temp;
		try {
			temp = event.getString("time_s");
		} catch (JSONException e) {
			temp = "";
		}
		
		this.timeStart = temp;
	}
	
	public String getTimeEnd() {
		return timeEnd;
	}
	
	private void setTimeEnd( ) {
		String temp;
		try {
			temp = event.getString("time_e");
		} catch (JSONException e) {
			temp = "";
		}
		
		this.timeEnd = temp;
	}
	
	public String getTitle() {
		return title;
	}
	
	private void setTitle( ) {
		String temp;
		try {
			temp = event.getString("title");
		} catch (JSONException e) {
			temp = "";
		}
		
		this.title = temp;
	}
	
	public String getDetail() {
		return detail;
	}
	
	private void setDetail( ) {
		String temp;
		try {
			temp = event.getString("detail");
		} catch (JSONException e) {
			temp = "";
		}
		
		this.detail = temp;
	}
	
	public String getAddress() {
		return address;
	}
	
	private void setAddress( ) {
		String temp;
		try {
			temp = event.getString("address");
		} catch (JSONException e) {
			temp = "";
		}
		
		this.address = temp;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(isHighlighted ? 1 : 0 );
		dest.writeString(timeStart);
		dest.writeString(timeEnd);
		dest.writeString(title);
		dest.writeString(detail);
		dest.writeString(address);
	}
	
	public static final Parcelable.Creator<Event> CREATOR = new Creator<Event>() {
		
		@Override
		public Event[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Event[size];
		}
		
		@Override
		public Event createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Event(source);
		}
	};
	
}
