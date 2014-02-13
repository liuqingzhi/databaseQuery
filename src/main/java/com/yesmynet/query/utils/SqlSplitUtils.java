package com.yesmynet.query.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.query.utils.dto.SignPair;
import com.yesmynet.query.utils.dto.SqlDto;


public class SqlSplitUtils {
	private static Logger logger=LoggerFactory.getLogger(SqlSplitUtils.class);
	/**
     * 分隔sql，并判断sql是不就select语句
     * */
    public static List<SqlDto> splitSql(String sql)
    {
    	List<SqlDto> re=new ArrayList<SqlDto>(); 
		Set<SignPair> pairs = new HashSet<SignPair>();

		pairs.add(new SignPair("'", "'"));
		pairs.add(new SignPair("/\\*", "\\*/"));
		pairs.add(new SignPair("--", "\n"));
		final String splitter=";";
		
		List<String> splitSQL = splitSQL(sql,pairs,splitter);
		logger.debug("分隔后的SQL:{}",splitSQL);
		
		if(!CollectionUtils.isEmpty(splitSQL))
		{
			for(String sqlStr:splitSQL)
			{
				sqlStr=sqlStr.trim();
				if(sqlStr.endsWith(splitter))
				{
					sqlStr=sqlStr.substring(0,sqlStr.length()-1);
				}
				if(StringUtils.hasText(sqlStr))
				{
					boolean select=isSqlSelect(sqlStr);
					
					SqlDto s=new SqlDto();
					s.setSql(sqlStr);
					s.setSelect(select);
					
					re.add(s);
				}
			}
		}
		return re;
    }
	/**
	 * 分隔SQL，使用指定的分隔符分开多条sql
	 * @param sql 多条sql
	 * @param pairs 在sql中有成对出现的标记符，如多行注释，单行注释，两个单引号等，在这些成对出现的标记中
	 * 出现的分隔符不应该分隔SQL
	 * @param splitter 分隔符，如，使用分号分隔sql，则本变量即为";"
	 * @return 分隔后的多条sql
	 */
	private static List<String> splitSQL(final String sql,final Set<SignPair> pairs,final String splitter) {
		List<String> re=new ArrayList<String>();
		String sqlSelf=sql.trim();
		String patternStr=getALLSignPair(pairs,0);
		patternStr+="|"+splitter;//要在sql中查找的符号是所有的标记的开关和分隔符
		Pattern pattern=Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(sqlSelf);
		boolean finished=false;
		boolean findComma=false;
		String currentSplittedSql="";
		int i=0;
		while(!finished)
		{
			if(findComma)
			{
				re.add(currentSplittedSql);
				currentSplittedSql="";
			}
			matcher = pattern.matcher(sqlSelf);
			if(matcher.find())
			{
				String group = matcher.group();
				int end = matcher.end();
				String tmp=sqlSelf.substring(0, end);
				currentSplittedSql+=tmp;
				sqlSelf=sqlSelf.substring(end);
				logger.debug("\t\ti="+i+",group="+group+",end="+end+",currentSplittedSql="+currentSplittedSql+",sqlSefl="+sqlSelf);
				
				if(group.equals(splitter))
				{
					findComma=true;
				}
				else
				{
					findComma=false;
					String endStr =getSignPairEnd(group, pairs);
					
					int indexOf =sqlSelf.length();
					
					Pattern endPattern = Pattern.compile(endStr);
					Matcher matcher2 = endPattern.matcher(sqlSelf);
					if(matcher2.find())
					{
						indexOf=matcher2.end();
					}
					
					currentSplittedSql+=sqlSelf.substring(0,indexOf);
					sqlSelf=sqlSelf.substring(indexOf);
					
					logger.debug("\t\ti="+i+",endStr="+ endStr +",indexOf="+indexOf+",currentSplittedSql="+currentSplittedSql+"sqlSefl="+sqlSelf);
				}
				
			}
			else
			{
				finished=true;
			}
			i++;
		}
		currentSplittedSql+=sqlSelf;
		
		if(currentSplittedSql.length()>0)
			re.add(currentSplittedSql);
		
		
		return re;
	}

	/**
	 * 得到成对出现的标记的匹配情况。
	 * @param pairs 所有的标记
	 * @param sql 源字符串
	 * @param flag 0表示要得到开始标记的匹配情况，1表示结束标记的匹配情况
	 * @return
	 */
	private static String getALLSignPair(Set<SignPair> pairs,int flag)
	{
		String allBeginSign = "";
		
		int i = 0;
		for (SignPair p : pairs) {
			String start ="";
			if(flag==0)
				start=p.getStart();
			else if(flag==1)
				start=p.getEnd();
			else
				throw new RuntimeException("不支持的操作");
			
			allBeginSign += start;
			if (i < pairs.size() - 1)
			{
				allBeginSign += "|";
			}

			i++;
		}
		return allBeginSign;

	}
	/**
	 * 根据运行时得到的开始标记得到对应的结束标记
	 * @param start 在sql中得到的开始标记
	 * @param pairs 所有标记对
	 * @return 该 start对应的结束标记
	 */
	private static String getSignPairEnd(String start,Set<SignPair> pairs)
	{
		String re="";
		for(SignPair p:pairs)
		{
			if(Pattern.matches(p.getStart(), start))
			{
				re=p.getEnd();
				break;
			}
		}
		return re;
	}

    /**
     * 判断sql是不是一个select语句
     * @param sql
     * @return
     */
    private static boolean isSqlSelect(String sql)
    {
    	boolean re=false;
    	
    	Set<SignPair> pairs = new HashSet<SignPair>();
		boolean finished=false;
		String sqlSelf=sql.trim();
		
		pairs.add(new SignPair("/\\*", "\\*/"));
		pairs.add(new SignPair("--", "\n"));
		
		String patternStr=getALLSignPair(pairs,0);
		Pattern pattern=Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(sqlSelf);
		
		while(!finished)
		{
			matcher = pattern.matcher(sqlSelf);
			if(matcher.find())
			{
				String group = matcher.group();
				int start = matcher.start();
				int end = matcher.end();
				String remainderSqlAfterPairStart=sqlSelf.substring(end);
				String endStr =getSignPairEnd(group, pairs);
				int indexOf =remainderSqlAfterPairStart.length();
				
				Pattern endPattern = Pattern.compile(endStr);
				Matcher matcher2 = endPattern.matcher(remainderSqlAfterPairStart);
				if(matcher2.find())
				{
					indexOf=matcher2.end();
				}
				sqlSelf=sqlSelf.substring(0,start)+remainderSqlAfterPairStart.substring(indexOf);
				
				
			}
			else
			{
				finished=true;
			}
				
		}
    	
		sqlSelf=sqlSelf.trim();
		String substring1 ="";
		if(sqlSelf.length()>6)
			substring1=sqlSelf.substring(0,6);
		
		if("select".equalsIgnoreCase(substring1))
			re=true;
		
    	return re;
    }
}
