package com.yesmynet.query.utils;

import java.text.MessageFormat;

public class MessageFormatUtils {
	/**
	 * 调用messageFormat.format格式化字符串。
	 * 使用messageFormat.format格式化字符串，但是如果数据中有null,在生成的字符串中会出现null，我希望
	 * 把null看作""。
	 * @param messageTemplate
	 * @param datas
	 * @return
	 */
	public static String format(String messageTemplate,Object... datas)
	{
		if(datas!=null && datas.length>0)
		{
			for(int i=0;i<datas.length;i++)
			{
				if(datas[i]==null)
					datas[i]="";
			}
		}
		
		return MessageFormat.format(messageTemplate,datas);
	}
}
