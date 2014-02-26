package com.yesmynet.query.http.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterLayoutDTO;
import com.yesmynet.query.http.service.ParameterLayoutService;

/**
 * 用于参数的布局的Service
 * @author zhi_liu
 *
 */
public class FormLayoutService implements ParameterLayoutService {
	/**
	 * 显示参数时分成几列，也就是一行最多显示几个参数
	 */
	private int cloumnNum;
	/**
	 * 是否显示参数的标题
	 */
	private boolean showTitle;
	@Override
	public String showParameter(List<Parameter> parameters)
	{
		List<Parameter> sort = sort(parameters);
		
		return null;
	}
	/**
	 * 把参数按显示顺序排序
	 * @param parameters
	 * @return 返回一个新的list以避免改变了原来的List
	 */
	private List<Parameter> sort(List<Parameter> parameters)
	{
		List<Parameter> re =new ArrayList<Parameter>(parameters);
		Collections.sort(re, new Comparator<Parameter>(){
			@Override
			public int compare(Parameter o1, Parameter o2) {
				int sort1 = getLayoutSort(o1);
				int sort2 = getLayoutSort(o2);
				
				return sort1-sort2;
			}});
		
		return re;
	}
	/**
	 * 得到参数的显示顺序
	 * @param parameter
	 * @return
	 */
	private int getLayoutSort(Parameter parameter)
	{
		int re=0;
		ParameterLayoutDTO layout1 = parameter.getParameterLayoutDTO();
		if(layout1!=null)
		{
			Integer sort = layout1.getSort();
			re=sort==null?0:sort;
		}
		return re;
	}
}
