package com.dumbs.um2cal;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dialog = ProgressDialog.show(this, "", 
				"Chargement de la liste des cours. Veuillez attendre...", true);

		programs = new Programs();

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				completePrograms();
				if (dialog.isShowing())
					dialog.dismiss();
			}
		});

		adapter = new ProgramsAdapter(this, programs.getPrograms());
		getListView().setOnItemClickListener(this);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Program p = (Program)arg0.getItemAtPosition(arg2);
		
		// we set in preferences, the element we have selected
		SharedPreferences.Editor ed = getSharedPreferences(Constant.app_name, MODE_PRIVATE).edit();
		ed.putInt(Constant.program, p.getId());
		ed.commit();
		
		this.setResult(RESULT_OK, new Intent().putExtra(Constant.program, p.getId()));
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
		if (dialog.isShowing()) 
			dialog.dismiss();
	}
	
	@Override
	public boolean onSearchRequested() {
		System.out.println("Search...");
		startSearch(null, false, null, false);
		return super.onSearchRequested();
	}

	private void completePrograms() {
		try {
			programs.completePrograms();
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
