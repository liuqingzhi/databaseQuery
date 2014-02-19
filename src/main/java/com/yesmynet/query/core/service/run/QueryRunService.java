package com.yesmynet.query.core.service.run;

import java.util.List;

import javax.management.Query;
import javax.sql.DataSource;

import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;


/**
 * 执行查询的Service,也就是对于得到的一个{@link com.yesmynet.database.query.core.dto.Query}实例，
 * 运行这个实例，得到结果，并且显示。
 * 
 * @author 刘庆志
 *
 */
public interface QueryRunService
{
	/**
	 * 显示一个查询
	 * @param queryId
	 * @return
	 */
	public QueryDefinition getQueryDefinition(String queryId);
	/**
	 * 运行一个查询
	 * @param queryId 要运行的查询的ID
	 * @return 查询结果，其中包含了查询的定义
	 */
	public QueryResult run(QueryDefinition queryDefinition);
}
