package com.yesmynet.query.core.dto;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


/**
 * 表示一个查询参数。
 * 查询有两个阶段:1、定义阶段，2、运行阶段。当定义查询时，可以定义查询所使用的参数，例如：定义一个查询有
 * 多少个参数，每个参数在展示时使用单行文本框还是多行文本框，是否必填等；在运行阶段，这些参数的值被从界面上收集来
 * 并传给查询使用。
 * @author 刘庆志
 *
 */
public class Parameter extends BaseDto
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 对应的查询
	 */
	private QueryDefinition queryDefinition;
	/**
	 * 输入控件
	 */
	private ParameterInput parameterInput;
	/**
	 * 参数验证器配置
	 */
	private List<ValidatorRule>validatorRules;
	public QueryDefinition getQueryDefinition()
	{
		return queryDefinition;
	}
	public void setQueryDefinition(QueryDefinition queryDefinition)
	{
		this.queryDefinition = queryDefinition;
	}
    public ParameterInput getParameterInput()
    {
        return parameterInput;
    }
    public void setParameterInput(ParameterInput parameterInput)
    {
        this.parameterInput = parameterInput;
    }
	public List<ValidatorRule> getValidatorRules() {
		return validatorRules;
	}
	public void setValidatorRules(List<ValidatorRule> validatorRules) {
		this.validatorRules = validatorRules;
	}
}
