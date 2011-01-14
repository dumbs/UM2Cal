package com.dumbs.um2cal.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.gson.stream.JsonReader;

// TODO : Ajouter une methode pour recupere les cours d'un certain jour.

public class Courses {
	private List<Course> courses = new ArrayList<Course>();

	public Courses (JsonReader reader) throws IOException {
		reader.beginArray();
		while (reader.hasNext()) {
			courses.add(readCourse(reader));
		}
		reader.endArray();
	}

	private Course readCourse(JsonReader reader) throws IOException {
		int id = -1;
		int model = -1;
		boolean allDay = false;
		boolean editable = false;
		boolean readOnly = false;
		String description = null;
		String location = null;
		String title = null;
		String type = null;
		String group = null;
		Calendar end = new GregorianCalendar();
		Calendar start = new GregorianCalendar();
		
		int year = 0;
		int month = 0;
		int day = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = reader.nextInt();
			} else if (name.equals("model")) {
				model = reader.nextInt();
			} else if (name.equals("allDay")) {
				allDay = reader.nextBoolean();
			} else if (name.equals("editable")) {
				editable = reader.nextBoolean();
			} else if (name.equals("readOnly")) {
				readOnly = reader.nextBoolean();
			} else if (name.equals("description")) {
				description = reader.nextString();
			} else if (name.equals("location")) {
				location = reader.nextString();
			} else if (name.equals("title")) {
				title = reader.nextString();
			} else if (name.equals("type")) {
				type = reader.nextString();
			} else if (name.equals("group")) {
				group = reader.nextString();
			} else if (name.equals("end")) {
				String endS = reader.nextString();
				year = Integer.valueOf(endS.substring(0, 4));
				month = Integer.valueOf(endS.substring(5, 7)) - 1;
				day = Integer.valueOf(endS.substring(8, 10));
				hour = Integer.valueOf(endS.substring(11, 13));
				minute = Integer.valueOf(endS.substring(14, 16));
				second = Integer.valueOf(endS.substring(17, 19));
				end = new GregorianCalendar(year, month, day, hour, minute, second);
			} else if (name.equals("start")) {
				String startS = reader.nextString();
				year = Integer.valueOf(startS.substring(0, 4));
				month = Integer.valueOf(startS.substring(5, 7)) - 1;
				day = Integer.valueOf(startS.substring(8, 10));
				hour = Integer.valueOf(startS.substring(11, 13));
				minute = Integer.valueOf(startS.substring(14, 16));
				second = Integer.valueOf(startS.substring(17, 19));
				start = new GregorianCalendar(year, month, day, hour, minute, second);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new Course(id, allDay, editable, readOnly, model, description, location, title, type, group, end, start);
	}


	public List<Course> getCourses() {
		return courses;
	}


	/**
	 * @param dayOfWeek An integer which represente a day of week. It's between 0 and 6
	 * @return a list of all lesson which takes place on day of week.
	 */
	public List<Course> getCourses(int dayOfWeek) {
		List<Course> result = new ArrayList<Course>();

		for (Course c : courses) {
			if (c.getStart().get(Calendar.DAY_OF_WEEK) == dayOfWeek)
				result.add(c);
		}
		return (result);
	}

	@Override
	public String toString() {
		return "Courses [courses=" + courses + "]";
	}

}