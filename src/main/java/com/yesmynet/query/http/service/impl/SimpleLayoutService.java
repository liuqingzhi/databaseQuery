package com.yesmynet.query.http.service.impl;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.http.service.ParameterLayoutService;
/**
 * 参数显示的简单实现，就是把参数一个个列出来
 * @author zhi_liu
 *
 */
public class SimpleLayoutService implements ParameterLayoutService {

	@Override
	public String showParameter(List<Parameter> parameters) {
		StringBuilder re=new StringBuilder();
		if(!CollectionUtils.isEmpty(parameters))
        {
            for(Parameter define:parameters)
            {
                String oneParameterHtml=getParameterHtml(define);
                
                re.append(oneParameterHtml);
                
            }
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
