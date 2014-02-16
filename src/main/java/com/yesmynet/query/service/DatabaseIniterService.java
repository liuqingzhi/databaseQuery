package com.yesmynet.query.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.ibatis.sqlmap.client.SqlMapExecutor;
import com.yesmynet.query.dto.DBUpdateDTO;
import com.yesmynet.query.utils.SqlSplitUtils;
import com.yesmynet.query.utils.dto.SqlDto;
/**
 * 初始始化数据库
 * @author 刘庆志
 *
 */
public class DatabaseIniterService extends SqlMapClientDaoSupport 
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 期望的数据库版本
	 */
	private Integer expectedDatabaseVersion=0;
	/**
	 * 数据库更新文件
	 */
	private Resource sqlFile; 
	/**
	 * 初始化数据库的方法
	 */
	public void init()
	{
		boolean inited=dbinited();
		if(!inited)
		{
			initDB();
		}
	}
	private boolean dbinited()
	{
		boolean re=false;
		
		DBUpdateDTO queryForObject=null;
		try
		{
			queryForObject = (DBUpdateDTO)this.getSqlMapClientTemplate().queryForObject("getDBInitedData");
		}
		catch (Exception e)
		{
			logger.debug("查询出的数据=",e);//JSONObject.fromObject(queryForObject));
			re=false;
		}
		logger.debug("查询出的数据={}",JSONObject.fromObject(queryForObject));
		if(queryForObject!=null)
		{
			Integer curVerion = queryForObject.getCurrentVersion();
			logger.debug("初始化标志={}",curVerion);
			
			if(curVerion<expectedDatabaseVersion)
				re=false;
			else
				re=true;
		}
		
		return re;
		
	}
	private void initDB()
	{
		DataSource dataSource = this.getDataSource();
		JdbcTemplate jdbcTemplat=new JdbcTemplate(dataSource);
		
		String sql=readDBSql();
		if(StringUtils.hasText(sql))
		{
			String[] splitSql = splitSql(sql);
			
			if(!ArrayUtils.isEmpty(splitSql) )
			{
				jdbcTemplat.batchUpdate(splitSql);
				
				try
				{
					Map<String,Object> param=new HashMap<String,Object>();
					param.put("newCurrentVersion", expectedDatabaseVersion);
					int updateRows = this.getSqlMapClientTemplate().update("updateSysInit",param);
					if(updateRows<1)
					{
						this.getSqlMapClientTemplate().insert("insertSysInit", param);
					}
					else if(updateRows>1)
					{
						logger.error("数据库中记录数据库版本的数据有{}条数据，期望只有一条，接下来会只保留版本最高的一条",updateRows);
						int deleteRows = this.getSqlMapClientTemplate().delete("deleteSysInit");
						logger.error("数据库中记录数据库版本的数据有{}条数据，删除了其中的{}条",updateRows,deleteRows);
					}
					
				}
				catch (Exception e)
				{
					logger.error("执行数据库更新的SQL出错了",e);
				}
				finally
				{
					
					
				}
			}
			
			
			
			
		}
		
	}
	private String[] splitSql(String sql)
	{
		String[] re=null;
		List<String> sqls=new ArrayList<String>(); 
		List<SqlDto> splitSql = SqlSplitUtils.splitSql(sql);
		if(!CollectionUtils.isEmpty(splitSql ))
		{
			for(SqlDto sqlDto:splitSql)
			{
				String sql2 = sqlDto.getSql();
				if(StringUtils.hasText(sql2))
					sqls.add(sql2);
			}
		}
		if(!CollectionUtils.isEmpty(sqls ))
		{
			re=sqls.toArray(new String[0]);
		}
		
		return re;
	}
	private String readDBSql()
	{
		String re="";
		try
		{
			InputStream inputStream = sqlFile.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
			re=FileCopyUtils.copyToString(bufferedReader);
		}
		catch (IOException e)
		{
			logger.error("读出数据库初始化文件出错",e);
		}
		return re;
	}
	public Integer getExpectedDatabaseVersion()
	{
		return expectedDatabaseVersion;
	}
	public void setExpectedDatabaseVersion(Integer expectedDatabaseVersion)
	{
		this.expectedDatabaseVersion = expectedDatabaseVersion;
	}
	public Resource getSqlFile()
	{
		return sqlFile;
	}
	public void setSqlFile(Resource sqlFile)
	{
		this.sqlFile = sqlFile;
	}
	
}
