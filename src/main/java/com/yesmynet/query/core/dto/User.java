package com.yesmynet.query.core.dto;

import java.util.Date;
import java.util.List;

/**
 * 表示一个用户
 * @author 刘庆志
 *
 */
public class User extends BaseDto
{
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 昵称
     */
    private String nick;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 该用户具有的所有角色
     */
    private List<Role> roles;
    public String getLoginName()
    {
        return loginName;
    }
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getNick()
    {
        return nick;
    }
    public void setNick(String nick)
    {
        this.nick = nick;
    }
    public Date getCreateDate()
    {
        return createDate;
    }
    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }
	public List<Role> getRoles()
	{
		return roles;
	}
	public void setRoles(List<Role> roles)
	{
		this.roles = roles;
	}
}
