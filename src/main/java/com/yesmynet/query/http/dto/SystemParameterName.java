package com.yesmynet.query.http.dto;

/**
 * 系统使用的request参数名。
 * @author 刘庆志
 *
 */
public enum SystemParameterName
{
	/**
	 * 表示要查看或要执行的查询的Id
	 */
	QueryId("SystemQueryId"),
	;
	/**
	 * http参数名称
	 */
	private String paramerName;
	private SystemParameterName(String httpParamerName)
	{
		this.paramerName=httpParamerName;
	}
	public String getParamerName()
	{
		return paramerName;
	}
	
}
