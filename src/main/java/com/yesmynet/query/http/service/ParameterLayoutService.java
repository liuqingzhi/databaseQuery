package com.yesmynet.query.http.service;

import java.util.List;

import com.yesmynet.query.core.dto.Parameter;

public interface ParameterLayoutService {

	/**
	 * 显示查询中的参数
	 * @param parameters
	 * @return
	 */
	public abstract String showParameter(List<Parameter> parameters);

}