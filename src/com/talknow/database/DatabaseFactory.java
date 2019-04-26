package com.talknow.database;

public class DatabaseFactory {
	
	public static DAO getNewProxy() {
		DAO p = null;
//		try {
//			p = (Proxy) Class.forName("com.talknow.database.Proxy").newInstance();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}s
		p = new Proxy();
		return p;
	}

}
