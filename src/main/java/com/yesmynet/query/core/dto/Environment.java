package com.yesmynet.query.core.dto;

public class Environment extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 当前用户
	 */
	private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
