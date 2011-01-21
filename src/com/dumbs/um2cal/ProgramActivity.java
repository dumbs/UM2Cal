package com.dumbs.um2cal;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dumbs.um2cal.models.Constant;
import com.dumbs.um2cal.models.Program;
import com.dumbs.um2cal.models.Programs;

public class ProgramActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener {

	private Programs programs;
	private ProgramsAdapter adapter;
	private ProgressDialog dialog;
	private Thread background;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (dialog.isShowing()) 
				dialog.dismiss();

			reloadData();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		programs = new Programs();
		
		dialog = ProgressDialog.show(this, "", 
				"Chargement de la liste des cours. Veuillez attendre...", true);

		background = new Thread(new Runnable() {

			@Override
			public void run() {
				completePrograms();
				handler.sendMessage(handler.obtainMessage());
			}
		});

		adapter = new ProgramsAdapter(this, programs.getPrograms());
		getListView().setOnItemClickListener(this);
		
		background.start();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Program p = (Program)arg0.getItemAtPosition(arg2);
		
		// we set in preferences, the element we have selected
		SharedPreferences.Editor ed = getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE).edit();
		ed.putInt(Constant.PROGRAM, p.getId());
		ed.commit();
		
		this.setResult(RESULT_OK, new Intent().putExtra(Constant.PROGRAM, p.getId()));
		this.finish();
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		return false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		System.out.println("TOTO");
		return super.onContextItemSelected(item);
	}
	
	public void reloadData() {
		adapter = new ProgramsAdapter(this, programs.getPrograms());
		setListAdapter(adapter);
	}

	private void completePrograms() {
		try {
			programs.completePrograms();
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

	public class ProgramsAdapter extends ArrayAdapter<Program> {
		Activity context;

		public ProgramsAdapter(Activity context, Program[] programs) {
			super(context, R.layout.steprow, programs);

			this.context = context;
		}

		public ProgramsAdapter(Activity context, ArrayList<Program> programs) {
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
