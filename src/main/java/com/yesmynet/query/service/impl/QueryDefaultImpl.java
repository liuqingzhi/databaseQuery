package com.yesmynet.query.service.impl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.DatabaseDialect;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.InfoDTO;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.ParameterLayoutDTO;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.ResultStream;
import com.yesmynet.query.core.dto.ResultTemplate;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.core.exception.ServiceException;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.http.dto.SystemParameterName;
import com.yesmynet.query.service.DatabseDialectService;
import com.yesmynet.query.utils.FreemarkerUtils;
import com.yesmynet.query.utils.QueryUtils;
import com.yesmynet.query.utils.SqlSplitUtils;
import com.yesmynet.query.utils.dto.SqlDto;
/**
 * 查询的默认实现，就是在界面上显示一个输入sql的多行文本框，用来执行给定的SQL
 * @author 刘庆志
 *
 */
public class QueryDefaultImpl implements QueryService,QueryDefinitionGetter
{
	 private Gson gson = new GsonBuilder().serializeNulls().create();
	 
	Logger logger=LoggerFactory.getLogger(this.getClass());
    /**
     * 关于本查询的设置，包括所有的参数
     */
    private QueryDefinition queryDefinition;
    /**
     * 所有可用的数据库言
     */
    private List<DatabseDialectService> databaseDialectServices;
    /**
     * 查询数据库时，不进行分页的最多记录数，如果查询的记录数大于本参数则进行分页
     * 如果设为负数意味着查询总是分页。
     */
    private Long noPageMaxResult=-1L;
    /**
     * 查询分页时，一页显示的记录数的最大数
     */
    private Long maxPageSize=100L;
    /**
     * 默认每页显示的记录数
     */
    private Long pageSizeDefault=20L;
    /**
     * 表示提交的参数与要执行的命令的Map
     */
    private Map<String,QueryService> commandQueryMap;
    /**
     * 构造函数
     */
    public QueryDefaultImpl() {
		super();
		commandQueryMap=new HashMap<String,QueryService>();
		commandQueryMap.put(null, new EmptyResult());
		commandQueryMap.put("", new ExecuteDBQuery());
		commandQueryMap.put("streamLobQuery", new StreamLobQuery());
		
		
	}
	public QueryResult doInQuery(QueryDefinition queryDefinition,ResourceHolder resourceHolder,Environment environment)
    {
		QueryResult re =null;
		List<Parameter> parameters = queryDefinition.getParameters();
		String command = QueryUtils.getParameterValue(parameters, ParameterName.Command.getParameter().getParameterInput().getName());
		
		settingParameterOptions(parameters,resourceHolder);
		
		QueryService commandQuery = commandQueryMap.get(command);
		if(commandQuery!=null)
			re=commandQuery.doInQuery(queryDefinition, resourceHolder, environment);
		return re;
    }
    /**
     * 根据用户选择的数据库ID得到数据库
     * @param dbId
     * @param resourceHolder
     * @return
     */
    private InfoDTO<DataSourceConfig> getDataSourceConfig(String dbId,ResourceHolder resourceHolder)
    {
    	InfoDTO<DataSourceConfig> re=new InfoDTO<DataSourceConfig>();
    	re.setSuccess(true);
    	re.setMsg("操作成功");
    	if(!StringUtils.hasText(dbId))
    	{
    		re.setSuccess(false);
    		re.setMsg("没有选择要执行的目标数据库");
    		return re;
    	}
    	List<DataSourceConfig> dataSourceConfigs = resourceHolder.getDataSourceConfigs();
    	if(CollectionUtils.isEmpty(dataSourceConfigs))
    	{
    		re.setSuccess(false);
    		re.setMsg("您没有权限操作数据库");
    		return re;
    	}
    	DataSourceConfig foundDB=null;
    	for(DataSourceConfig db:dataSourceConfigs)
    	{
    		String id = db.getId();
    		if(id.equals(dbId))
    		{
    			foundDB=db;
    		}
    	}
    	if(foundDB==null)
    	{
    		re.setSuccess(false);
    		re.setMsg("您没有权限操作目标数据库");
    		return re;
    	}
    	re.setData(foundDB);
    	return re;
    }
    /**
     * 在statement中实现分页，这可能会有性能问题。
     * 我试了在sql中实现分页，会查出一些奇怪的结果集，如下的sql:SELECT b.* FROM (
		SELECT a.*,ROWNUM num FROM (select * From m_trading_transaction_balance t1 
		join m_trading_goods t2 on t1.seller_trading_id=t2.id
		order by t1.id) a where ROWNUM  <=  20 ) b WHERE num  >=  1
		字段名都是很奇怪的。
     * @param sql
     * @param pageSize
     * @param currentPage
     * @param dataSourceConfig
     * @param sqlIndex
     * @param showResultDivId
     * @return
     */
    
