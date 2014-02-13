package com.yesmynet.query.core.service;

import java.util.Map;

import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.ValidateResult;

/**
 * 参数的值的验证器
 * @author 刘庆志
 *
 */
public interface ValidatorMethod
{
    /**
     * 验证参数的值
     * @param parameterInput 要验证的输入框
     * @param validateRuleDatas 验证时用到的数据，如：如果是正则表达式验证，则要得到要使用的
     * 正则表达式；如果是数字范围验证，则要得到最小值和最大值，并得到是否包含边界的标志。
     * @return
     * @author 刘庆志
     */
    public ValidateResult valid(ParameterInput parameterInput,Map<String,String> validateRuleDatas); 
}
