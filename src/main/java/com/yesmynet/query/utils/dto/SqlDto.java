package com.yesmynet.query.utils.dto;

/**
 * 表示一条sql语句的对象
 * @author 刘庆志
 *
 */
public class SqlDto
{
	/**
	 * sql语句
	 */
	private String sql;
	/**
	 * 是否是一个select语句。
	 */
	private boolean select;
	public String getSql()
	{
		return sql;
	}
	public void setSql(String sql)
	{
		this.sql = sql;
	}
	public boolean isSelect()
	{
		return select;
	}
	public void setSelect(boolean select)
	{
		this.select = select;
	}
}