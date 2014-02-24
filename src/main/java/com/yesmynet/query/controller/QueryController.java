package com.yesmynet.query.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.service.run.QueryRunService;
import com.yesmynet.query.http.dto.SystemParameterName;
import com.yesmynet.query.http.service.QueryRenderService;


@Controller
public class QueryController {
	Logger logger=LoggerFactory.getLogger(this.getClass());
	/**
	 * 得到查询定义、运行查询的Service
	 */
	@Resource(name = "qureyRunService")
	private QueryRunService queryRunService;
	/**
	 * 显示查询定义的service
	 */
	@Resource(name="queryRenderService")
	private QueryRenderService queryRenderService;
	/**
	 * 显示查询的界面
	 * @param queryId
	 * @param model
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/query.do")/*@RequestMapping(value="/query/{ownerId}/view.do", method=RequestMethod.GET)*/
	public String showQuery(HttpServletRequest request,HttpServletResponse response,Model model) throws IOException
    {
		String viewName="showQuery";
		String queryId=request.getParameter(SystemParameterName.QueryId.getParamerName());//要使用的查询的ID
	    String queryExecute=request.getParameter(SystemParameterName.QueryExecute.getParamerName());//是否要执行查询
	    boolean executeQuery=(queryExecute==null)?false:true;//是否要执行查询
	    
	    QueryDefinition queryDefinition = queryRunService.getQueryDefinition(queryId);
	    setHttpParameterValue(queryDefinition,request);
	    String queryHtml = queryRenderService.getQueryHtml(queryDefinition);
	    QueryResult queryResult =null;
	    String queryExecuteExceptionString="";
	    if(executeQuery)
	    {
	    	queryResult = queryRunService.run(queryDefinition);
	    }
	    if(queryResult!=null )
        {
        	if(queryResult.getContentInputStream()!=null)
            {	
        		FileCopyUtils.copy(queryResult.getContentInputStream(), response.getOutputStream());
        		return null;
            }
        	else if(queryResult.getOnlyShowContent()!=null && queryResult.getOnlyShowContent())
        	{
        		viewName="showQueryOnlyResult";	
        	}
        	
        	if(queryResult.getException()!=null)
        	{
        		queryExecuteExceptionString=printException(queryResult.getException());
        	}
        }
	    
		model.addAttribute("queryHtml", queryHtml);
		model.addAttribute("queryResult", queryResult);
		model.addAttribute("queryExecuteExceptionString", queryExecuteExceptionString);
	    
		return viewName;
    }
	/**
	 * 把httpRequest中请求的参数值设置到查询的参数中
	 * @param queryParameters
	 */
	private void setHttpParameterValue(QueryDefinition queryParameters,HttpServletRequest request)
	{
	    if(queryParameters!=null)
	    {
	        List<Parameter> parameters = queryParameters.getParameters();
	        if(!CollectionUtils.isEmpty(parameters))
	        {
	            for(Parameter p:parameters)
	            {
	                String parameterName = p.getParameterInput().getName();
	                String[] parameterValue = request.getParameterValues(parameterName);
	                p.getParameterInput().setValue(parameterValue);
	            }
	        }    
	    }
		
	}
	/**
	 * 把exception转成string以帮助打印
	 * @param e
	 * @return
	 */
	private String printException(Exception e)
	{
	    String re="";
	    if(e!=null)
	        re=ExceptionUtils.getStackTrace(e);
	    
	    return re;
	}
}
