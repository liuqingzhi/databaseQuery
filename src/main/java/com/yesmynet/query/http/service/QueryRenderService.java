package com.yesmynet.query.http.service;

import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.QueryDefinition;

/**
 * 以http方式显示一个查询的service
 * @author liuqingzhi
 *
 */
public class QueryRenderService {
	/**
	 * 显示查询
	 * @param query
	 * @return
	 */
	public String getQueryHtml(QueryDefinition query)
	{
		StringBuilder re=new StringBuilder();
		if(query!=null)
		{
		    List<Parameter> parameters = query.getParameters();
	        if(!CollectionUtils.isEmpty(parameters))
	        {
	            for(Parameter define:parameters)
	            {
	                String oneParameterHtml=getParameterHtml(define);
	                
	                re.append(oneParameterHtml);
	                
	            }
	        }
	        String afterParameterHtml = query.getAfterParameterHtml();
	        if(StringUtils.hasText(afterParameterHtml))
	        	re.append(afterParameterHtml);
	        
	        //if(query.getShowExecuteButton())
	        //    re.append("<input type='submit' value='执行查询' name='executeButton'>");
		}
		
		return re.toString();
	}
	/**
	 * 得到在html中显示一个查询参数的html。
	 * @param parameter 查询参数的定义
	 * @return 用来显示参数的html字符串，如，< input ...>
	 */
	protected String getParameterHtml(Parameter parameter)
	{
		String re="";
		re=parameter.getParameterInput().toHtml();
		return re;
	}
}
