package com.yesmynet.query.core.service;
/**
 * 表示在运行时显示一个查询时的监听器，当在显示一个查询时，如果这个查询实现了本接口，会在
 * 相应事件发生时调用相应的接口。
 * @author liuqingzhi
 *
 */
public interface QueryShowListner {
	/**
	 * 在显示这个查询前调用本方法
	 */
	public void beforeShow();
	
}
