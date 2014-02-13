package com.yesmynet.query.core.dto;
/**
 * 表示验证参数后的结果
 * @author 刘庆志
 *
 */
public class ValidateResult extends BaseDto
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 验证是否通过
     */
    private boolean passed;
    /**
     * 如果验证未通过时的错误消息
     */
    private String msg;
    public boolean isPassed()
    {
        return passed;
    }
    public void setPassed(boolean passed)
    {
        this.passed = passed;
    }
    public String getMsg()
    {
        return msg;
    }
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    
}
