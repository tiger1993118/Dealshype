package com.example.deals.schedule;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.example.deals.R;
import com.example.deals.schedule.data.Day;
import com.example.deals.schedule.data.Event;

public class DayFragment extends Fragment implements OnItemClickListener,
OnItemLongClickListener{
	private Day day;
	private SchedulerListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_day, container, false);
		day = getArguments().getParcelable("day");

		adapter = new SchedulerListAdapter(day, getActivity());
		ListView lvEvents = (ListView) rootView.findViewById(R.id.lvEvents);
		lvEvents.setOnItemClickListener(this);
		lvEvents.setOnItemLongClickListener(this);
		lvEvents.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		return rootView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		Log.d("FgmtDay", day.getEvent(pos).getDetail());
		Toast.makeText(getActivity(), "Test : " + day.getEvent(pos).getDetail(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		try {
			Event event = day.getEvent(arg2);
			boolean hasChanged = setHighlight(event.getTitle());
			
			if(hasChanged){
				Toast.makeText(getActivity(), "Test : Highlight", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity(), "Test : Highlight canceled", Toast.LENGTH_SHORT).show();
			}
			
			event.setHighlighted(hasChanged);
			adapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
		;
		return true;
	}

	// for test ; hardcoded arg
	private boolean setHighlight(String title) throws Exception {
		String tmp = String.format("{\"title\" : \"%s\"}", title);
		JSONArray jHighlights = null;
		
		try {
			String strHighlights = readJson();
			jHighlights = new JSONArray(strHighlights);
		} catch (FileNotFoundException fe) {
			jHighlights = new JSONArray();
		} catch (Exception e) {
			throw e;
		}
		
		boolean isUnselected = false;
		int indexUnselected = 0;
		for(int i = 0 ; i < jHighlights.length() ; i++){
			JSONObject obj = (JSONObject) jHighlights.get(i);
			if( obj.getString("title").equals(title) ){
//				jHighlights.remove(i); //only supported on API 19
				isUnselected = true;
				indexUnselected = i;
				break;
			}
		}
		
		if(isUnselected){
			JSONArray newJArr = new JSONArray();
			for(int i = 0 ; i < jHighlights.length() ; i++){
				if(i != indexUnselected)
					newJArr.put(jHighlights.get(i));
			}
			
			writeJson(newJArr);
			return false;
		}
		

		JSONObject jTmp = new JSONObject(tmp);
		jHighlights.put(jTmp);

		writeJson(jHighlights);
		return true;
	}

	private String readJson() throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			is = getActivity().openFileInput("highlight.txt");
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);

			sb = new StringBuilder();
			String line;

			line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
		} finally {
			if (br != null)
				br.close();
			if (isr != null)
				isr.close();
			if (is != null)
				is.close();
		}

		return sb.toString();
	}

	private void writeJson(JSONArray jArr) throws IOException {
		OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			os = getActivity().openFileOutput("highlight.txt",
					Context.MODE_PRIVATE);

			bos = new BufferedOutputStream(os);
			bos.write(jArr.toString().getBytes());
		} finally {
			if (bos != null)
				bos.close();
			if (os != null)
				os.close();
		}

	}
}
