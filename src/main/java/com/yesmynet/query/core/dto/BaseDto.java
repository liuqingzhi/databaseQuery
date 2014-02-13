package com.yesmynet.query.core.dto;
/**
 * 表示DTO的基类。
 * @author 刘庆志
 *
 */
public class BaseDto implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	protected String id;
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	
}
