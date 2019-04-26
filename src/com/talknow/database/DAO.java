package com.talknow.database;

/**
 * 
 * 有本接口内int型返回的方法都应遵守以下规则
 * 
 * return 0 代表字段更新成功
 * return 1 代表字段更新失败
 * return 2 代表数据库连接异常
 * 
 * @author Alex
 *
 */
public interface DAO {
	
	public int add(Record r);
	
	public int alter(Record r);
	
	public int delete(Record r);
	
	public int deleteById(String id);

	public Record searchById(String id);
	
	public void invalidate();
	
}