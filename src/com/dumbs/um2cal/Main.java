package com.dumbs.um2cal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import com.dumbs.um2cal.models.Constant;
import com.dumbs.um2cal.models.Course;
import com.dumbs.um2cal.models.Courses;

// TODO : Add the detailed view, of courses

public class Main extends ListActivity {


	public static final int RELOAD = Menu.FIRST + 0x01;
	public static final int DATE = Menu.FIRST + 0x02;
	public static final int TO_DAY = Menu.FIRST + 0x03;
	public static final int STEPS = Menu.FIRST + 0x04;

	public static final int PARAM_CODE = 0x01;
	static final int DATE_DIALOG_ID = 0x00;

	private Calendar cal;
	private ProgressDialog dialog;
	private Courses courses;
	private Thread background;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (dialog.isShowing())
				dialog.dismiss();

			reloadData();
		}
	};
	
	private DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			
			dialog.show();

			SharedPreferences mPrefs = getSharedPreferences(Constant.APP_NAME,MODE_PRIVATE);
			courses = new Courses(mPrefs.getInt(Constant.PROGRAM, -1));
			courses.setDate(year, monthOfYear, dayOfMonth);

			cal = courses.getCalendar();
			
			Thread th = new Thread(new Runnable() {
				
				@Override
				public void run() {
					completeCourses();
					handler.sendMessage(handler.obtainMessage());					
				}
			});
			th.start();
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);		
		if (cm != null && (cm.getActiveNetworkInfo()==null || !cm.getActiveNetworkInfo().isConnected())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("UM2Cal")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMessage("Problème de connexion.\nVeuillez vous connecter...")
			.setCancelable(false)
			.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {		
			SharedPreferences mPrefs = getSharedPreferences(Constant.APP_NAME,MODE_PRIVATE);
			int program = mPrefs.getInt(Constant.PROGRAM, -1);
			if (program == -1) {
				startActivityForResult(new Intent(this, ProgramActivity.class), PARAM_CODE);
			} else {
				startCollectCourses(program);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PARAM_CODE) {
			if (resultCode == RESULT_OK) {
				startCollectCourses(data.getExtras().getInt(Constant.PROGRAM));
			}
		}
		if (requestCode == SearchableActivity.SEARCH_CODE) {
			if (resultCode == RESULT_OK) {
				startCollectCourses(data.getExtras().getInt(Constant.PROGRAM));
			}
		}
	}

	private void startCollectCourses(int program) {
		dialog = ProgressDialog.show(this, "", 
				"Chargement de la liste des cours. Veuillez attendre...", true);

		courses = new Courses(program);
		cal = courses.getCalendar();

		background = new Thread(new Runnable() {

			@Override
			public void run() {
				completeCourses();
				handler.sendMessage(handler.obtainMessage());
			}
		});
		
		background.start();
	}

	private void completeCourses() {
		try {
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
	}

	private void setAdapter() {
		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);

		int dayOfWeek = Calendar.SUNDAY;
		// TODO : For test uncomment on the following
		//c = new GregorianCalendar(2010,0,24);
		while (!courses.getCourses().isEmpty() && dayOfWeek++ != Calendar.SATURDAY) {
			cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			adapter.addSection(new SimpleDateFormat("EEEE, dd MMMM").format(cal.getTime()),
					new CourseAdapter(this, courses.getCourses(dayOfWeek)));
		}
		cal = null;

		setListAdapter(adapter);
	}

	public void reloadData() {
		if (courses.getCourses().isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("UM2Cal")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMessage("Nous n'avons pas trouvé de cours pour cette semaine.")
			.setCancelable(false)
			.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			setAdapter();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return (super.onCreateOptionsMenu(menu));
	}

	private void populateMenu(Menu menu) {
		menu.add(Menu.NONE, RELOAD, Menu.NONE, "Actualiser").setIcon(R.drawable.ic_menu_refresh);
		menu.add(Menu.NONE, DATE, Menu.NONE, "Allez à la date").setIcon(android.R.drawable.ic_menu_week);
		menu.add(Menu.NONE, TO_DAY, Menu.NONE, "Aujourd'hui").setIcon(android.R.drawable.ic_menu_today);
		menu.add(Menu.NONE, STEPS, Menu.NONE, "Parcours").setIcon(android.R.drawable.ic_menu_manage);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (applyMenuChoice(item) || super.onOptionsItemSelected(item));
	}


	private boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case STEPS:
			startActivityForResult(new Intent(this, ProgramActivity.class), PARAM_CODE);
			return (true);
		case RELOAD:
			startCollectCourses(getSharedPreferences(Constant.APP_NAME,MODE_PRIVATE).getInt(Constant.PROGRAM, -1));
			return (true);
		case DATE:
			showDialog(DATE_DIALOG_ID);
			return (true);
		case TO_DAY:
			courses.setDateToDay();

			SharedPreferences mPrefs = getSharedPreferences(Constant.APP_NAME,MODE_PRIVATE);
			startCollectCourses(mPrefs.getInt(Constant.PROGRAM, -1));
			return (true);
		default:
			return (false);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
	    case DATE_DIALOG_ID:
	    	Calendar date = Calendar.getInstance();
	        return new DatePickerDialog(this,
	        		d,
	        		date.get(Calendar.YEAR),
	        		date.get(Calendar.MONTH),
	        		date.get(Calendar.DAY_OF_MONTH));
	    }
	    return null;
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