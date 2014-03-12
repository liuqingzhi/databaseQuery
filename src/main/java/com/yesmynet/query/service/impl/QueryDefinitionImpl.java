package com.yesmynet.query.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import com.yesmynet.query.core.dto.DataSourceConfig;
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
	 * 表示在执行查询时，默认要执行的命令
	 */
	private String DEFAULT_COMMAND="defaultCommand";
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
    	
    	QueryDefinitionId("查询的Id","id","",ParameterHtmlType.InputHidden,"","",""),
    	QueryDefinitionName("查询的名称","name","",ParameterHtmlType.InputText,"","",""),
    	QueryDefinitionDescription("查询描述","description","",ParameterHtmlType.InputText,"","",""),
    	QueryDefinitionJavaCode("查询代码","javaCode","",ParameterHtmlType.TextArea,"","",""),
    	
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
     * 根据ID得到数据库中的查询定义得到
     * @author liuqingzhi
     *
     */
    public static class QueryDefinitionGetter implements QueryService
    {

		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			DataSource systemDataSource = getSystemDataSource(resourceHolder);
			JdbcTemplate jdbcTemplate=new JdbcTemplate(systemDataSource);
			
			String sql="select * From m_sys_query where id=?";
			String queryId = QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.QueryDefinitionId.getParameter().getParameterInput().getName());
			QueryDefinition queryForObject = jdbcTemplate.queryForObject(sql, new Object[] {queryId}, QueryDefinitionRowMapper);
			
			sql="select t1.* From m_sys_query_parameter t1 where t1.query_id=?";
			List<Parameter> parameters = jdbcTemplate.query(sql, new Object[]{}, ParameterRowMapper);
			
			queryForObject.setParameters(parameters);
			
			
			return re;
		}
		private RowMapper<QueryDefinition> QueryDefinitionRowMapper=new RowMapper<QueryDefinition>() {

			@Override
			public QueryDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				QueryDefinition re=new QueryDefinition();
		        re.setId(rs.getString("id"));
		        re.setName(rs.getString("name"));
		        re.setDescription(rs.getString("description"));
		        re.setAfterParameterHtml(rs.getString("afterParameterHtml"));
		        re.setJavaCode(rs.getString("javaCode"));
		        
				return re;
			}
		};
		private RowMapper<Parameter> ParameterRowMapper=new RowMapper<Parameter>() {

			@Override
			public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Parameter re=new Parameter();
				ParameterInput input =new ParameterInput();
				re.setParameterInput(input);
		       
				
		        re.setId(rs.getString("id"));
		        input.setId(rs.getString("id"));
		        input.setTitle(rs.getString("title"));
		        input.setDescription(rs.getString("description"));
		        input.setHtmlType(ParameterHtmlType.valueOf(rs.getString("html_Type")));
		        input.setName(rs.getString("name"));
		        input.setStyle(rs.getString("style"));
		        input.setStyleClass(rs.getString("style_class"));
		        input.setOptionGetterKey(rs.getString("option_getter_Key"));
		        
				
				return re;
			}
		};
				
			
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
		if(!StringUtils.hasText(command))
		{
			command=DEFAULT_COMMAND;
		}
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
	/**
	 * 得到系统数据库
	 * @param resourceHolder
	 * @return
	 */
	private static DataSource getSystemDataSource(ResourceHolder resourceHolder)
	{
		DataSource re=null;
		List<DataSourceConfig> dataSourceConfigs = resourceHolder.getDataSourceConfigs();
		if(CollectionUtils.isNotEmpty(dataSourceConfigs))
		{
			for(DataSourceConfig dataSourceConfig:dataSourceConfigs)
			{
				if(dataSourceConfig.isSystemConfigDb())
				{
					re=dataSourceConfig.getDatasource();
					break;
				}
			}
		}
		
		return re;
	}
	public Map<String, QueryService> getCommandQueryMap() {
		return commandQueryMap;
	}
	public void setCommandQueryMap(Map<String, QueryService> commandQueryMap) {
		this.commandQueryMap = commandQueryMap;
	}
}
