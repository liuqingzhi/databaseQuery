package com.yesmynet.query.core.dto;
/**
 * 表示参数显示时的布局的DTO
 * @author zhi_liu
 *
 */
public class ParameterLayoutDTO {
	/**
	 * 显示顺序
	 */
	private Integer sort;
	/**
	 * 跨几行
	 */
	private Integer rowSpan;
	/**
	 * 跨几列
	 */
	private Integer columnSpan;
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getRowSpan() {
		return rowSpan;
	}
	public void setRowSpan(Integer rowSpan) {
		this.rowSpan = rowSpan;
	}
	public Integer getColumnSpan() {
		return columnSpan;
	}
	public void setColumnSpan(Integer columnSpan) {
		this.columnSpan = columnSpan;
	}
	
}
