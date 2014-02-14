package com.yesmynet.query.core.dto;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 表示查询结果的类。
 * @author 刘庆志
 *
 */
public class QueryReult extends BaseDto
{
	/**
	 * 要使用流输出的查询结果
	 */
	private OutputStream contentStream;
	/**
	 * 要直接显示的查询结果
	 */
	private String content;
	/**
	 * 只输出 {@link #content}的内容，不要增加任何其它内容，设置本参数为true时，系统会把{@link #content}直接通过
	 * response输出。
	 * 这个标志在应对ajax请求时特别有用，因为ajax请求的期望是得到的干干巴巴的结果，如果增加了其它内容会导致ajax处理结果时出错
	 * 
	 */
	private Boolean onlyShowContent;

	public OutputStream getContentStream() {
		return contentStream;
	}
	public void setContentStream(OutputStream contentStream) {
		this.contentStream = contentStream;
	}
	public String getContent()
    {
        return content;
    }
    public void setContent(String content)
    {
        this.content = content;
    }
	public Boolean getOnlyShowContent()
	{
		return onlyShowContent;
	}
	public void setOnlyShowContent(Boolean onlyShowContent)
	{
		this.onlyShowContent = onlyShowContent;
	}
}
