package com.yesmynet.query.core.service.run.impl;

import groovy.lang.GroovyClassLoader;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.RedisConfig;
import com.yesmynet.query.core.dto.ResultStream;
import com.yesmynet.query.core.dto.Role;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.core.dto.User;
import com.yesmynet.query.core.exception.ServiceException;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.core.service.run.QueryRunService;

public class QureyRunServiceDefaultImpl extends SqlMapClientDaoSupport implements QueryRunService{
	/**
	 * 配置的一些查询的实现
	 */
	Map<String,QueryService> configedQuerys=new HashMap<String,QueryService>();
	/**
	 * 所有数据库资源
	 */
	private List<DataSourceConfig> dataSourceConfigList;
	/**
	 * 所有redis资源
	 */
	private List<RedisConfig> redisConfigList;
	/**
	 * 资源ID和允许访问该ID资源的角色
	 */
	private Map<String,List<Role>> resourceRoles;
	@Override
	public QueryDefinition getQueryDefinition(String queryId) {
		QueryDefinition re = null;
		Environment environment = getEnvironment();
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
			queryService=getQueryInstanceFromGroovyCode(re.getId(),re.getJavaCode());
		}
		if(queryService instanceof QueryDefinitionGetter)
		{
			QueryDefinitionGetter queryDefinitionGetter=(QueryDefinitionGetter)queryService;
			re=queryDefinitionGetter.getQueryDefinition();
			re.setId(queryId);//因为这里得到查询定义后，后面运行时，会根据Id进行处理，所以要把ID设置正确
			
		}
		ResourceHolder resourceHolder = getResourceHolder(environment.getUser());
		//settingParameterOptions(re,resourceHolder,environment);
		return re;
	}
	@Override
	public QueryResult run(QueryDefinition queryDefinition) {
		QueryResult re =new QueryResult();
		
		try {
			Environment environment = getEnvironment();
			QueryService query = configedQuerys.get(queryDefinition.getId());
			if(query==null)
			{
				query=getQueryInstanceFromGroovyCode(queryDefinition.getId(),queryDefinition.getJavaCode());
			}
			if(query!=null)
			{
				re=query.doInQuery(queryDefinition,getResourceHolder(environment.getUser()), environment);
			}
			else
			{
				throw new ServiceException("没有找到指定的查询，id="+queryDefinition.getId());
			}
		} catch (Exception e) {
			re.setException(e);
			logger.debug("运行查询时出现了异常,queryId="+queryDefinition.getId(),e);
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
	 * 从Java代码得到一个QueryService的实例
	 * @param queryId
	 * @param javaCode
	 * @return
	 */
	private QueryService getQueryInstanceFromGroovyCode(String queryId,String javaCode)
	{
		QueryService myObject=null;
        javaCode="package com.yesmynet.database.query.packaged"+ queryId +";"+javaCode;
        
        try
        {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz = gcl.parseClass(javaCode, queryId);
            Object aScript = clazz.newInstance();
            myObject = (QueryService) aScript;
        } catch (Exception e)
        {
            throw new ServiceException("把代码转成java中的"+ QueryService.class.getName() +"对象实例出错了",e);
        } 
		return myObject;
	}
	/**
	 * 得到系统中所有的资源
	 * @return
	 */
	private ResourceHolder getSystemResourceHolder()
	{
		ResourcesHolderImpl re=new ResourcesHolderImpl(dataSourceConfigList,redisConfigList);
		return re;
	}
	/**
	 * 得到当前用户可操作的资源
	 * @return
	 */
	private ResourceHolder getResourceHolder(User user)
	{
		List<DataSourceConfig> dataSources = getDataSources(user);
		List<RedisConfig> redisConfigs = getRedisConfigs(user);
		ResourcesHolderImpl re=new ResourcesHolderImpl(dataSources,redisConfigs);
		return re;
	}
	/**
	 * 得到当前用户有权限使用的所有数据库配置
	 * @param user
	 * @return
	 */
	private List<DataSourceConfig> getDataSources(User user)
	{
		List<DataSourceConfig> re=new ArrayList<DataSourceConfig>();
		if(!CollectionUtils.isEmpty(dataSourceConfigList))
		{
			for(DataSourceConfig d:dataSourceConfigList)
			{
				if(isUserCanUseDatasourceConfig(d.getId(),user))
				{
					re.add(d);
				}
			}
		}
		return re;
	}
	/**
	 * 得到当前用户有权限使用的所有redis配置
	 * @param user
	 * @return
	 */
	private List<RedisConfig> getRedisConfigs(User user)
	{
		List<RedisConfig> re=new ArrayList<RedisConfig>();
		if(!CollectionUtils.isEmpty(redisConfigList))
		{
			for(RedisConfig d:redisConfigList)
			{
				if(isUserCanUseDatasourceConfig(d.getId(),user))
				{
					re.add(d);
				}
			}
		}
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
	private boolean isUserCanUseDatasourceConfig(String resourceId,User user)
	{
		boolean re=false;
		List<Role> permitRoles = resourceRoles.get(resourceId);
		if(CollectionUtils.isEmpty(permitRoles))
		{	
			re=true;
		}
		else
		{
			List<Role> roles = user.getRoles();
			for(Role r:permitRoles)
			{
				for(Role ur:roles)
				{
					if (r.getRoleCode().equals(ur.getRoleCode()))
					{
						re=true;
						break;
					}
				}
				if(re)
					break;
			}
		}
		
		return re;
	}
	public Map<String, QueryService> getConfigedQuerys() {
		return configedQuerys;
	}
	public void setConfigedQuerys(Map<String, QueryService> configedQuerys) {
		this.configedQuerys = configedQuerys;
	}
	public List<DataSourceConfig> getDataSourceConfigList() {
		return dataSourceConfigList;
	}
	public void setDataSourceConfigList(List<DataSourceConfig> dataSourceConfigList) {
		this.dataSourceConfigList = dataSourceConfigList;
	}
	public List<RedisConfig> getRedisConfigList() {
		return redisConfigList;
	}
	public void setRedisConfigList(List<RedisConfig> redisConfigList) {
		this.redisConfigList = redisConfigList;
	}
	public Map<String, List<Role>> getResourceRoles() {
		return resourceRoles;
	}
	public void setResourceRoles(Map<String, List<Role>> resourceRoles) {
		this.resourceRoles = resourceRoles;
	}
	
}
