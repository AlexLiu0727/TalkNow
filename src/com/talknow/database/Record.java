package com.talknow.database;

public class Record {
	
	private String id;
	private String passcode;
	private String name;
	private String style;
	
	public Record() {}
	
	public Record(Record r) {
		this.id = r.id;
		this.passcode = r.passcode;
		this.name = r.name;
		this.style = r.style;
	}
	
	public Record(String id,String passcode,String name,String style) {
		this.id = id;
		this.passcode = passcode;
		this.name = name;
		this.style = style;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String toString() {
		return "'"+id+"','"+passcode+"','"+name+"','"+style+"'";
	}
	
}
