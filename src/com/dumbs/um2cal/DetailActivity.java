package com.dumbs.um2cal;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.dumbs.um2cal.models.Course;

public class DetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		Course course = null;
		if (getIntent().getExtras() != null) {
			course = (Course)getIntent().getExtras().get("course");
		}

		((TextView)findViewById(R.id.title)).setText(course.getTitle());
		((TextView)findViewById(R.id.room)).setText("dans " + course.getLocation());
			((TextView)findViewById(R.id.group)).setText(
					(course.getGroup() == null) ? "groupe : Tous" : course.getGroup()
			);
		((TextView)findViewById(R.id.description)).setText(course.getDescription());
			
		String hourly = String.format("de %tR à %tR", course.getStart(), course.getEnd());
		String date = "le " + new SimpleDateFormat("EEEE, dd MMMM yyyy").format(course.getStart().getTime());

		((TextView)findViewById(R.id.date)).setText(date);
		((TextView)findViewById(R.id.hourly)).setText(hourly);
		
	}
}
