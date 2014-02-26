package com.yesmynet.query.http.service.impl;

import java.util.List;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.http.service.ParameterLayoutService;

/**
 * 用于参数的布局的Service
 * @author zhi_liu
 *
 */
public class FormLayoutService implements ParameterLayoutService {
	/**
	 * 显示参数时分成几列，也就是一行最多显示几个参数
	 */
	private int cloumnNum;
	/**
	 * 是否显示参数的标题
	 */
	private boolean showTitle;
	@Override
	public String showParameter(List<Parameter> parameters)
	{
		return null;
	}
}
