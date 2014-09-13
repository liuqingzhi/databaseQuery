package com.yesmynet.query.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.ParameterLayoutDTO;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.http.service.QueryRenderService;
import com.yesmynet.query.utils.QueryUtils;
/**
 * 查询的抽象实现，用于表示主查询，所谓主查询，就是本对象是用于显示界面、接收命令，当收到命令后
 * 再寻找合适的子查询，用来处理命令，把结果显示出来。
 * @author liuqingzhi
 *
 */
public abstract class AbstractMainQueryService implements QueryService{
	/**
	 * 展示Query的Service，也就是 把查询的所有参数显示出来
	 */
	protected QueryRenderService queryRenderService;
	/**
     * 表示提交的参数与要执行的命令的Map
     * 也就是命令与子查询的Map
     */
    protected Map<String,QueryService> commandQueryMap;
	public AbstractMainQueryService() {
		super();
		afterConstructor();
	}
	@Override
	public QueryResult doInQuery(QueryDefinition queryDefinition,ResourceHolder resourceHolder, Environment environment) {
		QueryResult doInQuery=null;
		QueryService queryService =getQueryService(queryDefinition, resourceHolder, environment);
		if(queryService!=null)
		{
			doInQuery = queryService.doInQuery(queryDefinition, resourceHolder, environment);
			
			if(doInQuery!=null && doInQuery.getOnlyShowContent()!=null && doInQuery.getOnlyShowContent())
			{
				return doInQuery;
			}
		}
		
		settingParameterOptions(queryDefinition,resourceHolder,environment);
		
		/*String queryHtml = queryRenderService.getQueryHtml(queryDefinition);
		if(doInQuery!=null)
		{
			re=doInQuery;
			re.setContent(queryHtml+re.getContent());
		}
		else
		{
			re.setContent(queryHtml);
		}
		return re;*/
		return doInQuery;
	}
	/**
	 * 构造函数后要执行的方法，可以在此方法初始化一些变量，如初始化 #commandQueryMap
	 */
	protected void afterConstructor()
	{
		
	}
	protected abstract String getCommand(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment);
	/**
	 * 设置参数的选项
	 * @param parameters
	 * @param resourceHolder 
	 * @param environment 
	 */
	protected void settingParameterOptions(QueryDefinition queryDefinition,ResourceHolder resourceHolder, Environment environment)
	{
	}
	/**
	 * 得到查询定义
	 * @return
	 */
	public abstract QueryDefinition getQueryDefinition();
	protected QueryService getQueryService(QueryDefinition queryDefinition,ResourceHolder resourceHolder, Environment environment) 
	{
		QueryService queryService = commandQueryMap.get(getCommand(queryDefinition,resourceHolder,environment));
		return queryService;
	}
	public void setQueryRenderService(QueryRenderService queryRenderService) {
		this.queryRenderService = queryRenderService;
	}
	public void setCommandQueryMap(Map<String, QueryService> commandQueryMap) {
		this.commandQueryMap = commandQueryMap;
	}
}
