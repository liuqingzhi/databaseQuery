package com.yesmynet.query.core.service;

import java.util.List;

import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.SelectOption;

/**
 * 获取参数的选项的接口。
 * 查询中可以设置有多个参数，每个参数可以有多个可选值（选项的概念在下拉框中表现的特别充分）,一个参数有
 * 哪些选项我希望通过一个配置就可以实现，但是，得到为了生成这些选项必须使用一个接口才能实现，所以增加本
 * 接口。
 * @author zhi_liu
 *
 */
public interface ParameterOptionGetter {
	/**
	 * 得到参数的所有选项
	 * @param parameterInput 要得到选项的输入控件
	 * systemALLResourceHolder 系统中所有的资源获取器
	 * @param resourceHolder 当前用户可以操作的资源获取器
	 * @param environment 当前的一些环境变量
	 * @return
	 */
	List<SelectOption> getOptions(ParameterInput parameterInput,ResourceHolder systemALLResourceHolder,ResourceHolder resourceHolder,Environment environment);
}
