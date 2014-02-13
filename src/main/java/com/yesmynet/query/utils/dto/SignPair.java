package com.yesmynet.query.utils.dto;
/**
 * 表示成对出现的符号,如''，两个单引号就是成对出现的符号，其它 还有注释也是.
 * 
 * @author 刘庆志
 * 
 */
public class SignPair {
	/**
	 * 开始标记，以正则表达式表示
	 */
	private String start;
	/**
	 * 结束标记，以正则表达式表示
	 */
	private String end;
	/**
	 * 构造函数
	 */
	public SignPair() {
		super();
	}
	/**
	 * 构造函数
	 * 
	 * @param start
	 * @param end
	 */
	public SignPair(String start, String end) {
		super();
		this.start = start;
		this.end = end;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

}