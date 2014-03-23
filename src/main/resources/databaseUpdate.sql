CREATE TABLE m_sys_database_init
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_database_init_PK PRIMARY KEY, 
	current_Version INT DEFAULT 0,/*数据库当前版本*/
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;
CREATE TABLE m_sys_query
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_query_PK PRIMARY KEY, 
	name VARCHAR(200),/*名称*/
	description VARCHAR(1000),/*描述*/
	after_Parameter_Html VARCHAR(8000),/*在显示完查询参数后要显示的html*/
	show_Execute_Button SMALLINT,/*是否显示默认的执行查询的按钮*/
	system_init_create SMALLINT,/*是否由系统创建,1是,0不是*/
	system_default SMALLINT,/*是否作为系统默认查询，当用户没有指定要使用哪个查询时，根据本字段得到默认查询,1是,0不是*/
	finished SMALLINT,/*是否已经编辑完成*/
	java_code CLOB,/*java代码*/
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;
CREATE TABLE m_sys_query_parameter
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_query_template_PK PRIMARY KEY,
	query_id int,/*查询的ID，对应了m_sys_query.id*/
	title VARCHAR(200),/*名称*/
	description VARCHAR(1000),/*描述*/
	html_Type VARCHAR(200),/*显示参数的html输入框的方式，如：单选文本框、多行文本框*/
	name  VARCHAR(200),/*自定义的查询参数名称,就是在http请求时的parameter名字*/
	style  VARCHAR(2000),/*css 样式*/
	style_class VARCHAR(2000),/*css class*/
	erase_value SMALLINT DEFAULT 0,/*是否探险参数值，即不回显示值，1表示擦除，0表示不擦除*/
	show SMALLINT DEFAULT 1,/*是否显示,1显示，0不显示*/
	element_html VARCHAR(200),/*直接在元素中的片断*/
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;
CREATE TABLE m_sys_query_template
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_query_parameter_PK PRIMARY KEY,
	query_id int,/*查询的ID，对应了m_sys_query.id*/
	name VARCHAR(200),/*模板名称*/
	title VARCHAR(200),/*模板显示名称*/
	content CLOB,/*模板的内容*/
	last_update_time TIMESTAMP/*上次更新时间*/
	)
	;		
CREATE TABLE m_sys_parameter_validator
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_parameter_validator_PK PRIMARY KEY,
	parameter_id int,/*参数的ID，对应了m_sys_query_parameter.id*/
	validator_type VARCHAR(200)/*验证器的类型，根据本类型可以知道是哪个验证器*/
	)
	;	
CREATE TABLE m_sys_parameter_validator_data
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_parameter_validator_data_PK PRIMARY KEY,
	validator_id int,/*验证器ID，对应了m_sys_parameter_validator.id*/
	data_key VARCHAR(200),/*验证规则的数据的key*/
	data_value VARCHAR(200)/*验证规则的数据的值*/
	)
	;	
CREATE TABLE m_sys_role
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_role_PK PRIMARY KEY,
	role_Code VARCHAR(200),/*角色的代码*/
	role_Title VARCHAR(200)/*角色的显示名称*/
	)
	;
CREATE TABLE m_sys_user
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_user_PK PRIMARY KEY,
	login_Name VARCHAR(200),/*角色的代码*/
	password VARCHAR(200),/*角色的显示名称*/
	nick VARCHAR(200),/*昵称*/
	createDate TIMESTAMP/*创建时间*/
	)
	;
CREATE TABLE m_sys_user_role
	(ID INT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT m_sys_user_role_PK PRIMARY KEY,
	user_id int,/*用户ID*/
	role_id int/*角色Id*/
	)
	;	
