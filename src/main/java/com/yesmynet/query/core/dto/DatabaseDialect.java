package com.yesmynet.query.core.dto;

public enum DatabaseDialect
{
	/**
	 * oracle数据库
	 */
	Oracle("oracle"),
	/**
	 * derby数据库
	 */
	Derby("derby")
	
	;
	/**
	 * 数据库的代码
	 */
	private String dialectCode;
	private DatabaseDialect(String dialect)
	{
		this.dialectCode=dialect;
	}
	
}
