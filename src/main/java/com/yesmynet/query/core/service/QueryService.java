package com.yesmynet.query.core.service;

import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.QueryResult;
/**
 * 查询接口，表示运行时要执行的操作。
 * @author liuqingzhi
 *
 */
public interface QueryService {
	/**
	 * 在执行查询时要实现的逻辑。
	 * @param resourceHolder 资源持有者
	 * @param environment 当前的一些环境变量。
	 * @return 
	 */
	public QueryResult doInQuery(ResourceHolder resourceHolder,Environment environment);
}
