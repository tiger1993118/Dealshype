package com.example.deals.schedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.deals.R;
import com.example.deals.schedule.data.Schedule;

public class SchedulerActivity extends FragmentActivity {
	Schedule sch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scheduler);

		// read JSON
		String showObj = "";
		JSONArray jSchedule = null;
		try {
			showObj = readJson();
			jSchedule = new JSONArray(showObj);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// parse JSON and create instances
		if (jSchedule != null) {
			sch = new Schedule(jSchedule);

			final Spinner spDate = (Spinner) findViewById(R.id.spDate);
			ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(
					getApplicationContext(), R.layout.item_spinner);
			spAdapter.setDropDownViewResource(R.layout.item_spinner);
			spDate.setAdapter(spAdapter);
			for (int i = 0; i < sch.getDaysCount(); i++)
				spAdapter.add(sch.getDay(i).getDate());
			spAdapter.notifyDataSetChanged();

			FgmtPageAdapter adapter = new FgmtPageAdapter(
					getSupportFragmentManager());
			final ViewPager vp = (ViewPager) findViewById(R.id.viewPager);
			vp.setAdapter(adapter);
			vp.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					spDate.setSelection(arg0);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});

			spDate.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					vp.setCurrentItem(arg2);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}

			});
		} else {
			// error handling
		}
	}

	private String readJson() throws IOException {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			is = getResources().openRawResource(R.raw.show);
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

	private class FgmtPageAdapter extends FragmentPagerAdapter {
		DayFragment[] fgmtDays;
		int count;

		public FgmtPageAdapter(FragmentManager fm) {
			super(fm);

			count = sch.getDaysCount();
			fgmtDays = new DayFragment[count];

			for (int i = 0; i < count; i++) {
				Bundle arg = new Bundle();
				arg.putParcelable("day", sch.getDay(i));

				fgmtDays[i] = new DayFragment();
				fgmtDays[i].setArguments(arg);
			}
		}

		@Override
		public Fragment getItem(int arg0) {
				return fgmtDays[arg0];
		}

		@Override
		public int getCount() {
			return count;
		}
	}
}
