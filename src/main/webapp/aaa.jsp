<%@ page language="java"%>
<%@ page contentType="text/html; charset=GBK" %>
<jsp:directive.page import="java.util.Date"/>
<%request.setCharacterEncoding("GBK");%>
<%! 
public java.sql.Connection getConnection(String driver,String url,String userName,String password) throws Exception
{
	java.sql.Connection re=null;
	try
	{
		Class.forName(driver);
        re=java.sql.DriverManager.getConnection(url, userName, password);
	}
	catch (Exception e)
	{
		e.printStackTrace();
		throw e;
	}
	return re;
}
public String ShowResultSet(final java.sql.ResultSet rs) throws Exception
{
	StringBuffer sb=new StringBuffer();
	StringBuffer sbReturnValue=new StringBuffer();
	java.sql.ResultSetMetaData rsmd =null; 
	int iColumnCount=0,i=0;
	String strCurrentColumnTypeName="",strCurrentColumnValue="",strColumnLength="";
	int iResultSetCount=0;
	try
	{
		//sb.append("<table width=100% border=1 >\n");
		if (rs!=null)
		{
			rsmd =rs.getMetaData();
			iColumnCount=rsmd.getColumnCount();
			//输出表头
			sb.append("	<tr>\n");
			for (i=1;i<=iColumnCount;i++)
			{
				sb.append("		<th>"+rsmd.getColumnName(i)+"</th>\n");
			}
			sb.append("	</tr>\n");
			sb.append("	<tr>\n");
			strColumnLength="";
			for (i=1;i<=iColumnCount;i++)
			{
				strColumnLength=isNull(rsmd.getScale(i)+"","0");
				strColumnLength=rsmd.getPrecision(i)+""+(strColumnLength.equals("0")? "":","+strColumnLength);
				strColumnLength=isNull(strColumnLength,"0");
				
				strColumnLength=strColumnLength.equalsIgnoreCase("0")?rsmd.getColumnDisplaySize(i)+"":strColumnLength;
				sb.append("		<th>"+rsmd.getColumnTypeName(i)+"("+ strColumnLength +")"+"</th>\n");
								

			}
			sb.append("	</tr>\n");
			
			//输出数据
			iResultSetCount=0;
			while(rs.next())
			{
				sb.append("	<tr>\n");
				for (i=1;i<=iColumnCount;i++)
				{
					strCurrentColumnTypeName=rsmd.getColumnTypeName(i);
					if (strCurrentColumnTypeName.equalsIgnoreCase("blob"))
						strCurrentColumnValue="&lt;Blob&gt;";
					else if (strCurrentColumnTypeName.equalsIgnoreCase("clob"))
						strCurrentColumnValue="&lt;Clob&gt;";
					else if (strCurrentColumnTypeName.equalsIgnoreCase("text"))
						strCurrentColumnValue="&lt;Text&gt;";
					else if (strCurrentColumnTypeName.equalsIgnoreCase("image"))
						strCurrentColumnValue="&lt;Image&gt;";
					else
						strCurrentColumnValue=rs.getString(i);
						
					sb.append("		<td>"+ strCurrentColumnValue +"</td>\n");
				}
				sb.append("	</tr>\n");
				iResultSetCount++;				
			}
			
		}
		
		
		sbReturnValue.append("<table width=100% border=1 >\n");
		sbReturnValue.append("	<tr>\n");
		sbReturnValue.append("		<th align='left' colspan='"+ iColumnCount +"'>共查询到<span style='color:red'>"+ iResultSetCount +"</span>条记录</th>\n");
		sbReturnValue.append("	<tr>\n");
		sbReturnValue.append(sb);
		sbReturnValue.append("</table>\n");
	}
	catch (Exception e)
	{
		e.printStackTrace();
		throw e;
	}
	
	return sbReturnValue.toString();
}

/*字符串处理*/
public static String isNull(String source,String target)
{
	if (source==null)
	{
		source=target;
	}
	else if(source.trim().equals(""))
	{
		source=target;
	}
	return(source);
}
public static String parseSql(String str)
	{
		if(str==null || str.equals(""))
		{
			return "";
		}
		str=str.replaceAll("'","''");
		return str;
	}
%>
<%

String strDBDrive="",strDBurl="",strDBUserName="",strDBPassword="";
java.sql.Connection conn=null;
String strSql="";
java.sql.Statement stmt=null;
java.sql.ResultSet rs=null;
int iUpdateCount=0;
String strDisplayResultSet="",strDisplayUpdateCounts="";
boolean bLoopNext;
		 
String command=isNull(request.getParameter("command"),"");
strDBDrive=isNull(request.getParameter("strDBDrive"),"");
strDBurl=isNull(request.getParameter("strDBurl"),"");
strDBUserName=isNull(request.getParameter("strDBUserName"),"");
strDBPassword=isNull(request.getParameter("strDBPassword"),"");
strSql=isNull(request.getParameter("strSql"),"");

