package com.yesmynet.query.core.dto;

public class RedisConfig extends BaseDto{
	/**
	 * 显示名称
	 */
	private String name;
	//private RedisTemplate redisTemplate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
