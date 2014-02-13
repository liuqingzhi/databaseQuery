package com.yesmynet.query.core.dto;

public class SelectOption {

	/**
	 * 选项的值
	 */
	private String value;
	/**
	 * 选项的文本
	 */
	private String text;
	/**
	 * 是否选中
	 */
	private Boolean selected;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
