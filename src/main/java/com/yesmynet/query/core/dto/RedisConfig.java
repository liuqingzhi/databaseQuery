package com.yesmynet.query.core.dto;

public class RedisConfig {
	/**
	 * 主键
	 */
	private String id;
	/**
	 * 显示名称
	 */
	private String name;
	//private RedisTemplate redisTemplate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
