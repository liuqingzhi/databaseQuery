package com.yesmynet.query.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.yesmynet.query.core.dto.BaseDto;
import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.InfoDTO;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.ParameterLayoutDTO;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.RedisConfig;
import com.yesmynet.query.core.dto.ResultTemplate;
import com.yesmynet.query.core.service.ResourceHolder;

public class QueryUtils {
	private static Gson gson = new GsonBuilder().serializeNulls().create();
	/**
     * 根据参数名称得到参数的值 
     * @param parameterMap 查询定义的所有参数
     * @param parameterName 要得到值的参数名称
     * @return 参数的值
     */
    public static String getParameterValue(List<Parameter> parameters,String parameterName)
    {
    	String re=null;
    	Parameter parameter = getParameterByName(parameters,parameterName);
    	if(parameter!=null)
    	{
    		ParameterInput parameterInput = parameter.getParameterInput();
    		if(parameterInput!=null)
    		{
    			String[] value = parameterInput.getValues();
    			if(value!=null && value.length>0)
    			{
    				re=value[0];			
    			}
    		}
    	}
    	return re;
    }
    /**
     * 根据参数名称得到查询定义的参数
     * @param parameters
     * @param parameterName
     * @return
     */
    public static Parameter getParameterByName(List<Parameter> parameters,String parameterName)
    {
    	Parameter re=null;
    	if(StringUtils.hasText(parameterName) && CollectionUtils.isNotEmpty(parameters))
    	{
    		for(Parameter p :parameters)
    		{
    			String name = p.getParameterInput().getName();
    			if(parameterName.equals(name)) 
    			{
    				re=p;
    				break;
    			}
    		}
    	}
    	return re;
    }
    /**
     * 根据名称得到用于显示结果的模板
     * @param templates
     * @param templateName
     * @return
     */
    public static ResultTemplate getTemplateByName(List<ResultTemplate> templates,String templateName)
    {
    	ResultTemplate re=null;
    	if(StringUtils.hasText(templateName) && CollectionUtils.isNotEmpty(templates))
    	{
    		for(ResultTemplate p :templates)
    		{
    			String name = p.getName();
    			if(templateName.equals(name)) 
    			{
    				re=p;
    				break;
    			}
    		}
    	}
    	return re;
    }
    /**
     * 根据用户选择的数据库ID得到数据库
     * @param dbId
     * @param resourceHolder
     * @return
     */
    public static InfoDTO<DataSourceConfig> getDataSourceConfig(String dbId,ResourceHolder resourceHolder)
    {
    	InfoDTO<DataSourceConfig> re=new InfoDTO<DataSourceConfig>();
    	getResourceConfig(dbId, resourceHolder, 1,re);
    	return re;
    }
    /**
     * 根据用户选择的数据库ID得到数据库
     * @param dbId
     * @param resourceHolder
     * @return
     */
    public static InfoDTO<RedisConfig> getRedisConfig(String dbId,ResourceHolder resourceHolder)
    {
    	InfoDTO<RedisConfig> re=new InfoDTO<RedisConfig>();
    	getResourceConfig(dbId, resourceHolder, 2,re);
    	return re;
    }
    /**
	 * 生成一个查询定义的参数的方法
	 * @param title 参数标题
	 * @param description 参数描述
	 * @param htmlType 参数html控件类型
	 * @param name 参数名称
	 * @param style 样式
	 * @param styleCss 样式的class
	 * @param elementHtml 直接在html中输出的内容
	 */
	public static Parameter createParameter(String title,String name,String description,ParameterHtmlType htmlType,String style,String styleClass,String elementHtml,Boolean notShow,Integer sort,Integer rowSpan,Integer columnSpan)
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
     * 根据ID得到资源 
     * @param dbId 资源的Id
     * @param resourceHolder
     * @param resourceType 类型，1表示要得到数据库资源，2表示要得到redis资源
     * @return
     */
    private static void getResourceConfig(String dbId,ResourceHolder resourceHolder,int resourceType,InfoDTO re)
    {
    	String resourceTypeName="";
    	List<? extends BaseDto> dataSourceConfigs =null;
    	if(resourceType==1)
    	{
    		dataSourceConfigs =resourceHolder.getDataSourceConfigs();
    		resourceTypeName="数据库";
    	}
    	else if(resourceType==2)
    	{
    		dataSourceConfigs =resourceHolder.getRedisConfigs();
    		resourceTypeName="redis";
    	}
    	
    	if(!StringUtils.hasText(dbId))
    	{
    		re.setSuccess(false);
    		re.setMsg("您没有选择要操作的"+ resourceTypeName +"，请选择");
    		return ;
    	}
    	
    	if(CollectionUtils.isEmpty(dataSourceConfigs))
    	{
    		re.setSuccess(false);
    		re.setMsg("您没有权限操作"+resourceTypeName);
    		return ;
    	}	
    	BaseDto foundDB=null;
    	for(BaseDto db:dataSourceConfigs)
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
    		re.setMsg("您没有权限操作目标"+resourceTypeName);
    		return ;
    	}
    	re.setData(foundDB);
    }
}
