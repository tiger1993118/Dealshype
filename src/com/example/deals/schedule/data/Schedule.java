package com.example.deals.schedule.data;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Schedule {
	private ArrayList<Day> days;
	
	public Schedule(JSONArray schedule){
		days = new ArrayList<Day>();
		
		try {
			setDays(schedule);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public int getDaysCount(){
		return days.size();
	}

	public Day getDay(int i) {
		return days.get(i);
	}

	public void setDays(JSONArray schedule) throws JSONException {
		for(int i = 0; i < schedule.length(); i++){
			JSONObject tmpObj = (JSONObject) schedule.get(i);
			Day tmpDay = new Day(tmpObj);
			days.add(tmpDay);
		}
	}
}
