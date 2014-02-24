package com.yesmynet.query.service;

import com.yesmynet.query.core.dto.DatabaseDialect;


/**
 * 处理数据库方言的Service
 * 如对数据库分页查询的sql等
 * @author 刘庆志
 *
 */
public interface DatabseDialectService
{
	/**
	 * oracle的数据库言
	 */
	public static DatabseDialectService OracleDialectService=new DatabseDialectService()
	{
		public String getPagingSql(DatabaseDialect dialect,String sql,Long resultBegin,Long resultEnd)
		{
			String re=" SELECT b.* FROM (\n"+
					"SELECT a.*,ROWNUM num FROM ("
					;
			re+=sql;
			re+=") a where ROWNUM  <=  "+ resultEnd +" ) b WHERE num  >=  "+resultBegin;	
			return re;
		}

		public boolean isSupport(DatabaseDialect dialect)
		{
			boolean re=DatabaseDialect.Oracle.equals(dialect);
			return re;
		}
	};
	/**
	 * derby数据库方言
	 * 这个实现类中没有对sql进行分页
	 */
	public static DatabseDialectService DerbyDialectService=new DatabseDialectService()
	{

		public String getPagingSql(DatabaseDialect dialect, String sql, Long resultBegin, Long resultEnd)
		{
			return sql;
		}

		public boolean isSupport(DatabaseDialect dialect)
		{
			boolean re=DatabaseDialect.Derby.equals(dialect);
			
			return re;
		}
	};
	/**
	 * 对分页进行处理，以得到可以分布的SQL
	 * @param sql 原始的sql
	 * @param resultBegin 到查询结果的开始
	 * @param resultEnd 查询结果的结束
	 * @return
	 */
	public String getPagingSql(DatabaseDialect dialect,String sql,Long resultBegin,Long resultEnd);
	/**
	 * 是否支持给定的数据库言
	 * @param dialect 数据库方言
	 * @return true表示支持，false表示不支持。
	 */
	public boolean isSupport(DatabaseDialect dialect);
	
}
