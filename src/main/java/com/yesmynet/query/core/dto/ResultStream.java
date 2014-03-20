package com.yesmynet.query.core.dto;

import java.io.OutputStream;

import com.yesmynet.query.core.service.ResourceHolder;

/**
 * 表示要使用流输出的结果
 * @author zhi_liu
 *
 */
public interface ResultStream {
	/**
	 * 得到流的字节数
	 * @return
	 */
	public int getLength();
	/**
	 * 下载的文件的保存名称 
	 * @return
	 */
	public String getFileName();
	/**
	 * 以流的方式输出内容
	 * @param outputStream
	 * @param queryDefinition
	 * @param resourceHolder
	 * @param environment
	 */
	public void write(OutputStream outputStream,QueryDefinition queryDefinition,ResourceHolder resourceHolder,Environment environment);
}
