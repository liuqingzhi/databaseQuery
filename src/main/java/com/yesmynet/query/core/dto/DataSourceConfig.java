package com.yesmynet.query.core.dto;

import java.util.List;

import javax.sql.DataSource;
/**
 * 表示系统中配置的所有数据源
 * @author 刘庆志
 *
 */
public class DataSourceConfig extends BaseDto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 显示名称
	 */
	private String name;
	/**
	 * 数据库言
	 */
	private DatabaseDialect databaseDialect;
	/**
	 * 配置的数据源
	 */
	private DataSource datasource;
	/**
	 * 是否是用来维护系统自己的数据。
	 * 因为系统本身也是通过数据库来维护数据的，有时，在执行一个查询时，希望得到系统自己的
	 * 数据，所以要识别一下。
	 */
	private boolean systemConfigDb;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public DatabaseDialect getDatabaseDialect()
	{
		return databaseDialect;
	}
	public void setDatabaseDialect(DatabaseDialect databaseDialect)
	{
		this.databaseDialect = databaseDialect;
	}
	public DataSource getDatasource()
	{
		return datasource;
	}
	public void setDatasource(DataSource datasource)
	{
		this.datasource = datasource;
	}
	public boolean isSystemConfigDb() {
		return systemConfigDb;
	}
	public void setSystemConfigDb(boolean systemConfigDb) {
		this.systemConfigDb = systemConfigDb;
	}
	
}
