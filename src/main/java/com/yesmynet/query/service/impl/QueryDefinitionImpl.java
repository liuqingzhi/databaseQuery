package com.yesmynet.query.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.InfoDTO;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.ResultTemplate;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.core.exception.ServiceException;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.utils.FreemarkerUtils;
import com.yesmynet.query.utils.QueryUtils;

/**
 * 实现查询定义的管理功能的查询。
 * @author zhi_liu
 *
 */
public class QueryDefinitionImpl implements QueryService,QueryDefinitionGetter{
	private Logger logger=LoggerFactory.getLogger(getClass());
	/**
	 * 用于生成gson字符串的对象
	 */
	private Gson gson = new GsonBuilder().create();//.serializeNulls()
	/**
	 * 表示在执行查询时，默认要执行的命令
	 */
	private String DEFAULT_COMMAND="queryDefinitionGetter";
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
    	Command("是执行的命令","command","",ParameterHtmlType.InputHidden,"","","",false),
    	
    	QueryDefinitionId("查询的Id","id","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryDefinitionName("查询的名称","name","",ParameterHtmlType.InputText,"","","",false),
    	QueryDefinitionDescription("查询描述","description","",ParameterHtmlType.InputText,"","","",false),
    	QueryDefinitionJavaCode("查询代码","javaCode","",ParameterHtmlType.TextArea,"","","",false),
    	
    	QueryParameterQueryId("参数的查询的Id","queryDefinition.id","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterId("参数的Id","id","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterTitle("参数标题","parameterInput.title","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterDescription("参数描述","parameterInput.description","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterHtmlType("参数类型","parameterInput.htmlType","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterName("参数名称","parameterInput.name","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterStyle("参数css","parameterInput.style","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterStyleClass("参数css class","parameterInput.styleClass","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterElementHtml("直接Html","parameterInput.elementHtml","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterShow("不显示本参数","parameterInput.show","",ParameterHtmlType.InputHidden,"","","",false),
    	QueryParameterEraseValue("不回显参数值","parameterInput.eraseValue","",ParameterHtmlType.InputHidden,"","","",false),
    	ToDeleteParameterId("要删除的参数的Id","toDeleteParameterId","",ParameterHtmlType.InputHidden,"","","",false),
    	
    	
    	TemplateId("模板的Id","id","",ParameterHtmlType.InputHidden,"","","",false),
    	TemplateQueryId("模板的查询的Id","queryDefinition.id","",ParameterHtmlType.InputHidden,"","","",false),
    	TemplateCode("模板代码","code","",ParameterHtmlType.InputHidden,"","","",false),
    	TemplateTitle("模板标题","title","",ParameterHtmlType.InputHidden,"","","",false),
    	TemplateContent("模板内容","content","",ParameterHtmlType.InputHidden,"","","",false),
    	ToDeleteTemplateId("要删除的模板的Id","ToDeleteTemplateId","",ParameterHtmlType.InputHidden,"","","",false),
    	
    	
    	
    	//ExecuteButton("确定","executeButton","",ParameterHtmlType.Button,"","","onclick='$(\\\"#queryForm\\\").submit();'",true),
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
    	private ParameterName(String title,String name,String description,ParameterHtmlType htmlType,String style,String styleClass,String elementHtml,Boolean notShow)
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
    		input.setShow(notShow);
    		
    		parameter.setParameterInput(input);
    	}
		public Parameter getParameter() {
			return parameter;
		}
    }
    /**
     * 构造函数
     */
    public QueryDefinitionImpl() {
		super();
		//初始化配置
		commandQueryMap=new HashMap<String,QueryService>();
		commandQueryMap.put("queryDefinitionGetter", new QueryDefinitionGetter());
		commandQueryMap.put("queryDefinitionSave", new QueryDefinitionSave());
		commandQueryMap.put("queryParameterSave", new QueryParameterSave());
		commandQueryMap.put("queryParameterGetter", new QueryParameterGetter());
		commandQueryMap.put("queryParameterDeleter", new QueryParameterDeleter());
		commandQueryMap.put("queryDefinitionAjaxGetter", new QueryDefinitionAjaxGetter());
		
		commandQueryMap.put("templateGetter", new TemplateGetter());
		commandQueryMap.put("templateSave", new TemplateSave());
		commandQueryMap.put("templateDeleter", new TemplateDeleter());
		
	}
    /**
     * 把参数的数据库查询映射为一个对象
     */
    private RowMapper<Parameter> parameterRowMapper=new RowMapper<Parameter>() {

		@Override
		public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			Parameter re=new Parameter();
			ParameterInput input =new ParameterInput();
			QueryDefinition queryDefinition=new QueryDefinition();
			
			re.setParameterInput(input);
			re.setQueryDefinition(queryDefinition);
	       
			queryDefinition.setId(rs.getString("query_id"));
	        re.setId(rs.getString("id"));
	        input.setId(rs.getString("id"));
	        input.setTitle(rs.getString("title"));
	        input.setDescription(rs.getString("description"));
	        input.setHtmlType(ParameterHtmlType.valueOf(rs.getString("html_Type")));
	        input.setName(rs.getString("name"));
	        input.setStyle(rs.getString("style"));
	        input.setStyleClass(rs.getString("style_class"));
			input.setEraseValue(rs.getInt("erase_value")==1?true:false);
			input.setShow(rs.getInt("show")==1?true:false);
			input.setElementHtml(rs.getString("element_html"));
			return re;
		}
	};
	/**
     * 把模板的数据库查询映射为一个对象
     */
    private RowMapper<ResultTemplate> templateRowMapper=new RowMapper<ResultTemplate>() {

		@Override
		public ResultTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			ResultTemplate re=new ResultTemplate();
			QueryDefinition queryDefinition=new QueryDefinition();
			
			re.setQueryDefinition(queryDefinition);
	       
			queryDefinition.setId(rs.getString("query_id"));
	        re.setId(rs.getString("id"));
	        re.setCode(rs.getString("code"));
	        re.setTitle(rs.getString("title"));
	        re.setContent(rs.getString("content"));
			return re;
		}
	};
	/**
     * 得到操作系统数据库的jdbcTemplate
     * @param resourceHolder
     * @return
     */
    private JdbcTemplate getSystemDBTemplate(ResourceHolder resourceHolder) throws ServiceException
    {
    	DataSource systemDataSource = getSystemDataSource(resourceHolder);
		if(systemDataSource==null)
		{
			//没有得到系统数据库，可能是当前用户没权限。
			throw new ServiceException("您没有权限编辑查询");
		}
		JdbcTemplate jdbcTemplate=new JdbcTemplate(systemDataSource);
		return jdbcTemplate;
    }
    /**
     * 从数据库根据ID得到查询定义
     * @param queryId
     * @param resourceHolder
     * @return
     * @throws ServiceException
     */
    private QueryDefinition getQueryDefinitionById(String queryId,ResourceHolder resourceHolder) throws ServiceException
    {
    	JdbcTemplate jdbcTemplate=getSystemDBTemplate(resourceHolder);
    	
    	QueryDefinition queryDefinitionInDB=null;
		if(queryId!=null)
		{
			String sql="select * From m_sys_query where id=?";
			queryDefinitionInDB = jdbcTemplate.queryForObject(sql, new Object[] {queryId}, new RowMapper<QueryDefinition>() {

				@Override
				public QueryDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
					
					QueryDefinition re=new QueryDefinition();
			        re.setId(rs.getString("id"));
			        re.setName(rs.getString("name"));
			        re.setDescription(rs.getString("description"));
			        re.setAfterParameterHtml(rs.getString("after_Parameter_Html"));
			        re.setJavaCode(rs.getString("java_code"));
			        
					return re;
				}
			});
			
			sql="select t1.* From m_sys_query_parameter t1 where t1.query_id=?";
			List<Parameter> parameters = jdbcTemplate.query(sql, new Object[]{queryId}, parameterRowMapper);
			queryDefinitionInDB.setParameters(parameters);
			
			sql="select t1.* From m_sys_query_template t1 where t1.query_id=?";
			List<ResultTemplate> templates = jdbcTemplate.query(sql, new Object[]{queryId}, templateRowMapper);
			queryDefinitionInDB.setTemplates(templates);
			
		}
		
    	return queryDefinitionInDB;
    }
    /**
     * 显示查询定义
     * @param queryDefinitionInDB
     * @return
     */
    private String showQueryDefinition(QueryDefinition queryDefinitionInDB,boolean ajaxShow,QueryDefinition queryDefinition) {
		Map<String,Object> toViewDatas=new HashMap<String,Object>();
		toViewDatas.put("queryDefinition", queryDefinitionInDB);
		toViewDatas.put("allHtmlTypes", ParameterHtmlType.values());
		toViewDatas.put("ajaxShow", ajaxShow);
		toViewDatas.put("systemQueryId", queryDefinition.getId());
		
		
		String content = FreemarkerUtils.renderTemplateInClassPath("/com/yesmynet/query/service/impl/editQuery.ftl", toViewDatas);
		return content;
	}
    /**
     * 根据ID得到一个参数
     * @param id
     * @param resourceHolder
     * @return
     * @throws ServiceException
     */
    private Parameter getParameterById(String id,ResourceHolder resourceHolder) throws ServiceException
    {
    	Parameter re=null;
    	if(StringUtils.hasText(id))
    	{
    		JdbcTemplate jdbcTemplate=getSystemDBTemplate(resourceHolder);
    		String sql="select t1.* From m_sys_query_parameter t1 where t1.id=?";
			re = jdbcTemplate.queryForObject(sql, new Object[]{id}, parameterRowMapper);
    	}
    	return re;
    }
    /**
     * 得到模板
     * @param id 模板的Id
     * @param resourceHolder
     * @return
     * @throws ServiceException
     */
    private ResultTemplate getTemplateById(String id,ResourceHolder resourceHolder) throws ServiceException
    {
    	ResultTemplate re=null;
    	if(StringUtils.hasText(id))
    	{
    		JdbcTemplate jdbcTemplate=getSystemDBTemplate(resourceHolder);
    		String sql="select t1.* From m_sys_query_template t1 where t1.id=?";
			re = jdbcTemplate.queryForObject(sql, new Object[]{id}, templateRowMapper);
    	}
    	return re;
    }
    /**
     * 显示一个参数
     * @param paramter
     * @return
     */
    private String showParameter(Parameter paramter,QueryDefinition queryDefinition) {
		Map<String,Object> toViewDatas=new HashMap<String,Object>();
		toViewDatas.put("parameter", paramter);
		toViewDatas.put("allHtmlTypes", ParameterHtmlType.values());
		toViewDatas.put("yesOrNoOptions", getOptionsForYesOrNo());
		toViewDatas.put("systemQueryId", queryDefinition.getId());
		
		String content = FreemarkerUtils.renderTemplateInClassPath("/com/yesmynet/query/service/impl/editParameter.ftl", toViewDatas);
		return content;
	}
    /**
     * 得到表示是否的选项的值
     * @param value
     * @return
     */
    private List<SelectOption> getOptionsForYesOrNo()
    {
    	List<SelectOption> re=new ArrayList<SelectOption>();
    	
    	SelectOption yes=new SelectOption();
    	yes.setText("是");
    	yes.setValue("1");
    	re.add(yes);
    	
    	SelectOption no=new SelectOption();
    	no.setText("否");
    	no.setValue("0");
    	re.add(no);
    	
    	return re;
    }
    /**
     * 显示模板
     * @param queryDefinitionInDB
     * @return
     */
    private String showTemplate(ResultTemplate template,QueryDefinition queryDefinition) {
		Map<String,Object> toViewDatas=new HashMap<String,Object>();
		toViewDatas.put("resultTemplate", template);
		toViewDatas.put("systemQueryId", queryDefinition.getId());
		
		
		String content = FreemarkerUtils.renderTemplateInClassPath("/com/yesmynet/query/service/impl/editTemplate.ftl", toViewDatas);
		return content;
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
    /**
     * 根据ID得到数据库中的查询定义得到
     * @author liuqingzhi
     *
     */
    private class QueryDefinitionGetter implements QueryService
    {

		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			
			String queryId = QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.QueryDefinitionId.getParameter().getParameterInput().getName());
			QueryDefinition queryDefinitionInDB=null;
		
			try {
				if(StringUtils.hasText(queryId))
				{
					queryDefinitionInDB=getQueryDefinitionById(queryId,resourceHolder);
				}	
			} catch (ServiceException e) {
				re.setContent(e.getMessage());
				return re;
			}
			String content = showQueryDefinition(queryDefinitionInDB,false,queryDefinition);
			re.setContent(content);
			return re;
		}
			
    }
    /**
     * 保存查询定义
     * @author liuqingzhi
     *
     */
    private class QueryDefinitionSave implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			JdbcTemplate jdbcTemplate=null;
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> datas=new HashMap<String,Object>();
			
