package com.yesmynet.query.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.utils.QueryUtils;

/**
 * 实现查询定义的管理功能的查询。
 * @author zhi_liu
 *
 */
public class QueryDefinitionImpl implements QueryService,QueryDefinitionGetter{
	 /**
     * 关于本查询的设置，包括所有的参数
     */
    private QueryDefinition queryDefinition;
	@Override
	public QueryDefinition getQueryDefinition() {
		if(this.queryDefinition==null)
    	{
    		this.queryDefinition=initQueryDefinition();
    	}
        return this.queryDefinition;
	}

	@Override
	public QueryResult doInQuery(QueryDefinition queryDefinition, ResourceHolder resourceHolder, Environment environment) {
		return null;
	}
	/**
	 * 初始化本查询的所有参数
	 * @return
	 */
	private QueryDefinition initQueryDefinition()
	{
        /**
         * 初始化本查询的配置,包括所有参数
         */
		QueryDefinition queryDefinition=new QueryDefinition();
        List<Parameter> parameters=new ArrayList<Parameter>(); 
        
        StringBuilder paramJsons=new StringBuilder();
        
        paramJsons.append("[");
        
        paramJsons.append("{\"queryDefinition\":null,\"parameterInput\":{\"title\":\"SQL脚本\",\"description\":\"功能提示：1、可以使用F8执行SQL；2、可以选中一部分SQL执行，然后执行。\",\"htmlType\":\"TextArea\",\"name\":\"sqlCode\",\"style\":\"width: 1000px; height: 200px;\",\"styleClass\":null,\"values\":null,\"OptionValues\":null,\"eraseValue\":null,\"optionGetterKey\":null,\"elementHtml\":null,\"id\":null},\"validatorRules\":null,\"id\":null}");
        paramJsons.append(",");
        
        paramJsons.append("]");
        
        parameters = QueryUtils.getParametersFromJson(paramJsons.toString());
        queryDefinition.setParameters(parameters);

        return queryDefinition;
	}
}
