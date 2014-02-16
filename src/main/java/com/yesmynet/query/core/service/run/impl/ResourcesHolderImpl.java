package com.yesmynet.query.core.service.run.impl;

import java.util.List;

import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.RedisConfig;
import com.yesmynet.query.core.service.ResourceHolder;
/**
 * 资源持有者的默认实现
 * @author liuqingzhi
 *
 */
public class ResourcesHolderImpl implements ResourceHolder {
	private List<DataSourceConfig> dataSources;
	private List<RedisConfig> rediss;
	public ResourcesHolderImpl(List<DataSourceConfig> dataSources,List<RedisConfig> rediss) {
		super();
		this.dataSources = dataSources;
		this.rediss = rediss;
	}
	@Override
	public List<DataSourceConfig> getDataSourceConfigs() {
		return dataSources;
	}
	@Override
	public List<RedisConfig> getRedisConfigs() {
		return rediss;
	}
	
}