insert into  m_sys_user (LOGIN_NAME,PASSWORD,NICK,CREATEDATE) values ('admin','123456','系统管理员',CURRENT_TIMESTAMP);
insert into  m_sys_user (LOGIN_NAME,PASSWORD,NICK,CREATEDATE) values ('user1','123456','普通用户1',CURRENT_TIMESTAMP);
insert into  m_sys_role (ROLE_CODE,ROLE_TITLE) values ('role_admin','系统管理员角色');
insert into  m_sys_role (ROLE_CODE,ROLE_TITLE) values ('role_user','普通用户');
insert into  m_sys_user_role (USER_ID,ROLE_ID) values (1,1);
insert into  m_sys_user_role (USER_ID,ROLE_ID) values (1,2);
insert into  m_sys_user_role (USER_ID,ROLE_ID) values (2,2);

/*生成第1个测试查询*/
insert into m_sys_query (name,description,after_Parameter_Html,java_code) values ('测试查询','初始化生成的测试查询','','
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import java.lang.StringBuilder;
import java.util.List;
import com.yesmynet.query.core.dto.Parameter;
import org.springframework.util.CollectionUtils;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.utils.QueryUtils;
import com.yesmynet.query.core.dto.ResultTemplate;
import java.util.Map;
import java.util.HashMap;
import com.yesmynet.query.utils.FreemarkerUtils;


public class Test1  implements  QueryService
{
	public QueryResult doInQuery(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		QueryResult re=new QueryResult();
		
		settingOptions(queryDefinition);

		String content="这是一个数据库中配置的groovy代码的测试，你可以通过如下地址查看本查询的在数据库中的设置<a href=\"query.do?SystemQueryId=queryDefinition&id=1\" target=\"_blank\">编辑本查询</a><br><br>得到的所有参数如下：<br>"+showAllParams(queryDefinition);
		content+=getResultByFreemarker(queryDefinition);

		re.setContent(content);		

		return re;
	}
	private String showAllParams(QueryDefinition queryDefinition)
	{
		StringBuilder sb=new StringBuilder();
		List<Parameter> parameters = queryDefinition.getParameters();
		if(!CollectionUtils.isEmpty(parameters))
		{
			for(Parameter p:parameters)
			{
				String value = p.getParameterInput().getValue();
				sb.append("参数名称:");
				sb.append(p.getParameterInput().getName());
				sb.append("，参数值：");
				sb.append(p.getParameterInput().getValue());
				sb.append("<br>");
				
			}
		}
		return sb.toString();
	}
	/**
	 * 设置参数的选项，这里可以根据数据库资源来生成所有选项
	 * @param queryDefinition
	 */
	private void settingOptions(QueryDefinition queryDefinition)
	{
		List<Parameter> parameters = queryDefinition.getParameters();
		Parameter parameterByName = QueryUtils.getParameterByName(parameters, "param2");
		if(parameterByName!=null && ParameterHtmlType.Select.equals(parameterByName.getParameterInput().getHtmlType()))
		{
			List<SelectOption> optionValues=new ArrayList<SelectOption>();
			
			SelectOption optEmpty=new SelectOption();
			optEmpty.setText("");
			optEmpty.setValue("");
			optionValues.add(optEmpty);
			
			SelectOption opt1=new SelectOption();
			opt1.setText("选项1");
			opt1.setValue("opt1Value");
			optionValues.add(opt1);
			
			SelectOption opt2=new SelectOption();
			opt2.setText("选项2");
			opt2.setValue("opt2Value");
			optionValues.add(opt2);
			
			parameterByName.getParameterInput().setOptionValues(optionValues);
		}
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
');
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (1,'参数1','参数1的描述','InputText','param1','color:red;','class1',0,1,'onclick=''alert("你点击了本输入框");''',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (1,'参数2','参数2的描述','Select','param2','color:green;','class2',0,1,'',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (1,'确定','参数3的描述','Button','param3','color:black;','class3',0,1,'onclick=''$("#queryForm").submit();''',CURRENT_TIMESTAMP);

insert into m_sys_query_template (query_id,name,title,content,last_update_time) values (1,'template1','模板1','这是使用freemarker模板生成的内容，当前日期和时间是${now?string(''yyyy-MM-dd HH:mm:ss'')}',CURRENT_TIMESTAMP);
insert into m_sys_query_template (query_id,name,title,content,last_update_time) values (1,'template2','模板2','一个查询可以有多个模板，在运行时，你可以得到所有模板，你来决定用哪个或哪几个模板。',CURRENT_TIMESTAMP);



/*生成第2个测试查询*/
insert into m_sys_query (name,description,after_Parameter_Html,java_code) values ('测试文件上传的查询','初始化生成的测试文件上传的查询','','
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.DatabaseDialect;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.core.exception.ServiceException;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.utils.QueryUtils;


public class TestFileUploadQuer implements QueryService
{
	Map<String,QueryService> commandQueryMap=new HashMap<String,QueryService>();
	
	
	public TestFileUploadQuer() {
		super();
		commandQueryMap.put("initDB", new InitDBQuery());
		commandQueryMap.put("uploadFile", new UploadFileQuery());
	}
	@Override
	public QueryResult doInQuery(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		QueryResult re=new QueryResult();
		List<Parameter> parameters = queryDefinition.getParameters();
		String command = QueryUtils.getParameterValue(parameters, "command");
		
		settingParameterOptions(queryDefinition,resourceHolder);
		
		if(StringUtils.hasText(command))
		{
			QueryService queryService = commandQueryMap.get(command);
			if(queryService!=null)
			{
				re=queryService.doInQuery(queryDefinition, resourceHolder, environment);
			}
			else
			{
				re.setContent("不支持的操作，请换个命令");
			}	
		}
		
		return re;
	}
	/**
	 * 设置下拉框的选项
	 * @param queryDefinition
	 */
	private void settingParameterOptions(QueryDefinition queryDefinition,ResourceHolder resourceHolder)
	{
		List<Parameter> parameters = queryDefinition.getParameters();
		Parameter command = QueryUtils.getParameterByName(parameters, "command");
		Parameter db = QueryUtils.getParameterByName(parameters, "saveToWhichDB");
		
		List<SelectOption> options=new ArrayList<SelectOption>();
		command.getParameterInput().setOptionValues(options);
		
		SelectOption opt1=new SelectOption();
		opt1.setValue("initDB");
		opt1.setText("初始化数据库表");
		options.add(opt1);
		
		SelectOption opt2=new SelectOption();
		opt2.setValue("uploadFile");
		opt2.setText("上传文件");
		options.add(opt2);
		
		List<SelectOption> dbOptions = getDBOptions(resourceHolder.getDataSourceConfigs(),true);
		db.getParameterInput().setOptionValues(dbOptions);
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
	 * 得到要操作的数据库的JdbcTemplate
	 * @param queryDefinition
	 * @param resourceHolder
	 * @return
	 */
	private JdbcTemplate getJdbcTemplate(QueryDefinition queryDefinition,ResourceHolder resourceHolder)
	{
		JdbcTemplate re=null;
		String dbId = QueryUtils.getParameterValue(queryDefinition.getParameters(), "saveToWhichDB");
		DataSourceConfig dataSourceConfig = getDatabaseConfig(dbId,resourceHolder);
		if(dataSourceConfig!=null)
		{
			re=new JdbcTemplate(dataSourceConfig.getDatasource());
		}
		else
		{
			throw new ServiceException("没有得到指定的数据库");
		}
		return re;
	}
	/**
	 * 根据数据库ID得到指定的数据库的JdbcTemplate
	 * @param dbId
	 * @param resourceHolder
	 * @return
	 */
	private DataSourceConfig getDatabaseConfig(String dbId,ResourceHolder resourceHolder)
	{
		DataSourceConfig re=null;
		List<DataSourceConfig> dataSourceConfigs = resourceHolder.getDataSourceConfigs();
		if(!CollectionUtils.isEmpty(dataSourceConfigs))
		{
			for(DataSourceConfig db:dataSourceConfigs)
			{
				if(dbId.equals(db.getId()))
				{
					re=db;
					break;
				}
			}
		}
		return re;
	}
	private class InitDBQuery implements QueryService
	{

		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re=new QueryResult();
			
			String dbId = QueryUtils.getParameterValue(queryDefinition.getParameters(), "saveToWhichDB");
			DataSourceConfig dataSourceConfig = getDatabaseConfig(dbId,resourceHolder);
			
			DatabaseDialect databaseDialect = dataSourceConfig.getDatabaseDialect();
			String sql="";
			if(DatabaseDialect.Oracle.equals(databaseDialect))
			{
				sql="create table test_file_upload\n"+
						"(\n"+
						"  file_title  VARCHAR2(100),\n"+
						"  content_data  BLOB, \n"+
						"  update_time   date\n"+
						")";
			}
			else if(DatabaseDialect.Derby.equals(databaseDialect))
			{
				sql="CREATE TABLE test_file_upload (file_title  VARCHAR(100), content_data BLOB)";
			}
			JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSourceConfig.getDatasource());
			jdbcTemplate.execute(sql);
			
			re.setContent("初始化数据库表成功");
			return re;
		}
	}
	/**
	 * 上传文件
	 * @author liuqingzhi
	 *
	 */
	private class UploadFileQuery implements QueryService
	{
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re=new QueryResult();
			try {
				List<Parameter> parameters = queryDefinition.getParameters();
				JdbcTemplate jdbcTemplate = getJdbcTemplate(queryDefinition,resourceHolder);
				
				final String fileTitle = QueryUtils.getParameterValue(parameters, "uploadfileTitle");
				Parameter file = QueryUtils.getParameterByName(parameters, "uploadfile");
				
				if(file.getParameterInput().getUploadedFile()!=null)
				{
					final MultipartFile uploadedFile = file.getParameterInput().getUploadedFile();
					writeUploadedFileToLocalFile(uploadedFile);
					
					String sql="insert into test_file_upload (file_title,content_data) values (?,?)";
					//jdbcTemplate.update(sql, fileTitle,uploadedFile.getInputStream());
					jdbcTemplate.update(sql, new PreparedStatementSetter(){

						@Override
						public void setValues(PreparedStatement ps)
								throws SQLException {
							try {
								ps.setString(1, fileTitle);
								int streamSize = (int)uploadedFile.getSize();
								ps.setBinaryStream(2, uploadedFile.getInputStream(),streamSize);
								//ps.setBlob(2, uploadedFile.getInputStream(),streamSize);
								
							} catch (Throwable e) {
								throw new RuntimeException("把文件写入数据库出错",e);
							}
						}});
					
					re.setContent("文件上传成功");
				}
				else
				{	
					re.setContent("您没有上传文件，请选择您要上传的文件");
				}
			} catch (Exception e) {
				re.setContent("处理上传的文件出错了");
				re.setException(e);
			} 
			return re;
		}
		
	}
	/**
	 * 把上传的文件写到本地的一个文件中
	 * @param uploadedFile
	 */
	private void writeUploadedFileToLocalFile(MultipartFile uploadedFile)
	{
		try {
			FileOutputStream file = new FileOutputStream ("d:\\test\\"+uploadedFile.getOriginalFilename());
			InputStream inputStream = uploadedFile.getInputStream();
			
			IOUtils.copy(inputStream, file);
			
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

');
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (2,'文件标题','表示文件标题','InputText','uploadfileTitle','','',0,1,'',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (2,'要上传的文件','请选择要上传的文件','File','uploadfile','','',0,1,'',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (2,'选择数据库','把上传的文件存储在哪个数据库中','Select','saveToWhichDB','','',0,1,'',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (2,'要执行的操作','选择要执行的操作','Select','command','','',0,1,'',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (2,'确定','确定上传文件','Button','ok','color:black;','class3',0,1,'onclick=''$("#queryForm").submit();''',CURRENT_TIMESTAMP);
