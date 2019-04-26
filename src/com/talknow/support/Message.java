package com.talknow.support;

import java.io.Serializable;
import java.util.LinkedList;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Schema schema = null;
	private String info = null;
	
	private String sender = null;
	private String getter = null;
	
	private LinkedList<String> list = null;
	
	private int code = -1;
	
	public Message(Schema schema,String sender,String getter,String info) {
		this.schema = schema;
		this.sender = sender;
		this.getter = getter;
		this.info = info;
	}
	
	public Message(String responseInfo,int responseCode) {
		this.schema = Schema.response;
		this.info = responseInfo;
		this.code = responseCode;
	}
	
	public Message(LinkedList<String> list) {
		this.schema = Schema.onlinelist;
		this.list = list;
	}
	
	public String getInfo() {
		return info;
	}
	
	public Schema getSchema() {
		return schema;
	}

	public String getSender() {
		return sender;
	}
	
	public String getGetter() {
		return getter;
	}
	
	public int getCode() {
		return code;
	}
	
	public LinkedList<String> getList() {
		return list;
	}
	
}
