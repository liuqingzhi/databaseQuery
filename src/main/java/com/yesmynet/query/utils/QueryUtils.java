package com.yesmynet.query.utils;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterInput;

public class QueryUtils {

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
}
