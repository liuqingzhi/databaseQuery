package com.yesmynet.query.dto;

import java.util.Date;

public class DBUpdateDTO
{
	/**
	 * 主键 
	 */
	private Integer id;
	/**
	 * 数据库当前版本
	 */
	private Integer currentVersion;
	/**
	 * 上次系统更新数据库的时间
	 */
	private Date lastUpdateTime;
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	public Integer getCurrentVersion()
	{
		return currentVersion;
	}
	public void setCurrentVersion(Integer currentVersion)
	{
		this.currentVersion = currentVersion;
	}
	public Date getLastUpdateTime()
	{
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime)
	{
		this.lastUpdateTime = lastUpdateTime;
	}
	
}
