package com.talknow.database;

import java.sql.*;

public class DatabaseConnection {
	
	final String DBDriver = "org.apache.derby.jdbc.EmbeddedDriver";
	final String connectionStr = "jdbc:derby:Person";
	
	private Connection conn;
	
	public DatabaseConnection() {
		try {
			Class.forName(DBDriver).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(connectionStr);
		} catch (SQLException e) {
			try {
				conn = DriverManager.getConnection("jdbc:derby:Person;create=true");
				conn.createStatement().execute("CREATE TABLE Person("
											 + "Pid varchar(20) primary key,"
											 + "Ppasscode varchar(20),"
											 + "Pname varchar(20),"
											 + "Pstyle varchar(20)"
											 + ")");
				System.out.println("built a database");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public void invalidate() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
