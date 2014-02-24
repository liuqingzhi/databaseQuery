package com.yesmynet.query.core.dto;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisConfig extends BaseDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1612585674754711295L;
	/**
	 * 显示名称
	 */
	private String name;
	/**
	 * 操作redis的对象
	 */
	private RedisTemplate redisTemplate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
