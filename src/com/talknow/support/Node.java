package com.talknow.support;

public class Node extends com.talknow.database.Record{


	private boolean state = false;
	
	public Node() {}
	
	public Node(com.talknow.database.Record d) {
		super(d);
	}
	
	public Node(String id,String passcode,String name,String style) {
		super(id,passcode,name,style);
	}
	
	public void onlineState() {
		state = true;
	}
	
	public void offlineState() {
		state = false;
	}
	
	public boolean getState() {
		return state;
	}
	
	
}
