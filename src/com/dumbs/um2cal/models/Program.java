package com.dumbs.um2cal.models;


public class Program {
	
	private final static int CODE = 0x0;
	private final static int NAME = 0x1;
	private final static int COMPONENT = 0x2;
	
	private int id;
	private String[] cell;
	
	public Program() {
		this.cell = new String[3];
	}
	
	public Program(int id, String[] cell) {
		this.id = id;
		this.cell = new String[3];
	}

	public Program(int id, String code, String name, String component) {
		this.id = id;
		setCode(code);
		setName(name);
		setComponent(component);
	}

	@Override
	public String toString() {
		return "Career [id=" + id + ", code=" + getCode() + ", name="
				+ getName() + ", component=" + getComponent() + "]";
	}

	public String getCode() {
		return cell[CODE];
	}
	
	public String getName() {
		return cell[NAME];
	}
	
	public String getComponent() {
		return cell[COMPONENT];
	}

	public int getId() {
		return id;
	}

	public void setCode(String code) {
		this.cell[CODE] = code;
	}
	
	public void setName(String name) {
		this.cell[NAME] = name;
	}
	
	public void setComponent(String component) {
		this.cell[COMPONENT] = component;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	

}
