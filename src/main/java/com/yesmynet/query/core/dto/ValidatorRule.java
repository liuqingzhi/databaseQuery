package com.yesmynet.query.core.dto;

import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.yesmynet.query.core.service.ValidatorMethod;


/**
 * 表示在数据库中一个参数要使用一个验证器的记录。
 * @author liuqingzhi
 *
 */
public class ValidatorRule extends BaseDto{
	/**
	 * 参数ID
	 */
	private Parameter parameter;
	/**
	 * 验证器使用的数据
	 */
	private Map<String,Object> validatorDatas;
	/**
	 * 对应的验证方法
	 */
	private ValidatorMethod validatorMethod; 
	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public Map<String, Object> getValidatorDatas() {
		return validatorDatas;
	}
	public void setValidatorDatas(Map<String, Object> validatorDatas) {
		this.validatorDatas = validatorDatas;
	}
	public ValidatorMethod getValidatorMethod() {
		return validatorMethod;
	}
	public void setValidatorMethod(ValidatorMethod validatorMethod) {
		this.validatorMethod = validatorMethod;
	}
	
}