			infoDTO.setData(datas);
			infoDTO.setSuccess(true);
			infoDTO.setMsg("保存查询定义成功");
			
			try {
				jdbcTemplate=getSystemDBTemplate(resourceHolder);
				

				final QueryDefinition queryByRequest = bindDatas(queryDefinition);
				
				String id = queryByRequest.getId();
				String sql="";
				
				if(StringUtils.hasText(id))
				{
					final int queryId=Integer.parseInt(id);
							
					sql="update m_sys_query set name=?,description=?,after_Parameter_Html=?,java_code=? where id=?";
					jdbcTemplate.update(sql, new PreparedStatementSetter(){
						@Override
						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setString(1, queryByRequest.getName());
							ps.setString(2, queryByRequest.getDescription());
							ps.setString(3, queryByRequest.getAfterParameterHtml());
							ps.setString(4, queryByRequest.getJavaCode());
							ps.setInt(5, queryId);
						}});
				}
				else
				{
					KeyHolder keyHolder = new GeneratedKeyHolder();
					jdbcTemplate.update(
					    new PreparedStatementCreator() {
					        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					            PreparedStatement ps =
					                connection.prepareStatement("insert into m_sys_query (name,description,after_Parameter_Html,java_code) values (?,?,?,?)", new String[] {"ID"});/*这个自动生成键的字段的名称一定要大写，不然会报错：SQL state [X0X0F]; error code [30000]; Table 'M_SYS_QUERY' does not have an auto-generated column named 'id'.; nested exception is java.sql.SQLException: Table 'M_SYS_QUERY' does not have an auto-generated column named 'id'.*/

					            ps.setString(1, queryByRequest.getName());
								ps.setString(2, queryByRequest.getDescription());
								ps.setString(3, queryByRequest.getAfterParameterHtml());
								ps.setString(4, queryByRequest.getJavaCode());

					            return ps;
					        }
					    },
					    keyHolder);
					id = keyHolder.getKey()+"";
				}
				QueryDefinition queryDefinitionById = getQueryDefinitionById(id,resourceHolder);
				String content = showQueryDefinition(queryDefinitionById,true,queryDefinition);
				
				datas.put("html", content);
				
			}
			catch(Exception e)
			{
				logger.error("保存查询定义失败",e);
				
				infoDTO.setSuccess(false);
				infoDTO.setMsg("保存查询定义失败，系统错误");
			}
			
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
		/**
		 * 把http请求的参数绑定到一个QueryDefinition对象上
		 * @param queryDefinition
		 * @return
		 */
		private QueryDefinition bindDatas(QueryDefinition queryDefinition)
		{
			QueryDefinition re =new QueryDefinition();
			List<Parameter> parameters = queryDefinition.getParameters();
			final Map<String,Object> parameterMap=new HashMap<String,Object>();
			for (Parameter i : parameters) parameterMap.put(i.getParameterInput().getName(),i.getParameterInput().getValue());
			
			WebDataBinder webDataBinder =new WebDataBinder(re);
			MutablePropertyValues propertyValues=new MutablePropertyValues(parameterMap);
			webDataBinder.bind(propertyValues);
			
			return re;
		}
    }
    /**
     * 得到一个参数并显示之
     * @author liuqingzhi
     *
     */
    private class QueryParameterGetter implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> data=new HashMap<String,Object>();
			infoDTO.setData(data);
			
			infoDTO.setSuccess(true);
			infoDTO.setMsg("得到参数成功");
			try
			{
				String parameterId = QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.QueryParameterId.getParameter().getParameterInput().getName());
				Parameter parameter = getParameterById(parameterId,resourceHolder);
				String showParameter = showParameter(parameter,queryDefinition);
				data.put("html", showParameter);
				
			}
			catch(Exception e)
			{
				infoDTO.setSuccess(true);
				infoDTO.setMsg("得到查询参数失败，"+e.getMessage());
			}
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
    }
    /**
     * 保存一个参数
     * @author liuqingzhi
     *
     */
    private class QueryParameterSave implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			JdbcTemplate jdbcTemplate=null;
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> datas=new HashMap<String,Object>();
			
			try {
				
				infoDTO.setData(datas);
				infoDTO.setSuccess(true);
				infoDTO.setMsg("保存参数成功");
				
				jdbcTemplate=getSystemDBTemplate(resourceHolder);
				

				final Parameter parameter = bindDatas(queryDefinition);
				
				String id = parameter.getId();
				String sql="";
				

				if(StringUtils.hasText(id))
				{
					final int queryId=Integer.parseInt(id);
							
					sql="update m_sys_query_parameter set title=?,description=?,html_Type=?,name=?,style=?,style_class=?,erase_value=?,show=?,element_html=?,last_update_time=CURRENT_TIMESTAMP where id=? and query_id=?";
					jdbcTemplate.update(sql, new PreparedStatementSetter(){
						@Override
						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setString(1, parameter.getParameterInput().getTitle());
							ps.setString(2, parameter.getParameterInput().getDescription());
							ps.setString(3, parameter.getParameterInput().getHtmlType()+"");
							ps.setString(4, parameter.getParameterInput().getName());
							ps.setString(5, parameter.getParameterInput().getStyle());
							ps.setString(6, parameter.getParameterInput().getStyleClass());
							ps.setInt(7,(parameter.getParameterInput().getEraseValue()!=null && parameter.getParameterInput().getEraseValue())?1:0);
							ps.setInt(8, (parameter.getParameterInput().getShow()!=null && parameter.getParameterInput().getShow())?1:0);
							ps.setString(9, parameter.getParameterInput().getElementHtml());
							ps.setString(10, parameter.getId());
							ps.setString(11, parameter.getQueryDefinition().getId());
							
						}});
				}
				else
				{
					KeyHolder keyHolder = new GeneratedKeyHolder();
					jdbcTemplate.update(
					    new PreparedStatementCreator() {
					        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					            PreparedStatement ps =
					                connection.prepareStatement("insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)", new String[] {"ID"});/*这个自动生成键的字段的名称一定要大写，不然会报错*/

					            ps.setString(1, parameter.getQueryDefinition().getId());
								ps.setString(2, parameter.getParameterInput().getTitle());
								ps.setString(3, parameter.getParameterInput().getDescription());
								ps.setString(4, parameter.getParameterInput().getHtmlType()+"");
								ps.setString(5, parameter.getParameterInput().getName());
								ps.setString(6, parameter.getParameterInput().getStyle());
								ps.setString(7, parameter.getParameterInput().getStyleClass());
								ps.setInt(8, (parameter.getParameterInput().getEraseValue()!=null && parameter.getParameterInput().getEraseValue())?1:0);
								ps.setInt(9, (parameter.getParameterInput().getShow()!=null && parameter.getParameterInput().getShow())?1:0);
								ps.setString(10, parameter.getParameterInput().getElementHtml());
								
								return ps;
					        }
					    },
					    keyHolder);
					id = keyHolder.getKey()+"";
				}
				Parameter parameterById = getParameterById(id,resourceHolder);
				String content = showParameter(parameterById,queryDefinition);
				
				datas.put("html", content);
				
				
			} catch (Exception e) {
				infoDTO.setSuccess(false);
				infoDTO.setMsg("保存参数失败");//有时 +e.getMessage()中有方括号导致json出错
			}
			
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
		/**
		 * 把http请求的参数绑定到一个QueryDefinition对象上
		 * @param queryDefinition
		 * @return
		 */
		private Parameter bindDatas(QueryDefinition queryDefinition)
		{
			Parameter re =new Parameter();
			List<Parameter> parameters = queryDefinition.getParameters();
			final Map<String,Object> parameterMap=new HashMap<String,Object>();
			for (Parameter i : parameters) parameterMap.put(i.getParameterInput().getName(),i.getParameterInput().getValue());
			
			WebDataBinder webDataBinder =new WebDataBinder(re);
			MutablePropertyValues propertyValues=new MutablePropertyValues(parameterMap);
			webDataBinder.bind(propertyValues);
			
			return re;
		}
    }
    /**
     * 删除查询参数的类
     * @author zhi_liu
     *
     */
    private class QueryParameterDeleter implements QueryService
    {

		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition, ResourceHolder resourceHolder,
				Environment environment) {
			
			String parameterId = QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.ToDeleteParameterId.parameter.getParameterInput().getName());
			String queryId= QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.QueryDefinitionId.parameter.getParameterInput().getName());
			QueryResult re =new QueryResult();
			JdbcTemplate jdbcTemplate=null;
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> datas=new HashMap<String,Object>();
			try
			{
				jdbcTemplate=getSystemDBTemplate(resourceHolder);
				String sql="delete from m_sys_query_parameter where id=? and query_id=?";
				int deletedRows = jdbcTemplate.update(sql, parameterId,queryId);
				
				if(deletedRows>0)
				{
					infoDTO.setSuccess(true);
					infoDTO.setMsg("删除参数成功");
				}
			}
			catch(Exception e)
			{
				infoDTO.setSuccess(false);
				infoDTO.setMsg("删除参数出错了");
			}
			
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
    }
    /**
     * 使用ajax得到查询定义的数据
     * @author zhi_liu
     *
     */
    private class QueryDefinitionAjaxGetter implements QueryService
    {

		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition, ResourceHolder resourceHolder,
				Environment environment) {
			
			String queryId= QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.QueryDefinitionId.parameter.getParameterInput().getName());
			QueryResult re =new QueryResult();
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> datas=new HashMap<String,Object>();
			QueryDefinition queryDefinitionInDB=null;
			try {
				infoDTO.setData(datas);
				infoDTO.setSuccess(true);
				infoDTO.setMsg("得到查询定义成功");
				
				if(StringUtils.hasText(queryId))
				{
					queryDefinitionInDB=getQueryDefinitionById(queryId,resourceHolder);
				}
				String content = showQueryDefinition(queryDefinitionInDB,true,queryDefinition);
				datas.put("html", content);
				
			} catch (Exception e) {
				infoDTO.setSuccess(false);
				infoDTO.setMsg("得到查询定义出错了");
			}
			
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
    	
    }
    /**
     * 得到模板
     * @author liuqingzhi
     *
     */
    private class TemplateGetter implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition, ResourceHolder resourceHolder,
				Environment environment) {
			
			String templateId= QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.TemplateId.parameter.getParameterInput().getName());
			String queryId = QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.TemplateQueryId.parameter.getParameterInput().getName());
			QueryResult re =new QueryResult();
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> datas=new HashMap<String,Object>();
			ResultTemplate queryDefinitionInDB=null;
			try {
				infoDTO.setData(datas);
				infoDTO.setSuccess(true);
				infoDTO.setMsg("得到模板成功");
				
				if(StringUtils.hasText(queryId))
				{
					queryDefinitionInDB=getTemplateById(queryId,resourceHolder);
				}
				String content = showTemplate(queryDefinitionInDB,queryDefinition);
				datas.put("html", content);
				
			} catch (Exception e) {
				infoDTO.setSuccess(false);
				infoDTO.setMsg("得到模板出错了");
			}
			
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
    }
    /**
     * 保存模板
     * @author liuqingzhi
     *
     */
    private class TemplateSave implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re =new QueryResult();
			JdbcTemplate jdbcTemplate=null;
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> datas=new HashMap<String,Object>();
			
			try {
				
				infoDTO.setData(datas);
				infoDTO.setSuccess(true);
				infoDTO.setMsg("保存模板成功");
				
				jdbcTemplate=getSystemDBTemplate(resourceHolder);
				

				final ResultTemplate template = bindDatas(queryDefinition);
				
				String id = template.getId();
				String sql="";
				if(StringUtils.hasText(id))
				{
					sql="update m_sys_query_template set code=?,title=?,content=?,last_update_time=CURRENT_TIMESTAMP where id=? and query_id=?";
					jdbcTemplate.update(sql, new PreparedStatementSetter(){
						@Override
						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setString(1, template.getCode());
							ps.setString(2, template.getTitle());
							ps.setString(3, template.getContent());
							ps.setString(4, template.getId());
							ps.setString(5, template.getQueryDefinition().getId());
						}});
				}
				else
				{
					KeyHolder keyHolder = new GeneratedKeyHolder();
					jdbcTemplate.update(
					    new PreparedStatementCreator() {
					        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					            PreparedStatement ps =
					                connection.prepareStatement("insert into m_sys_query_template (query_id,code,title,content,last_update_time) values (?,?,?,?,CURRENT_TIMESTAMP)", new String[] {"ID"});/*这个自动生成键的字段的名称一定要大写，不然会报错*/

					            ps.setString(1, template.getQueryDefinition().getId());
								ps.setString(2, template.getCode());
								ps.setString(3, template.getTitle());
								ps.setString(4, template.getContent());
								
								return ps;
					        }
					    },
					    keyHolder);
					id = keyHolder.getKey()+"";
				}
				
				ResultTemplate templateById = getTemplateById(id,resourceHolder);
				String content = showTemplate(templateById,queryDefinition);
				
				datas.put("html", content);
			} catch (Exception e) {
				infoDTO.setSuccess(false);
				infoDTO.setMsg("保存参数失败");//有时 +e.getMessage()中有方括号导致json出错
			}
			
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
		/**
		 * 把http请求的参数绑定到一个ResultTemplate对象上
		 * @param queryDefinition
		 * @return
		 */
		private ResultTemplate bindDatas(QueryDefinition queryDefinition)
		{
			ResultTemplate re =new ResultTemplate();
			List<Parameter> parameters = queryDefinition.getParameters();
			final Map<String,Object> parameterMap=new HashMap<String,Object>();
			for (Parameter i : parameters) parameterMap.put(i.getParameterInput().getName(),i.getParameterInput().getValue());
			
			WebDataBinder webDataBinder =new WebDataBinder(re);
			MutablePropertyValues propertyValues=new MutablePropertyValues(parameterMap);
			webDataBinder.bind(propertyValues);
			
			return re;
		}
    }
    /**
     * 删除模板的类
     * @author zhi_liu
     *
     */
    private class TemplateDeleter implements QueryService
    {
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition, ResourceHolder resourceHolder,
				Environment environment) {
			
			String templateId = QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.ToDeleteTemplateId.parameter.getParameterInput().getName());
			String queryId= QueryUtils.getParameterValue(queryDefinition.getParameters(), ParameterName.QueryDefinitionId.parameter.getParameterInput().getName());
			QueryResult re =new QueryResult();
			JdbcTemplate jdbcTemplate=null;
			InfoDTO<Map<String,Object>> infoDTO=new InfoDTO<Map<String,Object>>();
			Map<String,Object> datas=new HashMap<String,Object>();
			try
			{
				jdbcTemplate=getSystemDBTemplate(resourceHolder);
				String sql="delete from m_sys_query_template where id=? and query_id=?";
				int deletedRows = jdbcTemplate.update(sql, templateId,queryId);
				
				if(deletedRows>0)
				{
					infoDTO.setSuccess(true);
					infoDTO.setMsg("删除模板成功");
				}
			}
			catch(Exception e)
			{
				infoDTO.setSuccess(false);
				infoDTO.setMsg("删除模板出错了");
			}
			
			re.setContent(gson.toJson(infoDTO));
			re.setOnlyShowContent(true);
			
			return re;
		}
    }
}
