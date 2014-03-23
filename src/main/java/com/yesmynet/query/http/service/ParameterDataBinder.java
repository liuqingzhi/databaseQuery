package com.yesmynet.query.http.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.WebUtils;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.QueryDefinition;

/**
 * 把request提交的数据绑定到查询中的参数上。 本类是参照了 {@link ServletRequestDataBinder}实现的
 * 
 * @author liuqingzhi
 * 
 */
public class ParameterDataBinder {
	/**
	 * 把request中的数据绑定到查询的参数上
	 * 
	 * @param queryDefinition
	 */
	public void bind(QueryDefinition queryDefinition,HttpServletRequest request) 
	{
		if (queryDefinition != null) 
		{
			List<Parameter> parameters = queryDefinition.getParameters();
			if (!CollectionUtils.isEmpty(parameters)) 
			{
				MutablePropertyValues bindManual = bindManual(request);
				
				for (Parameter p : parameters) {
					String parameterName = p.getParameterInput().getName();
					ParameterHtmlType htmlType = p.getParameterInput().getHtmlType();
					//String[] parameterValue = request.getParameterValues(parameterName);
					//p.getParameterInput().setValues(parameterValue);
					if(bindManual.contains(parameterName))
					{
						PropertyValue propertyValue = bindManual.getPropertyValue(parameterName);
						Object value = propertyValue.getValue();
						if(ParameterHtmlType.File.equals(htmlType))
						{
							//是上传文件的参数
							p.getParameterInput().setUploadedFile((MultipartFile)value);
						}
						else
						{
							String[] parameterValues=null; 
							if(value instanceof String)
							{
								parameterValues=new String[]{value.toString()};
							}
							else
							{
								parameterValues=(String[])value;
							}
							p.getParameterInput().setValues(parameterValues);
						}
					}
				}
			}
		}

		
	}

	/**
	 * 把request中的所有参数及值放到一个对象中。
	 * 
	 * @param request
	 * @return
	 */
	private MutablePropertyValues bindManual(HttpServletRequest request) {
		MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(
				request);
		MultipartRequest multipartRequest = WebUtils.getNativeRequest(request,
				MultipartRequest.class);
		if (multipartRequest != null) {
			bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
		}
		return mpvs;
	}

	/**
	 * 绑定multipart form中上传的文件，本方法是复制了spring中的代码：
	 * org.springframework.web.bind.WebDataBinder.bindMultipart(Map<String,
	 * List<MultipartFile>>, MutablePropertyValues)
	 * 
	 * @param multipartFiles
	 * @param mpvs
	 */
	private void bindMultipart(Map<String, List<MultipartFile>> multipartFiles,
			MutablePropertyValues mpvs) {
		for (Map.Entry<String, List<MultipartFile>> entry : multipartFiles
				.entrySet()) {
			String key = entry.getKey();
			List<MultipartFile> values = entry.getValue();
			if (values.size() == 1) {
				MultipartFile value = values.get(0);
				if (!value.isEmpty()) {
					mpvs.add(key, value);
				}
			} else {
				mpvs.add(key, values);
			}
		}
	}
}
