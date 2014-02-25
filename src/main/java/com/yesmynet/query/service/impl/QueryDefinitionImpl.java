package com.yesmynet.query.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.ParameterInput;
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
    /**
     * 表示提交的参数与要执行的命令的Map
     */
    private Map<String,QueryService> commandQueryMap;
    private enum ParameterName
    {
    	Command("是执行的命令","command","",ParameterHtmlType.InputHidden,"","",""),
    	
    	QueryDefinitionId("查询的Id","definitionId","",ParameterHtmlType.InputHidden,"","",""),
    	QueryDefinitionName("查询的名称","definitionName","",ParameterHtmlType.InputText,"","",""),
    	QueryDefinitionDescription("查询描述","definitionDesc","",ParameterHtmlType.InputText,"","",""),
    	QueryDefinitionJavaCode("查询代码","definitionCode","",ParameterHtmlType.TextArea,"","",""),
    	
    	QueryParameterTitle("参数标题","parameterTitle","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterDescription("参数描述","parameterDescription","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterHtmlType("参数类型","parameterHtmlType","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterName("参数名称","parameterName","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterStyle("参数css","parameterStyle","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterStyleClass("参数css class","parameterStyleClass","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterEraseValue("不回显参数值","parameterEraseValue","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterOptionGetterKey("选项获取器","parameterOptionGetterKey","",ParameterHtmlType.InputHidden,"","",""),
    	QueryParameterElementHtml("直接html","parameterElementHtml","",ParameterHtmlType.InputHidden,"","",""),
    	
    	
    	
    	ExecuteButton("确定","executeButton","",ParameterHtmlType.Button,"","","onclick='$(\\\"#queryForm\\\").submit();'"),
    	;
    	private Parameter parameter;
    	/**
    	 * 构造函数
    	 * @param title 参数标题
    	 * @param description 参数描述
    	 * @param htmlType 参数html控件类型
    	 * @param name 参数名称
    	 * @param style 样式
    	 * @param styleCss 样式的class
    	 * @param elementHtml 直接在html中输出的内容
    	 */
    	private ParameterName(String title,String name,String description,ParameterHtmlType htmlType,String style,String styleClass,String elementHtml)
    	{
    		parameter=new Parameter();
    		ParameterInput input=new ParameterInput();
    		
    		input.setTitle(title);
    		input.setDescription(description);
    		input.setHtmlType(htmlType);
    		input.setName(name);
    		input.setStyle(style);
    		input.setStyleClass(styleClass);
    		input.setElementHtml(elementHtml);
    		parameter.setParameterInput(input);
    	}
		public Parameter getParameter() {
			return parameter;
		}
    }
    /**
     * 保存查询定义
     * @author liuqingzhi
     *
     */
    public static class QueryDefinitionSave implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			re.setContent("command=保存查询定义");
			
			return re;
		}
    }
    /**
     * 保存一个参数
     * @author liuqingzhi
     *
     */
    public static class QueryParameterSave implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			re.setContent("command=保存一个参数");
			
			return re;
		}
    }
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
		QueryResult re =null;
		List<Parameter> parameters = queryDefinition.getParameters();
		String command = QueryUtils.getParameterValue(parameters, ParameterName.Command.getParameter().getParameterInput().getName());
		QueryService commandQuery = commandQueryMap.get(command);
		if(commandQuery!=null)
			re=commandQuery.doInQuery(queryDefinition, resourceHolder, environment);
		return re;
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
        for(ParameterName p:ParameterName.values())
        {
        	parameters.add(p.getParameter());
        }
        queryDefinition.setParameters(parameters);

        return queryDefinition;
	}
	public Map<String, QueryService> getCommandQueryMap() {
		return commandQueryMap;
	}
	public void setCommandQueryMap(Map<String, QueryService> commandQueryMap) {
		this.commandQueryMap = commandQueryMap;
	}
}
