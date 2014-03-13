package com.yesmynet.query.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class FreemarkerUtilsTest extends TestCase {

	public void testRenderTemplate() {
		fail("Not yet implemented");
	}

	public void testRenderResourceTemplate() {
		String template="/com/yesmynet/query/utils/test.ftl";
		Map<String,Object> root=new HashMap<String,Object>();
		root.put("curDate", new Date());
		root.put("person", "刘庆志的测试");
		
		String renderTemplateByPath = FreemarkerUtils.renderTemplateInClassPath(template, root);
		System.out.printf("freemarker测试的结果：模板=%s，结果=%s",template,renderTemplateByPath);
		
		
		
	}

}
