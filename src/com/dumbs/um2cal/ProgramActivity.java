package com.dumbs.um2cal;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dumbs.um2cal.models.Program;
import com.dumbs.um2cal.models.Programs;

public class ProgramActivity extends ListActivity {

	private Programs programs;
	private ProgramsAdapter adapter;
	private ProgressDialog dialog;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			reloadData();
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dialog = ProgressDialog.show(this, "", 
				"Chargement de la liste des cours. Veuillez attendre...", true);
		
		programs = new Programs();
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				programs.completePrograms();
				handler.sendMessage(handler.obtainMessage());
			}
		});
		th.start();
		adapter = new ProgramsAdapter(this, programs.getPrograms());
	}
	
	

	public void reloadData() {
		adapter = new ProgramsAdapter(this, programs.getPrograms());
		setListAdapter(adapter);
		dialog.dismiss();
	}
	
	private class ViewWrapper {
		View base;

		TextView program = null;
		TextView group = null;

		ViewWrapper (View base) {
			this.base = base;
		}

		TextView getStep() {
			if (program == null) {
				program = (TextView) base.findViewById(R.id.step);
			}
			return (program);
		}

		TextView getGroup() {
			if (group == null) {
				group = (TextView) base.findViewById(R.id.group);
			}
			return (group);
		}
	}

	private class ProgramsAdapter extends ArrayAdapter<Program> {
		Activity context;

		public ProgramsAdapter(Activity context, Program[] programs) {
			super(context, R.layout.steprow, programs);
			
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;
			ViewWrapper wrapper = null;

			if (row == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				row = inflater.inflate(R.layout.steprow, null);
				wrapper = new ViewWrapper(row);
				row.setTag(wrapper);
			} else {
				wrapper = (ViewWrapper) row.getTag();
			}

			wrapper.getStep().setText(getItem(position).getName());
			wrapper.getGroup().setText("Groupe : Tous");

			return (row);
		}
	}

}
