package com.dumbs.um2cal.models;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Steps {
	private static Steps instance = null;
	
	private Step[] steps;

	private Steps() {
		//Transforms the json code in of instances Step.
		this.completeSteps();
	}
	
	public final synchronized static Steps getInstance() {
		if (instance == null) 
            instance = new Steps();
        return (instance);
	}

	public final synchronized static void reload() {
		instance.completeSteps();
	}
	
	public Step[] getSteps() {
		return steps;
	}
	

	/**
	 * Gets all the courses available at the Faculty of Montpellier in json format to the following address:
	 * http://edt.ufr.univ-montp2.fr/php/ajax/EtpGrid.php.
	 * The json recovered is converted into <code>ArrayList<Career></code> which contains all steps.
	 */
	private void completeSteps() {
		int page = 1;
		int total = 1;
		int nbRows = 200;
		Gson gson = new Gson();

		try {
			do {
				String urlS = String.format(Constant.edt+"EtpGrid.php?rows=%1$d&page=%2$d", nbRows, page);
				//Format the URL used to get steps.
				URL url = new URL(urlS);
				urlS = null;
				//Makes an JsonReader and set the json code got from URL. 
				JsonReader reader = new JsonReader(new InputStreamReader(url.openStream(), "UTF-8"));
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if (name.equals("total")) {
						total = reader.nextInt();
					} else if (name.equals("records")) {
						steps = new Step[reader.nextInt()];
					} else if (name.equals("rows")) {
						steps = gson.fromJson(reader, Step[].class);
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
				reader.close();
				reader = null;
			} while (++page <= total);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public String toString() {
		return "Steps [steps=" + Arrays.toString(steps) + "]";
	}

}
