package com.dumbs.um2cal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dumbs.um2cal.models.Constant;
import com.dumbs.um2cal.models.Course;
import com.dumbs.um2cal.models.Courses;

public class Main extends ListActivity {


	public static final int STEPS = Menu.FIRST + 0x01;
	public static final int RELOAD = Menu.FIRST + 0x02;

	public static final int PARAM = 0x01;

	private ProgressDialog dialog;
	private Courses courses;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        int program = mPrefs.getInt(Constant.program, -1);
        if (program == -1) {
        	startActivityForResult(new Intent(this, ProgramActivity.class), PARAM);
        } else {
        	startCollectCourses(program);
        	setAdapter();
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PARAM) {
			if (resultCode == RESULT_OK) {
				startCollectCourses(data.getExtras().getInt(Constant.program));
			}
		}
	}
	
	private void startCollectCourses(int program) {
		dialog = ProgressDialog.show(this, "", 
				"Chargement de la liste des cours. Veuillez attendre...", true);

		courses = new Courses(program);
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				completeCourses();
				if (dialog.isShowing())
					dialog.dismiss();
			}

		});
	}

	private void completeCourses() {
		try {
			courses.completeCourses();
			reloadData();
		} catch (IOException e) {
			if (dialog.isShowing())
				dialog.dismiss();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("UM2Cal")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMessage("Un problème est survenu.\nNous n'avons pas pu récupérer la liste des cours.")
			.setCancelable(false)
			.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private void setAdapter() {
		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);

		int dayOfWeek = Calendar.SUNDAY;
		Calendar c = new GregorianCalendar();
		// TODO : For test uncomment on the following
		c = new GregorianCalendar(2010,11,13);
		while (!courses.getCourses().isEmpty() && dayOfWeek++ != Calendar.SATURDAY) {
			c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			adapter.addSection(new SimpleDateFormat("EEEE, dd MMMM").format(c.getTime()),
					new CourseAdapter(this, courses.getCourses(dayOfWeek)));
		}
		c = null;

		setListAdapter(adapter);
	}

	public void reloadData() {
		setAdapter();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return (super.onCreateOptionsMenu(menu));
	}

	private void populateMenu(Menu menu) {
		menu.add(Menu.NONE, STEPS, Menu.NONE, "Actualiser");
		menu.add(Menu.NONE, STEPS, Menu.NONE, "Parcours");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
	}

	
	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case STEPS:
			startActivityForResult(new Intent(this, ProgramActivity.class), PARAM);
			return (true);
		case RELOAD:
			try {
				//TODO : A revoir.
				courses.completeCourses();
			} catch (IOException e) {
				if (dialog.isShowing())
					dialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("UM2Cal")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage("Un problème est survenu.\nNous n'avons pas pu récupérer la liste des cours.")
				.setCancelable(false)
				.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			return (true);
		default:
			return (false);
		}
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