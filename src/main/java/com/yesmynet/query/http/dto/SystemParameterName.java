package com.yesmynet.query.http.dto;

/**
 * 系统使用的request参数名，在查询定义时，应该不要让查询的参数名与
 * 这里使用的参数名有一样的。
 * @author 刘庆志
 *
 */
public enum SystemParameterName
{
	/**
	 * 表示要查看或要执行的查询的Id
	 */
	QueryId("SystemQueryId"),
	/**
	 * 表示要执行查询的命令，当本参数有值，不管值是什么都表示要执行查询
	 */
	QueryExecute("SystemQueryExecute"),
	/**
     * 表示要使用哪个数据源
     * 在执行查询时，可以从多个数据源中选择一个。
     */
    DataSourceId("SystemDataSourceId")
    
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
