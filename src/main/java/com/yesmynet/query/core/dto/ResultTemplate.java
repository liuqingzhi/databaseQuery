package com.yesmynet.query.core.dto;
/**
 * 在查询中使用的freemarker模板。
 * 因为在生成查询的结果时，有时要拼成一长串html，希望可以使用模板帮助生成结果。
 * @author zhi_liu
 *
 */
public class ResultTemplate extends BaseDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 对应的查询
	 */
	private QueryDefinition queryDefinition;
	/**
	 * 模板的代码
	 */
	private String name;
	/**
	 * 模板的显示名称
	 */
	private String title;
	/**
	 * 模板的内容
	 */
	private String content;
	public QueryDefinition getQueryDefinition() {
		return queryDefinition;
	}
	public void setQueryDefinition(QueryDefinition queryDefinition) {
		this.queryDefinition = queryDefinition;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
