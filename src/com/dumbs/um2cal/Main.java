package com.dumbs.um2cal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dumbs.um2cal.models.Lesson;
import com.dumbs.um2cal.models.Lessons;
import com.google.gson.stream.JsonReader;

public class Main extends ListActivity {
	
	private Lessons lessons;
	Calendar rightNow;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		try {
			rightNow = new GregorianCalendar(2010,11,13);
			System.out.println(rightNow.getTime());
			Calendar end = new GregorianCalendar();
			end.set(Calendar.DAY_OF_MONTH, end.get(Calendar.DAY_OF_MONTH) + 7);
			//"http://edt.ufr.univ-montp2.fr/php/ajax/OccList.php?start=2010-12-13&end=2010-12-20&sel=Etp%3A+124"
			
			
			String urlS = String.format("http://edt.ufr.univ-montp2.fr/php/ajax/OccList.php?start=%1$tY-%1$tm-%1$te&end=%2$tY-%2$tm-%2$te&sel=Etp%%3A+124", rightNow, end);
			System.out.println(urlS);
			URL url = new URL("http://edt.ufr.univ-montp2.fr/php/ajax/OccList.php?start=2010-12-13&end=2010-12-20&sel=Etp%3A+124%%2C160");
			JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), "UTF-8"));
			lessons = new Lessons(reader);
			reader.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		
		int index = 1;
		while (!lessons.getLessons().isEmpty() && index++ <= 7) {
			System.out.println(new SimpleDateFormat("E").format(rightNow.getTime()));
			adapter.addSection(new SimpleDateFormat("E").format(rightNow.getTime()),
					new LessonAdapter(this, lessons.getLessonsForDay(rightNow.get(Calendar.DAY_OF_WEEK))));

			for (Lesson l : lessons.getLessonsForDay(rightNow.get(Calendar.DAY_OF_WEEK))) {
				System.out.println(l);
			}
			
			rightNow.set(Calendar.DAY_OF_WEEK, rightNow.get(Calendar.DAY_OF_WEEK) + 1);
		}
		
		setListAdapter(adapter);
	}
	
	class ViewWrapper {
		View base;

		TextView start = null;
		TextView end = null;
		TextView cours = null;
		TextView type = null;
		
		ViewWrapper (View base) {
			this.base = base;
		}
		
		TextView getStart() {
			if (start == null) {
				start = (TextView) base.findViewById(R.id.start);
			}
			return (start);
		}
		
		TextView getEnd() {
			if (end == null) {
				end = (TextView) base.findViewById(R.id.end);
			}
			return (end);
		}
		
		TextView getType() {
			if (type == null) {
				type = (TextView) base.findViewById(R.id.type);
			}
			return (type);
		}
		
		TextView getCours() {
			if (cours == null) {
				cours = (TextView) base.findViewById(R.id.cours);
			}
			return (cours);
		}
	}
	
	class LessonAdapter extends ArrayAdapter<Lesson> {
		
		Activity context;
		
		public LessonAdapter(Activity context, List<Lesson> lessons) {
			super(context, R.layout.row, lessons);
			
			this.context = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View row = convertView;
			ViewWrapper wrapper = null;
			
			if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				row = inflater.inflate(R.layout.row, null);
				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) row.getTag();
			}
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
			wrapper.getStart().setText(simpleDateFormat.format(getItem(position).getStart().getTime()));
			wrapper.getEnd().setText(simpleDateFormat.format(getItem(position).getEnd().getTime()));
			wrapper.getCours().setText(getItem(position).getTitle());
			wrapper.getType().setText(getItem(position).getType());
			
			return (row);
		}
		
	}


}