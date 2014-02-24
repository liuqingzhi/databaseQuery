package com.yesmynet.query.core.dto;

/**
 * 表示在一个Service方法的结果
 * @author zhi_liu
 *
 * @param <T>
 */
public class InfoDTO<T> extends BaseDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6908790467119020594L;
	/**
	 * 表示操作是否成功
	 */
	private boolean success;
	/**
	 * 消息
	 */
	private String msg;
	/**
	 * 其它数据
	 */
	private T data;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
}