java.util.Date DateConnBegin,DateConnEnd,DateQueryBegin,DateQueryEnd,DatePrintBegin,DatePrintEnd;
StringBuffer sbJiShi=new StringBuffer();

try
{
	if (!command.equals("")) 
	{
		DateConnBegin=new java.util.Date();//连接数据库，开始时间
		conn=getConnection(strDBDrive,strDBurl, strDBUserName, strDBPassword);
		DateConnEnd=new java.util.Date();//连接数据库，结束时间
		
		sbJiShi.append("<table><tr><td>时间消耗（单位：毫秒）：</td><td>");
		sbJiShi.append("连接数据库："+(DateConnEnd.getTime()-DateConnBegin.getTime())+"，");
		
		if (command.equalsIgnoreCase("query"))
		{
			DateQueryBegin=new java.util.Date();//查询，开始时间
			stmt=conn.createStatement();
			stmt.execute(strSql);
			DateQueryEnd=new java.util.Date();//查询，结束时间
			
			sbJiShi.append("查询："+(DateQueryEnd.getTime()-DateQueryBegin.getTime())+"，");
			
			DatePrintBegin=new java.util.Date();//打印结果，开始时间
			rs=stmt.getResultSet();
			if (rs!=null)
			{
				strDisplayResultSet+=ShowResultSet(rs);
			}
			iUpdateCount=stmt.getUpdateCount();
			if (iUpdateCount>=0)
			strDisplayResultSet+="<table><tr><td><span style='color:red'>"+iUpdateCount+"</span>条记录被更新</td></tr></table>";
			bLoopNext=true;
			while(bLoopNext)
			{
				bLoopNext=false;
				if (stmt.getMoreResults())
				{
					rs=stmt.getResultSet();
					bLoopNext=true;
					
					strDisplayResultSet+="<br>";
					if (rs!=null)
					{
						strDisplayResultSet+=ShowResultSet(rs);
					}
				}
				else
				{
					iUpdateCount=stmt.getUpdateCount();
					if (iUpdateCount>=0)  
					{
						bLoopNext=true;
						strDisplayResultSet+="<table><tr><td><span style='color:red'>"+iUpdateCount+"</span>条记录被更新</td></tr></table>";
					}
				}
			}
			DatePrintEnd=new java.util.Date();//打印结果，结束时间
			
			sbJiShi.append("输出结果："+(DatePrintEnd.getTime()-DatePrintBegin.getTime())+"，");			
		}
		if (command.equalsIgnoreCase("query_update"))
		{
			stmt=conn.createStatement();
			stmt.executeUpdate(strSql);
		}
		
		sbJiShi.append("</td></tr></table>\n");
 		strDisplayResultSet=sbJiShi+strDisplayResultSet;
	}
}
catch (Exception e)
{
	conn.rollback();
	out.println("<pre>");
	e.printStackTrace(response.getWriter());
	out.println("</pre>");

}
finally
{
	try
	{
		if (conn!=null) conn.close();
	}
	catch (Exception e)
	{
	}
	
}


%>
<html>
<head>
<META HTTP-EQUIV="expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title></title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>
<style>
	input
	{
		font-size:12px;
		border:1px solid black;
	}
	textarea
	{
		font-size:12px;
		border:1px solid black;
	}
	table
	{
		font-size:12px;
	}
</style>
<body>
<form name=f1 method="post">
	<input type="hidden" name="command" value=""/>
	<table align=left width=100%>
		<tr>
			<td>
					<table align=left  border=1>
						<caption>数据库连接参数</caption>
						<tr>
							<th>驱动程序</th>
							<td colspan=3><input type=text name="strDBDrive" value="<%=strDBDrive%>" size=140></td>
						</tr>
						<tr>
							<th>URL</th>
							<td colspan=3><input type=text name="strDBurl" value="<%=strDBurl%>" size=140></td>
						</tr>
						<tr>
							<th>用户名</th>
							<td><input type=text name="strDBUserName" value="<%=strDBUserName%>" size=20></td>
							<th>密码</th>
							<td><input type=text name="strDBPassword" value="<%=strDBPassword%>" size=20></td>
						</tr>
						<tr>
							<td colspan=4><input type=button value=" 测试连接数据库 " onclick="f1.command.value='conn';f1.submit();"/></td>
						</tr>
					</table>
			</td>
		</tr>
		<tr>
			<td>
				<table align=left >
					<tr>
						<td>
							SQL语句：<br>
							<textarea name="strSql" cols=80 rows=5><%=strSql%></textarea>
						</td>
					</tr>
					<tr>
						<td>
							<input type=button value=" 执行SQL语句 " onclick="f1.command.value='query';f1.submit();">
							<input type=button value=" update执行SQL语句 " onclick="f1.command.value='query_update';f1.submit();">
						</td>
					</tr>
					
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<%=strDisplayResultSet%>
			</td>
		</tr>
	</table>
</form>
</body>

