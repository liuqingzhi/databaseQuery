package com.yesmynet.query.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.ResultStream;
import com.yesmynet.query.core.service.run.QueryRunService;
import com.yesmynet.query.http.dto.SystemParameterName;
import com.yesmynet.query.http.service.ParameterDataBinder;
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
	 * 用于绑定参数的service
	 */
	private ParameterDataBinder binder=new ParameterDataBinder();
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
	    
	    if(!StringUtils.hasText(queryId))
	    {
	    	viewName="showQueryError";
	    	model.addAttribute("errorMsg", "无法显示查询，请输入正确的查询ID");
	    	return viewName;
	    }
	    
	    QueryDefinition queryDefinition = queryRunService.getQueryDefinition(queryId);
	    setHttpParameterValue(queryDefinition,request);
	    QueryResult queryResult =null;
	    String queryExecuteExceptionString="";
	    queryResult = queryRunService.run(queryDefinition);
	    if(queryResult!=null )
        {
        	if(queryResult.getResultStream()!=null)
            {	
        		try {
					ResultStream resultStream = queryResult.getResultStream();
					ServletOutputStream outputStream = response.getOutputStream();
					Long length = resultStream.getLength();
					
					response.setHeader("Content-Type","application/force-download");
					if(length!=null)
						response.setHeader("Content-Length", resultStream.getLength()+"");
					response.setHeader("Content-Disposition", "inline; filename=\"" + resultStream.getFileName() + "\"");
					resultStream.write(outputStream);
				} catch (Exception e) {
					logger.error("输出流出错了",e);
					String printException = printException(e);
					
					writeResponseStream("输出流出错了:<pre>"+printException+"</pre>",response);
					
					return null;
				}
        		
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
	    String queryHtml = queryRenderService.getQueryHtml(queryDefinition);
	    
	    model.addAttribute("formEnctype", getFormEnctype(queryDefinition));
		model.addAttribute("queryHtml", queryHtml);
		model.addAttribute("queryResult", queryResult);
		model.addAttribute("queryExecuteExceptionString", queryExecuteExceptionString);
	    
		return viewName;
    }
	private String getFormEnctype(QueryDefinition queryDefinition)
	{
		 String re="";
		 List<Parameter> parameters = queryDefinition.getParameters();
		 boolean hasFileParameter=false;
		 if(!CollectionUtils.isEmpty(parameters))
		 {
			 for(Parameter p:parameters)
			 {
				 if(ParameterHtmlType.File.equals(p.getParameterInput().getHtmlType()))
				 {
					 hasFileParameter=true;
					 break;
				 }
			 }
		 }
		 
		 if(hasFileParameter)
			 re="enctype=\"multipart/form-data\"";
		 return re;
	}
	/**
	 * 向response中写入一些内容，因为response如果调用过response.getOutputStream();方法后
	 * 就不能直接输出内容了，要输出只能使用流的方式写入。
	 * @param content
	 * @param response
	 */
	private void writeResponseStream(String content,HttpServletResponse response)
	{
		try {
			final String encode="UTF-8";
			int length = content.getBytes().length;
			
			response.setHeader("Content-Type","text/html");
			response.setHeader("Content-Length", length+"");
			ServletOutputStream outputStream = response.getOutputStream();
			StringReader stringReader = new StringReader(content);
			
			IOUtils.copy(stringReader, outputStream,encode);
			
		} catch (Exception e) {
			logger.error("在流输出时出错，显示出错信息也出错",e);
		}
	}
	/**
	 * 把httpRequest中请求的参数值设置到查询的参数中
	 * @param queryParameters
	 */
	private void setHttpParameterValue(QueryDefinition queryParameters,HttpServletRequest request)
	{
		//ParameterDataBinder binder=new ParameterDataBinder();
		binder.bind(queryParameters, request);
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
