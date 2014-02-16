package com.yesmynet.query.core.service.run.impl;

import groovy.lang.GroovyClassLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.Role;
import com.yesmynet.query.core.dto.User;
import com.yesmynet.query.core.exception.ServiceException;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.QueryShowListner;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.core.service.run.QueryRunService;

public class QureyRunServiceDefaultImpl extends SqlMapClientDaoSupport implements QueryRunService{
	Map<String,QueryService> configedQuerys;
	@Override
	public QueryDefinition show(String queryId) {
		QueryDefinition re = null;
		QueryService queryService = configedQuerys.get(queryId);
		if(queryService!=null)
		{
			if(!(queryService instanceof QueryDefinitionGetter))
			{
				throw new ServiceException("使用java代码实现的QueryService接口必须同时实现QueryDefinitionGetter接口");
			}
		}
		else
		{
			re=getQueryDefinitionFromDB(queryId);
			queryService=getQueryInstanceFromDB(queryId);
		}
		if(queryService instanceof QueryDefinitionGetter)
		{
			QueryDefinitionGetter queryDefinitionGetter=(QueryDefinitionGetter)queryService;
			re=queryDefinitionGetter.getQueryDefinition();
		}
		if(queryService instanceof QueryShowListner)
		{
			QueryShowListner listner=(QueryShowListner)queryService;
			listner.beforeShow(getResourceHolder(), getEnvironment());
		}
		return re;
	}
	@Override
	public QueryResult run(String queryId) {
		QueryResult re =null;
		QueryService query = configedQuerys.get(queryId);
		if(query==null)
		{
			query=getQueryInstanceFromDB(queryId);
		}
		if(query!=null)
		{
			re=query.doInQuery(getResourceHolder(), getEnvironment());
		}
		else
		{
			throw new ServiceException("没有找到指定的查询，id="+queryId);
		}
		return re;
	}
	/**
	 * 从数据库得到查询的配置
	 * @param queryId
	 * @return
	 */
	private QueryDefinition getQueryDefinitionFromDB(String queryId)
	{
		return (QueryDefinition)this.getSqlMapClientTemplate().queryForObject("getQueryDefinitionById", queryId);
	}
	/**
	 * 从数据库得到一个查询实例
	 * @param id
	 * @return
	 */
	private QueryService getQueryInstanceFromDB(String id) 
	{
		QueryService myObject=null;
	    if(StringUtils.hasText(id))
	    {
	        QueryDefinition queryDefinition=getQueryDefinitionFromDB(id);
	        
	        String javaCode = queryDefinition.getJavaCode();
	        javaCode="package com.yesmynet.database.query."+ id +";"+javaCode;
	        
	        try
	        {
	            GroovyClassLoader gcl = new GroovyClassLoader();
	            Class clazz = gcl.parseClass(javaCode, id);
	            Object aScript = clazz.newInstance();
	            myObject = (QueryService) aScript;
	        } catch (Exception e)
	        {
	            throw new RuntimeException(e);
	        } 
	    }
	    return myObject;
	}
	/**
	 * 得到资源持有者
	 * @return
	 */
	private ResourceHolder getResourceHolder()
	{
		ResourcesHolderImpl re=new ResourcesHolderImpl(null,null);
		return re;
	}
	/**
	 * 得到环境变量
	 * @return
	 */
	private Environment getEnvironment()
	{
		Environment re = new Environment();
		re.setUser(getCurrentUser());
		return re;
	}
	/**
	 * 得到当前用户
	 * @return
	 */
	private User getCurrentUser()
	{
		User re=new User();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		String userName=((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername();
		Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
		
		
		
		List<Role> roles=new ArrayList<Role>();
		if(!CollectionUtils.isEmpty(authorities))
		{
			for(GrantedAuthority g:authorities)
			{
				String authority = g.getAuthority();
				Role role=new Role();
				role.setRoleCode(authority);
				
				roles.add(role);
			}
			
		}
		re.setLoginName(userName);
		re.setRoles(roles);
		
		
		return re;
	}
	/**
	 * 判断用户是否有权操作指定的数据源配置
	 */
	private boolean isUserCanUseDatasourceConfig(DataSourceConfig datasource,User user)
	{
		boolean re=false;
		List<Role> permitRoles = null;
		
		
		if(CollectionUtils.isEmpty(permitRoles))
		{	
			re=true;
		}
		else
		{
			List<Role> roles = user.getRoles();
			for(Role r:permitRoles)
			{
				if(!CollectionUtils.isEmpty(roles))
				{
					for(Role ur:roles)
					{
						if (r.getRoleCode().equals(ur.getRoleCode()))
						{
							re=true;
							break;
						}
					}
				}
				if(re)
					break;
			}
		}
		
		
		return re;
	}
}
