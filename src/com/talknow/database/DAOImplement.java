package com.talknow.database;

import java.sql.*;

public class DAOImplement implements DAO{

	private Statement statment = null;
	private ResultSet rs = null;
	
	public DAOImplement(Connection conn) {
		try {
			statment = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int add(Record r) {
		int b;
		try {
			b = statment.executeUpdate("Insert into Person values("+r+")");
		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return b>=1?0:1;
	}

	@Override
	public Record searchById(String id) {
		Record r = null;
		try {
			rs = statment.executeQuery("Select * From Person where Pid='"+id+"'");
			if(rs.next()) {
				r = new Record();
				r.setId(rs.getString("Pid"));
				r.setPasscode(rs.getString("Ppasscode"));
				r.setName(rs.getString("Pname"));
				r.setStyle(rs.getString("Pstyle"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}

	@Override
	public int alter(Record r) {
		try {
			statment.execute("UPDATE Person set Pstyle='"+r.getStyle()+"' where Pid='"+r.getId()+"'");
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	@Override
	public int delete(Record r) {
		return deleteById(r.getId());
	}

	@Override
	public int deleteById(String id) {
		int b = -1;
		try {
			b=statment.executeUpdate("DELETE FROM Person WHERE Pid='"+id+"'");
		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return b>=1?0:1;
	}

	@Override
	public void invalidate() {
		try {
			rs.close();
			statment.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void show() {
		try {
			rs = statment.executeQuery("Select * from Person");
			while(rs.next()) {
				System.out.println(rs.getString(1)+" "+rs.getString(2) +" "+rs.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
