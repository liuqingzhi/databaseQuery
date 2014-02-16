package com.yesmynet.query.core.service;

import com.yesmynet.query.core.dto.QueryDefinition;

/**
 * 得到查询定义的接口。
 * @author liuqingzhi
 *
 */
public interface QueryDefinitionGetter {
	/**
	 * 得到查询定义的方法
	 * @return
	 */
	public QueryDefinition getQueryDefinition();
}
