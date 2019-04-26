package com.talknow.database;

public class Proxy implements DAO{
	
	private DatabaseConnection dc = null;
	private DAO dao = null;
	
	protected Proxy() {
		dc = new DatabaseConnection();
		dao = new DAOImplement( dc.getConnection() );
	}

	@Override
	public int add(Record r) {
		return dao.add(r);
	}

	@Override
	public int alter(Record r) {
		return dao.alter(r);
	}

	@Override
	public int delete(Record r) {
		return dao.delete(r);
	}

	@Override
	public int deleteById(String id) {
		return dao.deleteById(id);
	}

	@Override
	public Record searchById(String id) {
		return dao.searchById(id);
	}
	
	public void invalidate() {
		dao.invalidate();
		dc.invalidate();
	}
	

}
