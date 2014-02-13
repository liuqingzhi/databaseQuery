package com.yesmynet.query.core.service.run;

import java.util.List;

import javax.management.Query;
import javax.sql.DataSource;

import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryReult;


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
	 * 运行一个查询实例
	 * @param queryId 要运行的查询的ID
	 * @param run 是否运行这个查询，如果为true表示执行这个查询，如果为false表示不执行这个查询。
	 * 因为有时只是为了显示一个查询而不执行它，典型的就是，当用户第一次打开一个查询时，这时只要显示这个
	 * 查询，然后，用户在显示出的查询上输入一些参数，然后再执行，但是，在显示时其实也要得到这个查询的实例
	 * 因为要判断这个实例是否实现了QueryShowListner接口，如果实现了，则要调用这个查询的相应参数
	 * @return 查询结果，其中包含了查询的定义
	 */
	public QueryReult run(String queryId,boolean run);
}
