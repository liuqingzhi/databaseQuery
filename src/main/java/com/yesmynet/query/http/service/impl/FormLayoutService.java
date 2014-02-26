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
	/**
	 * 表示一个参数在table中的位置
	 * @author liuqingzhi
	 *
	 */
	private class ParameterPosition
	{
		/**
		 * 参数
		 */
		private Parameter parameter;
		/**
		 * 计算后参数所在的行
		 */
		private int row;
		/**
		 * 计算后参数所在的列
		 */
		private int column;
		/**
		 * 计算后的行跨
		 */
		private int rowSpan;
		/**
		 * 计算后的列跨
		 */
		private int columnSpan;
		public Parameter getParameter() {
			return parameter;
		}
		public void setParameter(Parameter parameter) {
			this.parameter = parameter;
		}
		public int getRow() {
			return row;
		}
		public void setRow(int row) {
			this.row = row;
		}
		public int getColumn() {
			return column;
		}
		public void setColumn(int column) {
			this.column = column;
		}
		public int getRowSpan() {
			return rowSpan;
		}
		public void setRowSpan(int rowSpan) {
			this.rowSpan = rowSpan;
		}
		public int getColumnSpan() {
			return columnSpan;
		}
		public void setColumnSpan(int columnSpan) {
			this.columnSpan = columnSpan;
		}
		
	}
	@Override
	public String showParameter(List<Parameter> parameters)
	{
		List<Parameter> sort = sort(parameters);
		List<ParameterPosition> parameterPositions = computePosition(sort);
		String html = getHtml(parameterPositions);
		
		return html;
	}
	/**
	 * 计算出每个参数所在的位置 
	 * @param parameters
	 * @return
	 */
	private List<ParameterPosition> computePosition(List<Parameter> parameters)
	{
		List<ParameterPosition> re=new ArrayList<ParameterPosition>();
		return re;
	}
	/**
	 * 得到显示参数的html
	 * @param parameters
	 * @return
	 */
	private String getHtml(List<ParameterPosition> parameters)
	{
		StringBuilder re=new StringBuilder();
		return re.toString();
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
