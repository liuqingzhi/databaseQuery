package com.yesmynet.query.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.freemarker.SpringTemplateLoader;

import com.yesmynet.query.core.exception.ServiceException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUtils {

	/**
	 * 根据模板和数据合成最后要显示的结果
	 * @param templateContent 以字符串表示的模板
	 * @param datas 模板要使用的数据
	 * @return
	 * @throws IOException 
	 * @throws TemplateException 
	 */
	public static String renderTemplateByContent(String templateContent,Object datas)
	{
		String re=null;
		
		try {
			Template t = new Template("name", new StringReader(templateContent),new Configuration());
			re=renderTemplate(t,datas);
		} catch (Exception e) {
			throw new ServiceException("渲染模板出错了",e);
		}
		return re;
	}
	/**
	 * 渲染模板，模板是放在classpath中的。
	 * @param templateClassPath 模板在classpath中的路径，如：/com/yesmynet/query/utils/test.ftl、/test.ftl等。
	 * @param datas 模板要显示的数据
	 * @return
	 */
	public static String renderTemplateInClassPath(String templateClassPath,Object datas)
	{
		String re=null;
		try {
			Configuration freemarkerConfiguration = new Configuration();
			freemarkerConfiguration.setClassForTemplateLoading(FreemarkerUtils.class, "/");
			Template template = freemarkerConfiguration.getTemplate(templateClassPath);
			
			re=renderTemplate(template,datas);
		} catch (Exception e) {
			throw new ServiceException("渲染模板出错了，模板路径="+templateClassPath,e);
		}
		return re;
	}
	/**
	 * 渲染模板，并得到最后生成的字符串
	 * @param template
	 * @param datas
	 * @return
	 * @throws TemplateException
	 * @throws IOException
	 */
	private static String renderTemplate(Template template,Object datas) throws TemplateException, IOException
	{
		String re=null;
		
		StringWriter out=new StringWriter();
		template.process(datas, out);
		out.flush();
		re=out.toString();
		
		return re;
	}
}
