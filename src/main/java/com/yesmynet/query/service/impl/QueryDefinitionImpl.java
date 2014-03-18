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
    	Command("是执行的命令","command","",ParameterHtmlType.InputHidden,"","","",true),
    	
    	QueryDefinitionId("查询的Id","id","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryDefinitionName("查询的名称","name","",ParameterHtmlType.InputText,"","","",true),
    	QueryDefinitionDescription("查询描述","description","",ParameterHtmlType.InputText,"","","",true),
    	QueryDefinitionJavaCode("查询代码","javaCode","",ParameterHtmlType.TextArea,"","","",true),
    	
    	QueryParameterQueryId("参数的查询的Id","queryDefinition.id","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterId("参数的Id","id","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterTitle("参数标题","parameterInput.title","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterDescription("参数描述","parameterInput.description","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterHtmlType("参数类型","parameterInput.htmlType","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterName("参数名称","parameterInput.name","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterStyle("参数css","parameterInput.style","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterStyleClass("参数css class","parameterInput.styleClass","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterOptionGetterKey("选项获取器","parameterInput.optionGetterKey","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterElementHtml("直接html","parameterInput.notShow","",ParameterHtmlType.InputHidden,"","","",true),
    	QueryParameterEraseValue("不回显参数值","parameterInput.eraseValue","",ParameterHtmlType.InputHidden,"","","",true),
    	
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
    		input.setNotShow(notShow);
    		
    		parameter.setParameterInput(input);
    	}
		public Parameter getParameter() {
			return parameter;
		}
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
	        input.setOptionGetterKey(rs.getString("option_getter_Key"));
	        
			
			return re;
		}
	};
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
		
	}
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
		}
		
    	return queryDefinitionInDB;
    }
    /**
     * 显示查询定义
     * @param queryDefinitionInDB
     * @return
     */
    private String showQueryDefinition(QueryDefinition queryDefinitionInDB,boolean ajaxShow) {
		Map<String,Object> toViewDatas=new HashMap<String,Object>();
		toViewDatas.put("queryDefinition", queryDefinitionInDB);
		toViewDatas.put("allHtmlTypes", ParameterHtmlType.values());
		toViewDatas.put("ajaxShow", ajaxShow);
		
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
     * 显示一个参数
     * @param paramter
     * @return
     */
    private String showParameter(Parameter paramter) {
		Map<String,Object> toViewDatas=new HashMap<String,Object>();
		toViewDatas.put("parameter", paramter);
		toViewDatas.put("allHtmlTypes", ParameterHtmlType.values());
		toViewDatas.put("yesOrNoOptions", getOptionsForYesOrNo());
		
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
     * 根据ID得到数据库中的查询定义得到
     * @author liuqingzhi
     *
     */
    public class QueryDefinitionGetter implements QueryService
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
			String content = showQueryDefinition(queryDefinitionInDB,false);
			re.setContent(content);
			return re;
		}
			
    }
    /**
     * 保存查询定义
     * @author liuqingzhi
     *
     */
    public class QueryDefinitionSave implements QueryService
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
				String content = showQueryDefinition(queryDefinitionById,true);
				
				datas.put("html", content);
				
			} catch (ServiceException e) {
				infoDTO.setSuccess(false);
				infoDTO.setMsg("保存查询定义失败，"+e.getMessage());
			}
			catch(Exception e)
			{
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
    public class QueryParameterGetter implements QueryService
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
				String showParameter = showParameter(parameter);
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
    public class QueryParameterSave implements QueryService
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
				infoDTO.setMsg("保存查询定义成功");
				
				jdbcTemplate=getSystemDBTemplate(resourceHolder);
				

				final Parameter parameter = bindDatas(queryDefinition);
				
				String id = parameter.getId();
				String sql="";
				

				if(StringUtils.hasText(id))
				{
					final int queryId=Integer.parseInt(id);
							
					sql="update m_sys_query_parameter set title=?,description=?,html_Type=?,name=?,style=?,style_class=?,not_show=?,last_update_time=CURRENT_TIMESTAMP where id=? and query_id=?";
					jdbcTemplate.update(sql, new PreparedStatementSetter(){
						@Override
						public void setValues(PreparedStatement ps) throws SQLException {
							ps.setString(1, parameter.getParameterInput().getTitle());
							ps.setString(2, parameter.getParameterInput().getDescription());
							ps.setString(3, parameter.getParameterInput().getHtmlType()+"");
							ps.setString(4, parameter.getParameterInput().getName());
							ps.setString(5, parameter.getParameterInput().getStyle());
							ps.setString(6, parameter.getParameterInput().getStyleClass());
							ps.setString(7, parameter.getParameterInput().getOptionGetterKey());
							ps.setInt(8, (parameter.getParameterInput().getNotShow()!=null && !parameter.getParameterInput().getNotShow())?1:0);
							ps.setString(9, parameter.getId());
							ps.setString(10, parameter.getQueryDefinition().getId());
							
						}});
				}
				else
				{
					KeyHolder keyHolder = new GeneratedKeyHolder();
					jdbcTemplate.update(
					    new PreparedStatementCreator() {
					        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					            PreparedStatement ps =
					                connection.prepareStatement("insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,not_show,last_update_time) values (?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)", new String[] {"ID"});/*这个自动生成键的字段的名称一定要大写，不然会报错*/

					            ps.setString(1, parameter.getQueryDefinition().getId());
								ps.setString(2, parameter.getParameterInput().getTitle());
								ps.setString(3, parameter.getParameterInput().getDescription());
								ps.setString(4, parameter.getParameterInput().getHtmlType()+"");
								ps.setString(5, parameter.getParameterInput().getName());
								ps.setString(6, parameter.getParameterInput().getStyle());
								ps.setString(7, parameter.getParameterInput().getStyleClass());
								ps.setInt(8, (parameter.getParameterInput().getNotShow()!=null && !parameter.getParameterInput().getNotShow())?1:0);

								return ps;
					        }
					    },
					    keyHolder);
					id = keyHolder.getKey()+"";
				}
				Parameter parameterById = getParameterById(id,resourceHolder);
				String content = showParameter(parameterById);
				
				datas.put("html", content);
				
				
			} catch (Exception e) {
				infoDTO.setSuccess(false);
				infoDTO.setMsg("保存查询定义失败，"+e.getMessage());
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
}