    private String executeSelectSqlPageInStatement(String sql,Long pageSize,Long currentPage,DataSourceConfig dataSourceConfig,int sqlIndex,String showResultDivId)
    {
    	String re="";
    	DatabaseDialect databaseDialect = dataSourceConfig.getDatabaseDialect();
    	DatabseDialectService databaseDialectService = getDatabaseDialectService(databaseDialect);
    	DataSource datasource2 = dataSourceConfig.getDatasource();
        JdbcTemplate jdbcTemplate=new JdbcTemplate(datasource2);
        
        boolean paging=false;
        PagingDto pagingInfo=null; 
        String sqlToExecute=sql;
        
    	if(databaseDialectService!=null)
    	{
    		String pagingCountSql = getPagingCountSql(sql);
    		
            long resultCount = jdbcTemplate.queryForLong(pagingCountSql);
            if(resultCount>noPageMaxResult)
            {
            	//进行分页
            	pagingInfo = getPagingInfo(resultCount,pageSize,currentPage);
            	paging=true;
            	//sqlToExecute=databaseDialectService.getPagingSql(databaseDialect, sql, pagingInfo.getRecordBegin(), pagingInfo.getRecordEnd());
            }
    	}
    	final String sqlToExecuteSql=sqlToExecute;
    	final Long recordBegin=(pagingInfo==null?1:pagingInfo.getRecordBegin());
    	final Long recordEnd=(pagingInfo==null?-1:pagingInfo.getRecordEnd());
    	final StopWatch stopWatch=new StopWatch();
    	
        re=jdbcTemplate.execute(new ConnectionCallback<String>(){

			public String doInConnection(Connection con) throws SQLException,
					DataAccessException {
				String re="";
				
				try
                {
					
					stopWatch.start("执行查询");
					Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = stmt.executeQuery(sqlToExecuteSql);
					stopWatch.stop();
					
                    if(rs!=null)
                    {
                    	stopWatch.start("ResultSet中滚动以实现分页");
                    	//rs.first();
                    	//rs.relative(recordBegin.intValue()-1);
                    	int resultSetPageStart=recordBegin.intValue()-1;//国为在输出ResultSet时行调用了ResultSet的next()方法，所以这里少向前滚动了一条数据
                    	if(resultSetPageStart>0)
                    		rs.absolute(resultSetPageStart);
                    	stopWatch.stop();
                    	
                    	stopWatch.start("输出ResultSet中的数据");
                    	re=ShowResultSet(rs,recordBegin,recordEnd);
                    	stopWatch.stop();
                    }
                    
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                return re;
				
			}});
    	if(paging)
    	{
    		//String sqlIndexDivId="sqlPageDiv"+sqlIndex;
    		//分页了，则显示分页导航
    		
    		String showResultDivIdContainer=getRandomString();
    		//String pageDataDivIdContainer=getRandomString();
    		String page1=getPageNavigation(pagingInfo,showResultDivIdContainer,true);
    		String page2=getPageNavigation(pagingInfo,showResultDivIdContainer,false);
    		String pageDatas=showPagingNavigation(sql,dataSourceConfig,pagingInfo);//,pageDataDivIdContainer);
    		
    		re="<div id='"+ showResultDivIdContainer +"'>"+pageDatas+page1+re;
    		String prettyPrint = stopWatch.prettyPrint();
    		re+="<pre>"+ prettyPrint +"</pre>";
    		re+=page2;
    		re+="</div>";
    	}
    	
    	return re;
    }
    /**
     * 在页面上定义一个javascript函数，以进行分页操作。
     * @return
     */
    private String getPageNavigationScriptFunction(QueryDefinition queryDefinition)
    {
    	StringBuilder re=new StringBuilder();
    	
    	re.append("<div id=\"ajaxtip\" title=\"正在执行查询\" style=\"display:none;\">\n");
    	re.append("	<img id='ajaxtipImage' src='' style='vertical-align:middle; width:25px; height:25px; margin-right:5px; display:inline;' />正在执行查询，请稍候...\n");  
    	re.append("</div>\n");
    	
    	re.append("<script type=\"text/javascript\">\n");
    	re.append("		function goPage(toReplaceContentDivId,targetPageNum)\n");
    	re.append("		{\n");
    	re.append("			var url=requestContext+\"/query.do\";\n");
    	re.append("			var datasourceId=$(\"#\"+toReplaceContentDivId+\" #SystemDataSourceId\").val();\n");
    	re.append("			var sql=$(\"#\"+toReplaceContentDivId+\" #sqlCode\").val();\n");
    	re.append("			var queryId='").append(queryDefinition.getId()).append("';\n");
    	re.append("			var dbId=$(\"[name='").append(ParameterName.DbId.getParameter().getParameterInput().getName()).append("']\").val();\n");
    	
    	
    	re.append("			\n");
    	re.append("			$.ajax({\n");
    	re.append("				  type: \"POST\",\n");
    	re.append("				  url: url,\n");
    	re.append("				  dataType:\"html\",\n");
    	re.append("				  data: { \"").append(SystemParameterName.QueryId.getParamerName()).append("\":queryId,\"").append(ParameterName.DbId.getParameter().getParameterInput().getName()).append("\":dbId,\"command\":\"\",\"ajaxRequest\":\"1\",\"SystemDataSourceId\": datasourceId, \"sqlCode\": sql,\"currentPage\":targetPageNum },\n");
    	re.append("				  beforeSend:function() {\n");
    	re.append("				  	var ajaxtipImage =$( \"#ajaxtipImage\" );\n");
    	re.append("				  	var ajaxtip=$( \"#ajaxtip\" );\n");
    	re.append("				  	ajaxtipImage.attr('src',requestContext+'/image/loading.gif');\n");
    	re.append("				  	ajaxtip.css('position','absolute');\n");
    	re.append("				  	ajaxtip.css('top', Math.max(0, (($(window).height() - ajaxtip.outerHeight()) / 2) + $(window).scrollTop()) + 'px');\n");
    	re.append("				  	ajaxtip.css('left', Math.max(0, (($(window).width() - ajaxtip.outerWidth()) / 2) + $(window).scrollLeft()) + 'px');\n");
    	re.append("				  	ajaxtip.show();\n");
    	re.append("				  	 },\n");
    	re.append("				  success: function(data, textStatus, jqXHR) {\n");
    	re.append("				  	$( \"#ajaxtip\" ).hide();\n");
    	re.append("					  $(\"#\"+toReplaceContentDivId).html(data);\n");
    	re.append("				  }\n");
    	re.append("				});\n");
    	re.append("		}\n");
    	re.append("	</script>\n");
    	
    	return re.toString();
    }
    /**
     * 得到定义快捷键的javascript
     * @return
     */
    private String addShortCutKeyScript()
    {
    	StringBuilder sb=new StringBuilder();
    	sb.append("<script type=\"text/javascript\">\n");
    	sb.append("function submitQueryForm()\n");
    	sb.append("{\n");
    	sb.append("$(\"#queryForm\").submit();\n");
    	sb.append("}\n");
    	sb.append("shortcut.add(\"F8\", submitQueryForm);//定义快捷键\n");
    	sb.append("</script>");
    	return sb.toString();
    }
    /**
     * 得到为了执行用户选中的文本的功能的相关javascript
     * @return
     */
    private String getExecuteSelectedSqlScript()
    {
    	String re="";
    	
    	re=""+
    			"<script type=\"text/javascript\">\n"+
    			"	var getSelected = function(){\n"+
    			"	    var t = '';\n"+
    			"	    if(window.getSelection) {\n"+
    			"	        t = window.getSelection();\n"+
    			"	    } else if(document.getSelection) {\n"+
    			"	        t = document.getSelection();\n"+
    			"	    } else if(document.selection) {\n"+
    			"	        t = document.selection.createRange().text;\n"+
    			"	    }\n"+
    			"	    return t;\n"+
    			"	}\n"+
    			"	\n"+
    			"	$(\"[name='sqlCode']\").select(function(eventObject) {\n"+
    			"		var selectedText=getSelected().toString();\n"+
    			"		var selectedSqlInput=$(\"input[name='selectedSql']\") \n"+
    			"		selectedText=selectedText.replace(/^\\s+|\\s+$/g,'');\n"+
    			"		\n"+
    			"		if(selectedText.length>1)\n"+
    			"		{\n"+
    			"			selectedSqlInput.val(selectedText);	\n"+
    			"		}\n"+
    			"		else\n"+
    			"		{\n"+
    			"			selectedSqlInput.val('');\n"+
    			"		}\n"+
    			"		\n"+
    			"		\n"+
    			"	});\n"+
    			"	$(\"[name='sqlCode']\").click(function(eventObject) {\n"+
    			"		var selectedSqlInput=$(\"input[name='selectedSql']\") \n"+
    			"		selectedSqlInput.val('');\n"+
    			"		\n"+
    			"	});\n"+
    			
    			
    			"</script>\n"+
    			"";
    	return re;
    	
    }
    private String getLobDownloadScript()
    {
    	StringBuilder re=new StringBuilder();
    	re.append("<script type=\"text/javascript\">\n");
    			re.append("			$(document).delegate(\".clobViewButton\", \"click\", function() {\n");
    			re.append("				showDBLobSql();\n");
    			re.append("			});\n");
    			re.append("			$(document).delegate(\".blobViewButton\", \"click\", function() {\n");
    			re.append("				showDBLobSql();\n");
    			re.append("			});\n");
    			re.append("			\n");
    			re.append("			function showDBLobSql(command)\n");
    			re.append("			{\n");
    			re.append("				var formStr=\"<form id='lobDownloadForm' target='_blank'>\"+\n");
    			re.append("							\"<input type='hidden' name='SystemQueryId' value='dqQuery'>\"+\n");
    			re.append("							\"<input type='hidden' name='command' value='streamLobQuery'>\"+\n");
    			re.append("							\"<input type='hidden' name='dbId'>\"+\n");
    			re.append("							\"sql：<textarea rows='6' cols='60' name='sqlCode'></textarea><br>\"+\n");
    			re.append("							\"SQL的select子句中的第1个字段必须是大字段的字段名,SQL的最后不能有分号（‘;’），正确的SQL格式如下：select clob_blob_field from table_name where condition \"+\n");
    			re.append("							\"</form>\"\n");
    			re.append("				\n");
    			re.append("				var tag = $(\"#dbqueryDialogContainer\").html(formStr);\n");
    			re.append("				tag.dialog({\n");
    			re.append("				      modal: true, title: '下载大字段的sql', zIndex: 10000, autoOpen: true,\n");
    			re.append("				      width: '650px', resizable: true,\n");
    			re.append("				      buttons: [\n");
    			re.append("				      		{\n");
    			re.append("								text:\"确认\",\n");
    			re.append("								click:function(){\n");
    			re.append("									//var toSubmitData={'SystemQueryId':'dqQuery','command':'streamLobQuery','dbId':'','sqlCode':''};\n");
    			re.append("									var url=$(location).attr('pathname');\n");
    			re.append("									$('#lobDownloadForm').attr(\"action\",url);\n");
    			re.append("									$(\"#lobDownloadForm input[name='dbId']\").val($(\"#queryForm [name='dbId']\").val());\n");
    			re.append("									$('#lobDownloadForm').submit();\n");
    			re.append("									\n");
    			re.append("									$(this).dialog(\"close\");\n"); 
    			re.append("								} \n");
    			re.append("							},\n");
    			re.append("				      		{\n");
    			re.append("								text:\"取消\",\n");
    			re.append("								click:function(){\n");
    			re.append("									$(this).dialog(\"close\");\n"); 
    			re.append("								} \n");
    			re.append("							}\n");
    			re.append("				      ]\n");
    			re.append("				      \n");
    			re.append("				});\n");
    			re.append("				\n");
    			re.append("			}\n");
    			re.append("		</script>\n");
    	return re.toString();
    }
    /**
     * 得到tab选项卡的头，用来点击这个头可以显示tab页的内容。
     * @param sql 执行的SQL
     * @param tabListIndex 这个tab是所有tab选项卡的第几个。
     * @return
     */
    private String getTabHeader(SqlDto sql,int tabListIndex,String tabContentDivId)
    {
    	String re="<li><a href=\"#"+ tabContentDivId +"\">SQL "+ tabListIndex +"</a></li>\n";
    	return re;
    }
    /**
     * 得到tab选项页的内容
     * @param content
     * @return
     */
    private String getTabContent(String content,String tabResultDivId)
    {
    	String re="";
    	re="<div id='"+ tabResultDivId +"'>"+ content +"</div>";
    	return re;
    }
    private String executeUpdataSql(final String sql,DataSourceConfig dataSourceConfig)
    {
    	String re="";
    	DataSource datasource2 = dataSourceConfig.getDatasource();
        JdbcTemplate jdbcTemplate=new JdbcTemplate(datasource2);
        
        int update =0;
        update=jdbcTemplate.update(sql);//为了能自动提交事务,所以直接操作Connection
       /* update=jdbcTemplate.execute(new ConnectionCallback<Integer>(){

			public Integer doInConnection(Connection con) throws SQLException,
					DataAccessException {
				Integer re=0;
				
				try
                {
					con.setAutoCommit(true);
					Statement statement = con.createStatement();
					
					re=statement.executeUpdate(sql);
					con.commit();
					
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                return re;
				
			}});*/
        
        re=String.format("更新了%s条记录",update);
    	return re;
    }
    /**
     * 把Exception显示为字符串
     * @param e
     * @return
     */
    private String doWithException(Exception e)
    {
    	String re="<pre>";
    	re+=ExceptionUtils.getStackTrace(e);
	    re+="</pre>";
    	return re;
    }
    /**
     * 根据参数名称得到参数值
     * @param parameterMap
     * @param parameterName
     * @return
     */
    private Long getParameterValue(List<Parameter> parameterMap,String parameterName,Long defaultValue)
    {
    	Long re=null;
		
		String pageSizeStr = QueryUtils.getParameterValue(parameterMap, parameterName);
		try
		{
			re=Long.parseLong(pageSizeStr);
		}
		catch (NumberFormatException e)
		{
		}
		
		if(re==null) re=defaultValue;
		return re;
    }
    /**
     * 得到一个随机字符串作为显示查询结果的div的ID，这个ID不能重复，以准备使用ajax更新结果
     * @return
     */
    private String getRandomString()
    {
    	String re="";
    	re=UUID.randomUUID().toString();
    	return re;
    }
    /**
     * 输出用于分页控制的一些变量 
     * @param sql 要执行的SQL，是用户提交的sql，不带分页
     * @param dataSourceConfig 数据源配置
     * @param pagingInfo 分页的信息
     * @param sqlIndex 当前执行的SQL是所有SQL中的第几个SQL语句，用此序号在html中产生一个ID
     * @return 在html中分页相关的所有数据
     */
    private String showPagingNavigation(String sql,DataSourceConfig dataSourceConfig,PagingDto pagingInfo)//,String sqlIndexDivId)
    {
    	StringBuilder re=new StringBuilder();
    	if(pagingInfo!=null)
    	{
	    	//re.append("<div id='").append(sqlIndexDivId).append("' style='display:none;'>");
	    	re.append("<textarea id='"+ ParameterName.SQL.getParameter().getParameterInput().getName() +"' style='display:none;'>").append(sql).append("</textarea>\n");//使用textarea以避免sql中的特殊符号导致html出错
	    	//re.append("<input type='hidden' id='"+ SystemParameterName.DataSourceId.getParamerName() +"' value='"+ dataSourceConfig.getId() +"'>\n");
	    	re.append("<input type='hidden' id='"+ ParameterName.PageSize.getParameter().getParameterInput().getName() +"' value='"+ pagingInfo.getPageSize() +"'>\n");
	    	re.append("<input type='hidden' id='"+ ParameterName.CurrentPage.getParameter().getParameterInput().getName() +"' value='"+ pagingInfo.getCurrentPage() +"'>\n");
	    	re.append("<input type='hidden' id='"+ ParameterName.AjaxRequest.getParameter().getParameterInput().getName() +"' value=''>\n");
	    	
	    	//re.append("</div>");
    	}
    	return re.toString();
    }
    /**
     * 显示分页的导航，可以到达下一页，上一页等
     * @param pagingInfo
     * @return
     */
    private String getPageNavigation(PagingDto pagingInfo,String resultContentDivId,boolean beforeData)
    {
    	String re="";
    	if(pagingInfo!=null)
    	{
    		re=String.format("当前第%s页/共%s页，共%s条记录",pagingInfo.getCurrentPage(),pagingInfo.getPageCount(),pagingInfo.getRecordCount());
        	if(pagingInfo.getCurrentPage()>1)
        	{
        		re+=",<a href=\"javascript:goPage('"+ resultContentDivId +"','"+ (pagingInfo.getCurrentPage()-1) +"')\">上一页</a>";
        	}
        	else
        	{
        		re+=",<span>上一页</span>";
        	}
        	String goPageByNumInputName="goPageByNum"+(beforeData?"beforeData":"afterData") ;
        	String goPageNum="$('#"+ resultContentDivId +" #"+ goPageByNumInputName +"').val()";
        	re+=String.format(",到第<input type=\"text\" name=\"%s\" id=\"%s\" value=\"%s\" size=\"3\">页%s",goPageByNumInputName,goPageByNumInputName,pagingInfo.getCurrentPage(),pagingInfo.getPageCount()>0?"<a href=\"javascript:goPage('"+ resultContentDivId +"',"+ goPageNum +")\">确定</a>":"<span>确定</span>" );
        	
        	if(pagingInfo.getCurrentPage()<pagingInfo.getPageCount())
        	{
        		re+=",<a href=\"javascript:goPage('"+ resultContentDivId +"','"+ (pagingInfo.getCurrentPage()+1) +"')\">下一页</a>";
        	}
        	else
        	{
        		re+=",<span>下一页</span>";
        	}
    	}
    	
    	return re;
		
    }
    /**
     * 得到分页的数据
     * @param recordCountInDB 数据库中查询出的记录总数
     * @param pageSize 每页显示的记录数
     * @param currentPage 当前页码
     * @return 得到的整数数组，第1个元素是
     */
    private PagingDto getPagingInfo(Long recordCountInDB,Long pageSize,Long currentPage)
    {
    	PagingDto re=new PagingDto();
    	Long pageSizeSelf=pageSize>maxPageSize?maxPageSize:pageSize;
    	Long pageCount=recordCountInDB/pageSizeSelf;
    	pageCount+=recordCountInDB % pageSizeSelf>0?1:0;
    	Long currentPageSelf=currentPage>pageCount?pageCount:currentPage;
    	if(currentPageSelf<1)
    		currentPageSelf=1L;
    	Long recordBegin=(currentPageSelf-1)*pageSizeSelf+1;
    	Long recordEnd= currentPageSelf*pageSizeSelf;
    	
    	re.setCurrentPage(currentPageSelf);
    	re.setPageSize(pageSizeSelf);
    	re.setRecordBegin(recordBegin);
    	re.setRecordEnd(recordEnd);
    	re.setPageCount(pageCount);
    	re.setRecordCount(recordCountInDB);
    	
    	return re;
    }
    /**
     * 得到总记录数的sql
     * @param sql
     * @return
     */
    private String getPagingCountSql(String sql)
    {
    	String re="";
    	re+="SELECT count(*) as cnt from ( "+sql+" )a";
    	return re;
    }
    /**
     * 得到要进行分页查询的sql的言处理的service
     * @param databaseDialect 数据源的方言
     * @return
     */
    private DatabseDialectService getDatabaseDialectService(DatabaseDialect databaseDialect)
    {
    	DatabseDialectService re=null;
    	if(!CollectionUtils.isEmpty(databaseDialectServices))
    	{
    		for(DatabseDialectService dialectService:databaseDialectServices)
    		{
    			if(dialectService.isSupport(databaseDialect))
    			{
    				re=dialectService;
    				break;
    			}
    		}
    	}
    	return re;
    }
    /**
     * 显示结果
     * @param rs
     * @param recordBegin 输出rs中的数据时，用于显示当前输出的第1条数据是整个ResultSet中第几条数据。
     * @param recordEnd 输出rs中到哪条数据，1表示第1条记录，2表示第二条记录，依此类推，特殊的，用负数表示输出全部记录。
     * @return
     * @throws Exception
     */
    private String ShowResultSet(final java.sql.ResultSet rs,final Long recordBegin,Long recordEnd) throws Exception
    {
        StringBuffer sb=new StringBuffer();
        StringBuffer sbReturnValue=new StringBuffer();
        java.sql.ResultSetMetaData rsmd =null; 
        int iColumnCount=0,i=0;
        String strCurrentColumnTypeName="",strCurrentColumnValue="",strColumnLength="";
        Long iResultSetCountBegin=recordBegin;
        Long iResultSetCount=iResultSetCountBegin;
        boolean hasRecord=false;
        try
        {
            //sb.append("<table width=100% border=1 >\n");
            if (rs!=null)
            {
                rsmd =rs.getMetaData();
                iColumnCount=rsmd.getColumnCount();
                //输出表头
                sb.append(" <tr>\n");
                for (i=1;i<=iColumnCount;i++)
                {
                    sb.append("     <th>"+rsmd.getColumnName(i)+"</th>\n");
                }
                sb.append(" </tr>\n");
                sb.append(" <tr>\n");
                strColumnLength="";
                for (i=1;i<=iColumnCount;i++)
                {
                    strColumnLength=repalceNull(rsmd.getScale(i)+"","0");
                    strColumnLength=rsmd.getPrecision(i)+""+(strColumnLength.equals("0")? "":","+strColumnLength);
                    strColumnLength=repalceNull(strColumnLength,"0");
                    
                    strColumnLength=strColumnLength.equalsIgnoreCase("0")?rsmd.getColumnDisplaySize(i)+"":strColumnLength;
                    sb.append("     <th>"+rsmd.getColumnTypeName(i)+"("+ strColumnLength +")"+"</th>\n");
                                    

                }
                sb.append(" </tr>\n");
                
                iResultSetCount--;
                //输出数据
                while(rs.next())
                {
                    sb.append(" <tr>\n");
                    for (i=1;i<=iColumnCount;i++)
                    {
                        strCurrentColumnTypeName=rsmd.getColumnTypeName(i);
                        if (strCurrentColumnTypeName.equalsIgnoreCase("blob"))
                            strCurrentColumnValue="<a class='blobViewButton'>&lt;Blob&gt;</a>";
                        else if (strCurrentColumnTypeName.equalsIgnoreCase("clob"))
                            strCurrentColumnValue="<a class='clobViewButton'>&lt;Clob&gt;</a>";
                        else if (strCurrentColumnTypeName.equalsIgnoreCase("text"))
                            strCurrentColumnValue="<a class='clobViewButton'>&lt;Text&gt;</a>";
                        else if (strCurrentColumnTypeName.equalsIgnoreCase("image"))
                            strCurrentColumnValue="<a class='blobViewButton'>&lt;Image&gt;</a>";
                        else
                            strCurrentColumnValue=rs.getString(i);
                            
                        sb.append("     <td>"+ strCurrentColumnValue +"</td>\n");
                    }
                    sb.append(" </tr>\n");
                    iResultSetCount++;
                    hasRecord=true;
                    
                    if(recordEnd>0 && iResultSetCount>=recordEnd)
                    	break;
                }
                
            }
            
            if(!hasRecord)
            {
            	//这说明ResultSet是空的，没有数据
            	iResultSetCountBegin=0L;
            	iResultSetCount=0L;
            }
            
            
            sbReturnValue.append("<table width=100% border=1 >\n");
            sbReturnValue.append("  <tr>\n");
            sbReturnValue.append("      <th align='left' colspan='"+ iColumnCount +"'>当前显示从第<span style='color:red'>"+ iResultSetCountBegin +"</span>条到第<span style='color:red'>"+ iResultSetCount +"</span>条记录</th>\n");
            sbReturnValue.append("  <tr>\n");
            sbReturnValue.append(sb);
            sbReturnValue.append("</table>\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        
        return sbReturnValue.toString();
    }
    private String repalceNull(String src,String toReplaceNull)
    {
        String re=src;
        if(!StringUtils.hasText(src))
            re=toReplaceNull;
        return re;
            
    }
    /**
     * 表示查询中定义的所有参数
     * @author liuqingzhi
     *
     */
    private enum ParameterName
    {
    	Command("是执行的命令","command","",ParameterHtmlType.InputHidden,"","","",true,null,null,null),
    	ExecuteButton("查询按钮","executeButton","",ParameterHtmlType.Button,"","","onclick='$(\"#queryForm\").submit();'",true,30,1,1),
    	
    	SQL("SQL脚本","sqlCode","",ParameterHtmlType.TextArea,"width:1000px; height:200px;","","",true,10,1,2),
    	PageSize("每页显示的记录数","pageSize","",ParameterHtmlType.InputHidden,null,null,null,true,null,null,null),
    	CurrentPage("当前页码","currentPage","",ParameterHtmlType.InputHidden,null,null,null,true,null,null,null),
    	AjaxRequest("是否为ajax请求","ajaxRequest","",ParameterHtmlType.InputHidden,null,null,null,true,null,null,null),
    	SelectedSql("选中的sql","selectedSql","",ParameterHtmlType.InputHidden,null,null,null,true,null,null,null),
    	DbId("数据库","dbId","",ParameterHtmlType.Select,null,null,null,true,20,1,1),
    	
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
    	private ParameterName(String title,String name,String description,ParameterHtmlType htmlType,String style,String styleClass,String elementHtml,Boolean notShow,Integer sort,Integer rowSpan,Integer columnSpan)
    	{
    		parameter=new Parameter();
    		ParameterInput input=new ParameterInput();
    		ParameterLayoutDTO parameterLayoutDTO=new ParameterLayoutDTO();
    		
    		input.setTitle(title);
    		input.setDescription(description);
    		input.setHtmlType(htmlType);
    		input.setName(name);
    		input.setStyle(style);
    		input.setStyleClass(styleClass);
    		input.setElementHtml(elementHtml);
    		input.setShow(notShow);
    		
    		parameterLayoutDTO.setSort(sort);
    		parameterLayoutDTO.setRowSpan(rowSpan);
    		parameterLayoutDTO.setColumnSpan(columnSpan);
    		
    		parameter.setParameterInput(input);
    		parameter.setParameterLayoutDTO(parameterLayoutDTO);
    	}
		public Parameter getParameter() {
			return parameter;
		}
    }
	
	/**
	 * 表示分页查询的参数
	 * @author 刘庆志
	 *
	 */
	private class PagingDto
	{
		/**
		 * 每页显示的记录数
		 */
		private Long pageSize;
		/**
		 * 当前页码
		 */
		private Long currentPage;
		/**
		 * 总页数
		 */
		private Long pageCount;
		/**
		 * 总记录数
		 */
		private Long recordCount;
		/**
		 * 显示的记录是从第条记录开始的
		 */
		private Long recordBegin;
		/**
		 * 显示的记录是到第几条记录结束的
		 */
		private Long recordEnd;
		public Long getPageSize()
		{
			return pageSize;
		}
		public void setPageSize(Long pageSize)
		{
			this.pageSize = pageSize;
		}
		public Long getCurrentPage()
		{
			return currentPage;
		}
		public void setCurrentPage(Long currentPage)
		{
			this.currentPage = currentPage;
		}
		public Long getPageCount()
		{
			return pageCount;
		}
		public void setPageCount(Long pageCount)
		{
			this.pageCount = pageCount;
		}
		public Long getRecordCount()
		{
			return recordCount;
		}
		public void setRecordCount(Long recordCount)
		{
			this.recordCount = recordCount;
		}
		public Long getRecordBegin()
		{
			return recordBegin;
		}
		public void setRecordBegin(Long recordBegin)
		{
			this.recordBegin = recordBegin;
		}
		public Long getRecordEnd()
		{
			return recordEnd;
		}
		public void setRecordEnd(Long recordEnd)
		{
			this.recordEnd = recordEnd;
		}
		
	}
	/**
	 * 表示空的查询
	 * @author liuqingzhi
	 *
	 */
	private class EmptyResult implements QueryService
	{
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re=new QueryResult();
			
			re.setContent(addShortCutKeyScript());
			return re;
		}
		/**
		 * 测试使用freemarker模板显示查询结果
		 * @param queryDefinition
		 * @return
		 */
		private String getResultByFreemarker(QueryDefinition queryDefinition)
		{
			List<ResultTemplate> templates = queryDefinition.getTemplates();
			ResultTemplate template = QueryUtils.getTemplateByName(templates, "template1");
			Map<String,Object> datas=new HashMap<String,Object>();
			
			datas.put("now", new Date());
			
			return FreemarkerUtils.renderTemplateByContent(template.getContent(),datas);
		}
	}
	/**
	 * 执行数据库查询
	 * @author liuqingzhi
	 *
	 */
	private class ExecuteDBQuery implements QueryService
	{
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re=new QueryResult();
	        List<Parameter> parameters = queryDefinition.getParameters();
	        
	        StringBuilder resultContent=new StringBuilder();
	        String sql=QueryUtils.getParameterValue(parameters,ParameterName.SQL.getParameter().getParameterInput().getName());
	        String selectedSql=QueryUtils.getParameterValue(parameters,ParameterName.SelectedSql.getParameter().getParameterInput().getName());
	        final Boolean ajaxRequest=StringUtils.hasText(QueryUtils.getParameterValue(parameters,ParameterName.AjaxRequest.getParameter().getParameterInput().getName()));
	        String dbId = QueryUtils.getParameterValue(parameters,ParameterName.DbId.getParameter().getParameterInput().getName());
	        sql=StringUtils.hasText(selectedSql)?selectedSql:sql;//如果选中了sql，则只执行选中的部分
	        List<SqlDto> sqlList=SqlSplitUtils.splitSql(sql);
	        
	        
	        InfoDTO<DataSourceConfig> dataSourceConfigInfoDTO = getDataSourceConfig(dbId,resourceHolder);
	        
	        if(!dataSourceConfigInfoDTO.isSuccess())
	        {
	        	 re.setContent(dataSourceConfigInfoDTO.getMsg());
	             re.setOnlyShowContent(ajaxRequest);
	             return re;
	        }
	        DataSourceConfig dataSourceConfig = dataSourceConfigInfoDTO.getData();
	        
	        if(!CollectionUtils.isEmpty(sqlList))
	        {
	        	boolean tabbedContent=true;//是否使用tab选项卡显示内容
	        	int i=1;
	        	StringBuilder tabHeaders=new StringBuilder();
	        	StringBuilder tabContents=new StringBuilder();
	        	
	        	tabHeaders.append("<ul>");
	        	
	        	if(sqlList.size()>1) tabbedContent=true;
	        	
	        	for(SqlDto sqlDto:sqlList)
	        	{
	        		String sqlResult="";
	        		String tabShowResultDivId=getRandomString();//getShowResultDivId(i);
	        		Long pageSize=getParameterValue(parameters,ParameterName.PageSize.getParameter().getParameterInput().getName(),pageSizeDefault);
					Long currentPage=getParameterValue(parameters,ParameterName.CurrentPage.getParameter().getParameterInput().getName(),1L);
	        		try
					{
						if(sqlDto.isSelect())
						{
							//sqlResult=executeSelectSql(sqlDto.getSql(),pageSize,currentPage,dataSourceConfig,i,tabShowResultDivId);
							sqlResult=executeSelectSqlPageInStatement(sqlDto.getSql(),pageSize,currentPage,dataSourceConfig,i,tabShowResultDivId);
						}
						else
						{
							sqlResult=executeUpdataSql(sqlDto.getSql(),dataSourceConfig);
						}
					}
					catch (Exception e)
					{
						sqlResult=doWithException(e);
					}
	        		String tabHeader = getTabHeader(sqlDto,i,tabShowResultDivId);
	        		String tabContent=sqlResult;
	        		
	        		if(tabbedContent && !ajaxRequest)
	        			tabContent=getTabContent(sqlResult,tabShowResultDivId);
	        		
	        		tabHeaders.append(tabHeader);
	        		tabContents.append(tabContent);
	        		
	        		i++;
	        	}
	        	tabHeaders.append("</ul>\n");
	        	if(tabbedContent && !ajaxRequest)
	        	{
	        		resultContent.append("<div id='tabs'>\n");
	            	resultContent.append(tabHeaders);
	            	resultContent.append(tabContents);
	            	resultContent.append("</div>\n");

	            	resultContent.append("\n").append("<script>\n").append("$(function() {\n").append("$( \"#tabs\" ).tabs();\n").append("});\n").append("</script>\n");
	        	}
	        	else
	        	{
	        		resultContent.append(tabContents);
	        	}
	        	
	        	String pageNavigationScriptFunction = getPageNavigationScriptFunction(queryDefinition);
	        	resultContent.append(pageNavigationScriptFunction);
	        	resultContent.append(getExecuteSelectedSqlScript());
	        	resultContent.append(getLobDownloadScript());
	        }
	        if(!ajaxRequest)
	        {
	        	//如果是ajax请求就不能输出这个用来显示dialog的容器，那会导致显示多个dialog
	        	resultContent.append("<div id='dbqueryDialogContainer'></div>");
	        }
	        
	        resultContent.append(addShortCutKeyScript());
	        
	        re.setContent(resultContent.toString());
	        re.setOnlyShowContent(ajaxRequest);
	        return re;
		}
	}
	/**
	 * 表示输出流，如，查看clb字段，blob下载等。
	 * @author liuqingzhi
	 *
	 */
	private class StreamLobQuery implements QueryService
	{
		@Override
		public QueryResult doInQuery(final QueryDefinition queryDefinition,
				final ResourceHolder resourceHolder, final Environment environment) {
			QueryResult re=new QueryResult();
			
			List<Parameter> parameters = queryDefinition.getParameters();
			final String sql=QueryUtils.getParameterValue(parameters,ParameterName.SQL.getParameter().getParameterInput().getName());
	        final Boolean ajaxRequest=StringUtils.hasText(QueryUtils.getParameterValue(parameters,ParameterName.AjaxRequest.getParameter().getParameterInput().getName()));
	        String dbId = QueryUtils.getParameterValue(parameters,ParameterName.DbId.getParameter().getParameterInput().getName());
	        
			final InfoDTO<DataSourceConfig> dataSourceConfigInfoDTO = getDataSourceConfig(dbId,resourceHolder);
			DataSourceConfig dataSource = dataSourceConfigInfoDTO.getData();
			final JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSource.getDatasource());
			
			ResultStream stream=new ResultStream(){
				
				private final String encode="UTF-8";
				
				@Override
				public void write(final OutputStream outputStream) {
					jdbcTemplate.execute(new StatementCallback<Void>(){
						@Override
						public Void doInStatement(Statement stmt)
								throws SQLException, DataAccessException {
							
							try {
								ResultSet resultSet = stmt.executeQuery(sql);
								if(resultSet.next())
								{
									ResultSetMetaData metaData = resultSet.getMetaData();
									String columnTypeName = metaData.getColumnTypeName(1);
									 if (columnTypeName.equalsIgnoreCase("blob") || columnTypeName.equalsIgnoreCase("image"))
									 {
										 Blob blob = resultSet.getBlob(1);
										 InputStream binaryStream = blob.getBinaryStream();
										 IOUtils.copy(binaryStream, outputStream);
									 }
									 else if(columnTypeName.equalsIgnoreCase("clob") || columnTypeName.equalsIgnoreCase("text"))
									 {
										 Clob clob = resultSet.getClob(1);
										 Reader characterStream = clob.getCharacterStream();
										 IOUtils.copy(characterStream, outputStream,encode);
									 }
									 else
									 {
										 throw new ServiceException("不是Blob或Clob字段");
									 }
									
								}
							} catch (IOException e) {
								throw new ServiceException("写stream流出错了",e);
							}
							return null;
						}});
				}

				@Override
				public Long getLength() {
					Long execute = jdbcTemplate.execute(new StatementCallback<Long>(){
						@Override
						public Long doInStatement(Statement stmt)
								throws SQLException, DataAccessException {
							Long re=null;
							try {
								ResultSet resultSet = stmt.executeQuery(sql);
								if(resultSet.next())
								{
									ResultSetMetaData metaData = resultSet.getMetaData();
									String columnTypeName = metaData.getColumnTypeName(1);
									 if (columnTypeName.equalsIgnoreCase("blob") || columnTypeName.equalsIgnoreCase("image"))
									 {
										 Blob blob = resultSet.getBlob(1);
										 re=blob.length();
									 }
									 else if(columnTypeName.equalsIgnoreCase("clob") || columnTypeName.equalsIgnoreCase("text"))
									 {
										 //clob的clob.length()方法得到的字符数，不是字节数，所以，现在还没法得到clob的字节数。 
									 }
									 else
									 {
										 throw new ServiceException("不是Blob或Clob字段");
									 }
								}
							} catch (Exception e) {
								throw new ServiceException("写stream流出错了",e);
							}
							return re;
						}});
					
					return execute;
				}

				@Override
				public String getFileName() {
					return "test1";
				}

				@Override
				public String getContentType() {
					return null;
				}
				
			};
				
			re.setResultStream(stream);
			
			return re;
		}
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
	 * 设置参数的选项
	 * @param parameters
	 * @param resourceHolder 
	 */
	private void settingParameterOptions(List<Parameter> parameters,ResourceHolder resourceHolder)
	{
		Parameter parameterByName = QueryUtils.getParameterByName(parameters, "dbId");
		if(parameterByName!=null)
		{
			List<SelectOption> dbOptions = getDBOptions(resourceHolder.getDataSourceConfigs(),true);
			parameterByName.getParameterInput().setOptionValues(dbOptions);
		}
	}
	/**
	 * 得到要显示的所有数据库
	 * @param dbs
	 * @param generateAnEmptyOption
	 * @return
	 */
	private List<SelectOption> getDBOptions(List<DataSourceConfig> dbs,boolean generateAnEmptyOption)
	{
		List<SelectOption> re=new ArrayList<SelectOption>();
		if(generateAnEmptyOption)
		{
			SelectOption option=new SelectOption();
			option.setValue("");
			option.setText("");
			re.add(option);
		}
		
		if(!CollectionUtils.isEmpty(dbs))
		{
			for(DataSourceConfig db:dbs)
			{
				SelectOption option=new SelectOption();
				option.setValue(db.getId());
				option.setText(db.getName());
				re.add(option);
			}
		}
		
		return re;
	}
    public QueryDefinition getQueryDefinition()
    {
    	if(this.queryDefinition==null)
    	{
    		this.queryDefinition=initQueryDefinition();
    	}
        return this.queryDefinition;
    }
    public void setQueryDefinition(QueryDefinition queryDefinition)
    {
        this.queryDefinition = queryDefinition;
    }
	public List<DatabseDialectService> getDatabaseDialectServices() {
		return databaseDialectServices;
	}
	public void setDatabaseDialectServices(
			List<DatabseDialectService> databaseDialectServices) {
		this.databaseDialectServices = databaseDialectServices;
	}
    
}
