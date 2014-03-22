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
	code VARCHAR(200),/*代码*/
	title VARCHAR(200),/*名称*/
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

/*生成测试查询*/
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

public class Test1  implements  QueryService
{
	public QueryResult doInQuery(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		QueryResult re=new QueryResult();
		
		settingOptions(queryDefinition);
		re.setContent("这是一个数据库中配置的groovy代码的测试，你可以通过如下地址查看本查询的在数据库中的设置<a href=\"query.do?SystemQueryId=queryDefinition&id=1\" target=\"_blank\">编辑本查询</a><br><br>得到的所有参数如下：<br>"+showAllParams(queryDefinition));
		
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
}
');
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (1,'参数1','参数1的描述','InputText','param1','color:red;','class1',0,1,'onclick=''alert("你点击了本输入框");''',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (1,'参数2','参数2的描述','Select','param2','color:green;','class2',0,1,'',CURRENT_TIMESTAMP);
insert into m_sys_query_parameter (query_id,title,description,html_Type,name,style,style_class,erase_value,show,element_html,last_update_time) values (1,'确定','参数3的描述','Button','param3','color:black;','class3',0,1,'onclick=''$("#queryForm").submit();''',CURRENT_TIMESTAMP);
