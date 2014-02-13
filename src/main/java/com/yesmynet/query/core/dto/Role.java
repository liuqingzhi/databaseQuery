package com.yesmynet.query.core.dto;

/**
 * 表示一个角色
 * @author 刘庆志
 *
 */
public class Role extends BaseDto
{
    /**
     * 角色的代码
     */
    private String roleCode;
    /**
     * 角色的显示名称
     */
    private String roleTitle;
    public String getRoleCode()
    {
        return roleCode;
    }
    public void setRoleCode(String roleCode)
    {
        this.roleCode = roleCode;
    }
    public String getRoleTitle()
    {
        return roleTitle;
    }
    public void setRoleTitle(String roleTitle)
    {
        this.roleTitle = roleTitle;
    }
}
