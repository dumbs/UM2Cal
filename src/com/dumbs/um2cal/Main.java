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

import com.dumbs.um2cal.models.Course;
import com.dumbs.um2cal.models.Courses;
import com.google.gson.stream.JsonReader;

public class Main extends ListActivity {
	
	private Courses courses;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		try {
			//Create the different calendar we need
			Calendar monday = new GregorianCalendar();
			Calendar saturday = new GregorianCalendar();
			
			//Set the calendars day of week to Monday and Saturday
			monday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			saturday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			
			//For test uncomment on the following
			monday = new GregorianCalendar(2010,11,13);
			saturday = new GregorianCalendar(2010,11,13 + 6);
			
			//Format the URL used to get information about the courses.
			String urlS = String.format("http://edt.ufr.univ-montp2.fr/php/ajax/OccList.php?start=%1$tY-%1$tm-%1$te&end=%2$tY-%2$tm-%2$te&sel=Etp%%3A+124%%2C160", monday, saturday);
			URL url = new URL(urlS);
			//Makes an JsonReader and set the json code got from URL. 
			JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), "UTF-8"));
			//Transforms the json code in an instance of Lessons.
			courses = new Courses(reader);
			reader.close();
			
			monday = null;
			saturday = null;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		
		int dayOfWeek = Calendar.SUNDAY;
		Calendar c = new GregorianCalendar();
		// For test uncomment on the following
		c = new GregorianCalendar(2010,11,13);
		while (!courses.getCourses().isEmpty() && dayOfWeek++ != Calendar.SATURDAY) {
			c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			adapter.addSection(new SimpleDateFormat("EEEE, dd MMMM").format(c.getTime()),
					new CourseAdapter(this, courses.getCourses(dayOfWeek)));
		}
		
		c = null;
		
		setListAdapter(adapter);
	}
	
	private class ViewWrapper {
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
	
	private class CourseAdapter extends ArrayAdapter<Course> {
		
		Activity context;
		
		public CourseAdapter(Activity context, List<Course> lessons) {
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