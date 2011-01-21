package com.dumbs.um2cal;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.dumbs.um2cal.models.Constant;
import com.dumbs.um2cal.models.Program;
import com.dumbs.um2cal.models.Programs;

public class SearchableActivity extends ListActivity implements OnItemClickListener {

	public static final int SEARCH_CODE = 0x02;
	
	private Programs programs = new Programs();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		try {
			programs.completePrograms();

			Intent intent = getIntent();

			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				String query = intent.getStringExtra(SearchManager.QUERY);
				doMySearch(query);
			}
		} catch (IOException e) {
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
		getListView().setOnItemClickListener(this);

	}

	private void doMySearch(String query) {
		ArrayList<Program> progs = new ArrayList<Program>();

		for (Program p : programs.getPrograms()) {
			if (p.getName().contains(query)) {
				progs.add(p);
			}
		}
		
		setTitle(""+progs.size()+" résultats pour \""+query+"\"");

		ProgramActivity.ProgramsAdapter adapter = new ProgramActivity().new ProgramsAdapter(this, progs);
		this.setListAdapter(adapter);

		progs = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Program p = (Program) arg0.getItemAtPosition(arg2);
		
		// we set in preferences, the element we have selected
		SharedPreferences.Editor ed = getSharedPreferences(Constant.APP_NAME, MODE_PRIVATE).edit();
		ed.putInt(Constant.PROGRAM, p.getId());
		ed.commit();
		
		startActivity(new Intent(this, Main.class));
	}
}
