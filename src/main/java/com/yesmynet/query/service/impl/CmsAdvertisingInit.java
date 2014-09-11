package com.yesmynet.query.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.InfoDTO;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.ParameterLayoutDTO;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.utils.QueryUtils;
/**
 * 商城页面用到的广告的初始化
 * @author liuqingzhi
 *
 */
public class CmsAdvertisingInit implements QueryService,QueryDefinitionGetter{
	private final String Parameter_dbId="dbId";
	private final String Parameter_command="command";
	private final String Command_Value_Delete="deleteAds";
	private final String Command_Value_Init="initAds";
	/**
	 * 所有定义的广告
	 */
	private Set<AdCodeAndType> allAds;
	 /**
     * 表示提交的参数与要执行的命令的Map
     */
    private Map<String,QueryService> commandQueryMap;
    
	public CmsAdvertisingInit() {
		super();
		commandQueryMap=new HashMap<String,QueryService>();
		
		commandQueryMap.put("deleteAds", new DeleteAdQueryService());
		commandQueryMap.put("initAds", new InitAdQueryService());
		
		
		allAds=new HashSet<AdCodeAndType>();
		allAds.add(new AdCodeAndType("aa",1,"商城页面顶部条","<div class=\"site-bar\">\n"+
				"  <ul class=\"userinfos\">\n"+
				"    <li class=\"bold\"><a href=\"/portal/toLogin.do\" class=\"txt-login\" rel=\"nofollow\">登录</a></li>\n"+
				"    <li class=\"bldr\"><a href=\"/portal/toRegister.do\" class=\"txt-register\" rel=\"nofollow\">注册</a></li>\n"+
				"  </ul>\n"+
				"  <ul class=\"sitelinks\">\n"+
				"    <li class=\"dropdown myaccount\">\n"+
				"      <a href=\"/personalCenter/showIndex.do?page=1\" class=\"txt-down\" rel=\"nofollow\">我的账户</a>\n"+
				"      <ul>\n"+
				"        <li><a href=\"/personalCenter/showMyOrder.do\" rel=\"nofollow\">我的订单<em></em></a></li>\n"+
				"				<li><a href=\"/personalCenter/showMyCollect.do\" rel=\"nofollow\">我的收藏夹<em></em></a></li>\n"+
				"			</ul>\n"+
				"			<b class=\"arrow-top\"></b>\n"+
				"		</li>\n"+
				"	</ul>\n"+
				"</div>\n"));
		
		
	}

	@Override
	public QueryDefinition getQueryDefinition() {
		QueryDefinition queryDefinition=new QueryDefinition();
        List<Parameter> parameters=new ArrayList<Parameter>();
        
        parameters.add(getParameter("数据库",Parameter_dbId,"",ParameterHtmlType.Select,null,null,null,true,10,1,1));
        parameters.add(getParameter("要执行的操作",Parameter_command,"",ParameterHtmlType.Select,null,null,null,true,10,1,1));
        parameters.add(getParameter("确定","executeButton","",ParameterHtmlType.Button,"","","onclick='$(\"#queryForm\").submit();'",true,30,1,1));
    	
        queryDefinition.setParameters(parameters);

        return queryDefinition;
	}

