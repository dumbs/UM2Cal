package com.dumbs.um2cal.models;

import java.util.Date;


public class Lesson {
	private int id;
	private int model;
	private boolean allDay;
	private boolean editable;
	private boolean readOnly;
	private String description;
	private String location;
	private String title;
	private String type;
	private String group;
	private Date end;
	private Date start;
	
	public Lesson() {
		this.id = -1;
		this.model = -1;
		this.allDay = false;
		this.editable = false;
		this.readOnly = false;
		this.description = null;
		this.location = null;
		this.title = null;
		this.type = null;
		this.group = null;
		this.end = null;
		this.start = null;
	}
	
	public Lesson(int id, boolean allDay, boolean editable, boolean readOnly,
			int model, String description, String location, String title,
			String type, String group, Date end, Date start) {
		super();
		this.id = id;
		this.allDay = allDay;
		this.editable = editable;
		this.readOnly = readOnly;
		this.model = model;
		this.description = description;
		this.location = location;
		this.title = title;
		this.type = type;
		this.group = group;
		this.end = end;
		this.start = start;
		
		this.formatTitle();
	}

	// TODO : A remplacer tous ca par des Expression reguliere.
	private void formatTitle() {
		int index;
		
		if ((index = title.indexOf(" [")) != -1) {
			int index2 = title.indexOf("]");
			this.location = title.substring(index + 2, index2);
			this.title = title.substring(0, index); 
		}
		if ((index = title.indexOf(" dans ")) != -1) {
			this.title = title.substring(0, index);
		}
		if ((index = title.indexOf(" gr. ")) != -1) {
			int indexForGroup = index;
			indexForGroup += 1;
			this.group = title.substring(index, indexForGroup);
			this.title = title.substring(0, index);
		}
		index = 0;
		this.type = title.substring(index, 2);
		// TODO : A changer car vraiment pas beau :(
		this.title = title.substring(3).replaceAll("&amp;", "&");
	}
	
	@Override
	public String toString() {
		return "Cours [id=" + id + ", allDay=" + allDay + ", editable="
				+ editable + ", readOnly=" + readOnly + ", model=" + model
				+ ", description=" + description + ", location=" + location
				+ ", title=" + title + ", type=" + type + ", group=" + group
				+ ", end=" + end + ", start=" + start + "]";
	}

	public int getId() {
		return id;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public boolean isEditable() {
		return editable;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public int isModel() {
		return model;
	}
	public String getDescription() {
		return description;
	}
	public String getLocation() {
		return location;
	}
	public String getTitle() {
		return title;
	}
	public String getType() {
		return type;
	}
	public String getGroup() {
		return group;
	}
	public Date getEnd() {
		return end;
	}
	public Date getStart() {
		return start;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public void setModel(int model) {
		this.model = model;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	
}
