package com.yesmynet.query.core.dto;

import java.util.List;

import javax.sql.DataSource;
/**
 * 表示系统中配置的所有数据源
 * @author 刘庆志
 *
 */
public class DataSourceConfig
{
	/**
	 * 主键
	 */
	private String id;
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
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
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
	
}