	@Override
	public QueryResult doInQuery(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		QueryResult re=null;
		
		List<Parameter> parameters = queryDefinition.getParameters();
		String command = QueryUtils.getParameterValue(parameters, Parameter_command);
		
		settingParameterOptions(parameters,resourceHolder);
		QueryService queryService = commandQueryMap.get(command);
		if(queryService!=null)
			re=queryService.doInQuery(queryDefinition, resourceHolder, environment);
        
		return re;
	}
	/**
	 * 设置参数的选项
	 * @param parameters
	 * @param resourceHolder 
	 */
	private void settingParameterOptions(List<Parameter> parameters,ResourceHolder resourceHolder)
	{
		Parameter parameterByName = QueryUtils.getParameterByName(parameters, Parameter_dbId);
		if(parameterByName!=null)
		{
			List<SelectOption> dbOptions = getDBOptions(resourceHolder.getDataSourceConfigs(),true);
			parameterByName.getParameterInput().setOptionValues(dbOptions);
		}
		
		Parameter parameterByName2 = QueryUtils.getParameterByName(parameters, Parameter_command);
		if(parameterByName2!=null)
		{
			List<SelectOption> dbOptions=new ArrayList<SelectOption>(); 
			
			SelectOption delete=new SelectOption();
			delete.setText("删除所有广告");
			delete.setValue(Command_Value_Delete);
			dbOptions.add(delete);
			
			SelectOption init=new SelectOption();
			init.setText("插入所有广告");
			init.setValue(Command_Value_Init);
			dbOptions.add(init);
			
			parameterByName2.getParameterInput().setOptionValues(dbOptions);
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
	private Parameter getParameter(String title,String name,String description,ParameterHtmlType htmlType,String style,String styleClass,String elementHtml,Boolean notShow,Integer sort,Integer rowSpan,Integer columnSpan)
	{
		Parameter parameter=new Parameter();
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
		
		return parameter;
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
     * 表示广告和对应的类型
     * @author liuqingzhi
     *
     */
    private class AdCodeAndType
    {
    	/**
    	 * 广告的代码
    	 */
    	private String adCode;
    	/**
    	 * 广告的类型，因为广告分为两种，一种是html，一种是广告中包含多个数据的
    	 * 1表示html，2表示多个数据的广告
    	 */
    	private int type;
    	/**
    	 * 广告的中文名称
    	 */
    	private String name;
    	/**
    	 * 广告的内容
    	 */
    	private String content;
		
		public AdCodeAndType(String adCode, int type, String name,
				String content) {
			super();
			this.adCode = adCode;
			this.type = type;
			this.name = name;
			this.content = content;
		}
		public String getAdCode() {
			return adCode;
		}
		public void setAdCode(String adCode) {
			this.adCode = adCode;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
    }
    private abstract class AbstractQueryService implements QueryService
    {
    	@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re=new QueryResult();
			
			List<Parameter> parameters = queryDefinition.getParameters();
			String dbId = QueryUtils.getParameterValue(parameters,Parameter_dbId);
	        
			InfoDTO<DataSourceConfig> dataSourceConfigInfoDTO = getDataSourceConfig(dbId,resourceHolder);
	        
	        if(!dataSourceConfigInfoDTO.isSuccess())
	        {
	        	 re.setContent(dataSourceConfigInfoDTO.getMsg());
	             return re;
	        }
	        DataSourceConfig dataSourceConfig = dataSourceConfigInfoDTO.getData();
	        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSourceConfig.getDatasource());
	        re=doInDb(queryDefinition, resourceHolder, environment, jdbcTemplate);
	        
			return re;
		}
    	public abstract QueryResult doInDb(QueryDefinition queryDefinition,ResourceHolder resourceHolder, Environment environment,JdbcTemplate jdbcTemplate);
    	
    }
    private class InitAdQueryService extends AbstractQueryService
    {

		@Override
		public QueryResult doInDb(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment,JdbcTemplate jdbcTemplate) {
			QueryResult re=new QueryResult();
			
			for(AdCodeAndType ad:allAds)
			{
				initHtmlAd(ad.getAdCode(),ad.getName(),ad.getContent(),jdbcTemplate);
			}
			re.setContent("初始化广告内容成功");
			return re;
		}
		
		private void initHtmlAd(String locateCode,String adName,String adContent,JdbcTemplate jdbcTemplate)
		{
			long locationId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_L_ID.Nextval  from dual");
			long adId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_I_ID.Nextval  from dual");
			long adLocMapId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_ILM_ID.Nextval  from dual");
			long publicId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_ILP_ID.Nextval  from dual");
			
			jdbcTemplate.update("insert into M_CMS_AD_LOCATION (ID, L_NAME, L_CODE, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION)\n"+
			"values (?, ?, ?, 0, null, null, sysdate, 1, 0)", locationId,adName,locateCode);
			
			jdbcTemplate.update("insert into M_CMS_AD_INFO (ID, I_NAME, I_BEGIN_DATE, I_END_DATE, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION, I_CONTENT, CHANNEL_NO)\n"+
			"values (?, ?, sysdate-200, sysdate+500, 0, null, null, sysdate, 1, 0, ?, '01')",adId,adName,adContent);
			
			jdbcTemplate.update("insert into M_CMS_AD_IL_MAP (ID, I_ID, L_ID, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION)\n"+
			"values (?, ?, ?, 0, null, null, sysdate, 1, 0)",adLocMapId,adId,locationId);

			jdbcTemplate.update("insert into m_cms_ad_il_publish (ID, M_ID, P_ONLINE_FLAG, P_BEGIN_DATE, P_END_DATE, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION)\n"+
			"values (?, ?, 1, sysdate-1, sysdate+1000, 0, null, null, sysdate, 1, 0)",publicId,adLocMapId);
		}
		
    	
    }
    private class DeleteAdQueryService extends AbstractQueryService
    {

		@Override
		public QueryResult doInDb(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment,
				JdbcTemplate jdbcTemplate) {
			QueryResult re=new QueryResult();
			
			try {
				for(AdCodeAndType ad:allAds)
				{
					deleteHtmlAd(ad.getAdCode(),jdbcTemplate);
				}
			} catch (Exception e) {
				re.setContent(ExceptionUtils.getFullStackTrace(e));
			}
			re.setContent("删除广告成功");
			return re;
		}
		/**
		 * 删除指定
		 * @param locateCode
		 * @param jdbcTemplate
		 * @return
		 */
		private int deleteHtmlAd(String locateCode,JdbcTemplate jdbcTemplate)
		{
			int deleted=0;
			
			jdbcTemplate.update("delete  from  m_cms_ad_il_publish where M_ID in (select id from M_CMS_AD_IL_MAP where L_ID in (select id from M_CMS_AD_LOCATION where L_CODE=?)",locateCode);
			jdbcTemplate.update("delete  from M_CMS_AD_IL_MAP where L_ID in (select id from M_CMS_AD_LOCATION where L_CODE=?)",locateCode);
			jdbcTemplate.update("delete  from from M_CMS_AD_LOCATION where L_CODE=?",locateCode);
			
			return deleted;
		}
		
    	
    }
}
