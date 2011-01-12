package com.dumbs.um2cal.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.stream.JsonReader;

public class Lessons {
	private List<Lesson> lessons = new ArrayList<Lesson>();
	
	public Lessons (JsonReader reader) throws IOException {
		reader.beginArray();
		while (reader.hasNext()) {
			lessons.add(readLesson(reader));
		}
		reader.endArray();
	}
	
	private Lesson readLesson(JsonReader reader) throws IOException {
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
		Date end = null;
		Date start = null;
		
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
	        	 int year = Integer.valueOf(endS.substring(0, 4));
	        	 int month = Integer.valueOf(endS.substring(5, 7));
	        	 int day = Integer.valueOf(endS.substring(8, 10));
	        	 int hour = Integer.valueOf(endS.substring(11, 13));
	        	 int minute = Integer.valueOf(endS.substring(14, 16));
	        	 int second = Integer.valueOf(endS.substring(17, 19));
	        	 end = new Date(year, month, day, hour, minute, second);
	         } else if (name.equals("start")) {
	        	 String endS = reader.nextString();
	        	 int year = Integer.valueOf(endS.substring(0, 4));
	        	 int month = Integer.valueOf(endS.substring(5, 7));
	        	 int day = Integer.valueOf(endS.substring(8, 10));
	        	 int hour = Integer.valueOf(endS.substring(11, 13));
	        	 int minute = Integer.valueOf(endS.substring(14, 16));
	        	 int second = Integer.valueOf(endS.substring(17, 19));
	        	 start = new Date(year, month, day, hour, minute, second);
	         } else {
	           reader.skipValue();
	         }
	     }
	     reader.endObject();
	     return new Lesson(id, allDay, editable, readOnly, model, description, location, title, type, group, end, start);
	}

	
	public List<Lesson> getLessons() {
		return lessons;
	}

	
	@Override
	public String toString() {
		return "Lessons [lessons=" + lessons + "]";
	}



}
