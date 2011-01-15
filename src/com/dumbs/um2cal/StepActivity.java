package com.dumbs.um2cal;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dumbs.um2cal.models.Program;
import com.dumbs.um2cal.models.Programs;

public class StepActivity extends ListActivity {

	private Programs programs;
	private StepsAdapter adapter;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dialog = ProgressDialog.show(this, "", 
				"Chargement de la liste des cours. Veuillez attendre...", true);
		programs = Programs.getInstance(this);
		
		if (programs != null) {
			adapter = new StepsAdapter(this, programs.getSteps());
			setListAdapter(adapter);
		}
	}

	public void reloadData() {
		dialog.dismiss();
		adapter.notifyDataSetChanged();
	}
	
	private class ViewWrapper {
		View base;

		TextView step = null;
		TextView group = null;

		ViewWrapper (View base) {
			this.base = base;
		}

		TextView getStep() {
			if (step == null) {
				step = (TextView) base.findViewById(R.id.step);
			}
			return (step);
		}

		TextView getGroup() {
			if (group == null) {
				group = (TextView) base.findViewById(R.id.group);
			}
			return (group);
		}
	}

	private class StepsAdapter extends ArrayAdapter<Program> {
		Activity context;

		public StepsAdapter(Activity context, Program[] steps) {
			super(context, R.layout.steprow, steps);
			
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
