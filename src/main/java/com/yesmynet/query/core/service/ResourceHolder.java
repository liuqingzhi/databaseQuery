package com.yesmynet.query.core.service;

import java.util.List;

import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.RedisConfig;
/**
 * 资源持有者，通过本接口可以得到要操作的资源。
 * @author liuqingzhi
 *
 */
public interface ResourceHolder {
	/**
	 * 表示资源的类型
	 */
	public enum ResourceType
	{
		Database("数据库"),
		Redis("redis缓存")
		;
		private String name;
		private ResourceType(String name)
		{
			this.name=name;
		}
		public String getName() {
			return name;
		}
		
	}
	/**
	 * 得到所有可用的数据库配置
	 * @return
	 */
	public List<DataSourceConfig> getDataSourceConfigs();
	/**
	 * 得到所有可用的redis配置
	 * @return
	 */
	public List<RedisConfig> getRedisConfigs();
}
