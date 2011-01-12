package com.dumbs.um2cal;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

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
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		try {
			URL url = new URL("http://edt.ufr.univ-montp2.fr/php/ajax/OccList.php?start=2010-12-13&end=2010-12-20&sel=Etp%3A+124");
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
		
		setListAdapter(new LessonAdapter(this));

	}

	private Lesson getModel(int position) {
		return (((LessonAdapter)getListAdapter()).getItem(position));
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
		
		public LessonAdapter(Activity context) {
			super(context, R.layout.row, lessons.getLessons());
			
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
			wrapper.getStart().setText(simpleDateFormat.format(getModel(position).getStart()));
			wrapper.getEnd().setText(simpleDateFormat.format(getModel(position).getEnd()));
			wrapper.getCours().setText(getModel(position).getTitle());
			wrapper.getType().setText(getModel(position).getType());
			
			return (row);
		}
		
	}


}