package com.yesmynet.query.core.dto;

import java.io.OutputStream;

import com.yesmynet.query.core.service.ResourceHolder;

/**
 * 表示要使用流输出的结果
 * @author zhi_liu
 *
 */
public interface  ResultStream {
	public void write(OutputStream outputStream);
	public Long getLength();
	public String getFileName();
	public String getContentType();
}
