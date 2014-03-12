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
			StringWriter out=new StringWriter();
			t.process(datas, out);
			out.flush();
			re=out.toString();
		} catch (Exception e) {
			throw new ServiceException("渲染模板出错了",e);
		}
		return re;
	}
	public static String renderTemplateByPath(String templateLoaderPath,Object datas)
	{
		String re=null;
		try {
			ResourceLoader resourceLoader=new DefaultResourceLoader();
			SpringTemplateLoader springTemplateLoader = new SpringTemplateLoader(resourceLoader, templateLoaderPath);
			Object templateSource = springTemplateLoader.findTemplateSource(templateLoaderPath);
			Reader reader = springTemplateLoader.getReader(templateSource, templateLoaderPath);
			
			
			Template t = new Template(templateLoaderPath, reader,new Configuration());
			StringWriter out=new StringWriter();
			t.process(datas, out);
			out.flush();
			re=out.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}
	
}
