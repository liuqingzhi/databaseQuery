package com.yesmynet.query.http.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
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
	private int cloumnNum=2;
	/**
	 * 是否显示参数的标题
	 */
	private boolean showTitle=true;
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
		Map<String, List<Parameter>> classifyParameters = classifyParameters(parameters);
		List<Parameter> inTable = classifyParameters.get("inTable");
		List<Parameter> notInTable = classifyParameters.get("notInTable");
		
		List<Parameter> sort = sort(inTable);
		List<ParameterPosition> parameterPositions = computePosition(sort);
		String html = getHtml(parameterPositions,notInTable);
		
		return html;
	}
	/**
	 * 把所有参数分类，一些是显示在表格中的;一些是不显示在表格中的，如隐藏的参数。
	 * @param parameters
	 * @return
	 */
	private Map<String,List<Parameter>> classifyParameters(List<Parameter> parameters)
	{
		Map<String,List<Parameter>> re=new HashMap<String,List<Parameter>>();
		List<Parameter> inTable =new ArrayList<Parameter>();
		List<Parameter> notInTable =new ArrayList<Parameter>();
		
		if(CollectionUtils.isNotEmpty(parameters))
		{
			for(Parameter param:parameters)
			{
				ParameterHtmlType htmlType = param.getParameterInput().getHtmlType();
				if(ParameterHtmlType.InputHidden.equals(htmlType))
					notInTable.add(param);
				else
					inTable.add(param);
			}
		}
		
		re.put("inTable", inTable);
		re.put("notInTable", notInTable);
		
		return re;
		
	}
	/**
	 * 计算出每个参数所在的位置 
	 * @param parameters
	 * @return
	 */
	private List<ParameterPosition> computePosition(List<Parameter> parameters)
	{
		List<ParameterPosition> re=new ArrayList<ParameterPosition>();
		if(CollectionUtils.isNotEmpty(parameters))
		{
			Integer curRow=1;//当前行
			Integer curColumn=1;//当前列
			Integer curRowRemainedColumn=0;//当前行剩余列数
			Map<Integer,Integer> rowRemainedColumnMap=new HashMap<Integer,Integer>();//每行剩余的列数
			for(Parameter param:parameters)
			{
				int rowSpan = getSpanNum(param, 1);
				int columnSpan = getSpanNum(param, 2);
				if(columnSpan>cloumnNum) columnSpan=cloumnNum;
				if(curRowRemainedColumn==0)
				{
					curRowRemainedColumn = rowRemainedColumnMap.get(curRow);
					if(curRowRemainedColumn==null)
						curRowRemainedColumn=cloumnNum;
					curRow++;
				}
								
				if(curRowRemainedColumn>=columnSpan)
				{
					//这个参数的列跨小于本行剩余列数
					ParameterPosition p=new ParameterPosition();
					p.setParameter(param);
					p.setRow(curRow);
					p.setColumn(curColumn);
					p.setRowSpan(rowSpan);
					p.setColumnSpan(columnSpan);
					
					re.add(p);
					
					curRowRemainedColumn=curRowRemainedColumn-columnSpan;
				}
				else
				{
					//本行剩余列数太少，放不下这个参数
					curRowRemainedColumn=0;
				}
				saveRowSpans(curRow,rowSpan,columnSpan,rowRemainedColumnMap);
			}
		}
		return re;
	}
	/**
	 * 保存一个跨行的参数对当前行及后续行的剩余列数的影响
	 * @param paramenterShowedRow 参数显示在哪一行
	 * @param rowSpan 参数的行跨
	 * @param columnSpan 参数的列跨
	 * @param rowRemainedColumnMap 行与剩余列数的映射
	 */
	private void saveRowSpans(Integer paramenterShowedRow,Integer rowSpan,Integer columnSpan,Map<Integer,Integer> rowRemainedColumnMap)
	{
		for(int i=1;i<rowSpan;i++)
		{
			Integer affectedRow=paramenterShowedRow+i;
			Integer remainedColumn = rowRemainedColumnMap.get(affectedRow);
			if(remainedColumn==null)
				remainedColumn=cloumnNum;
			remainedColumn=remainedColumn-columnSpan;
			rowRemainedColumnMap.put(affectedRow, remainedColumn);
		}
	}
	/**
	 * 得到行跨度或列跨度
	 * @param param 参数
	 * @param rowOrcolumn 行还是列，1表示行，2表示列
	 * @return
	 */
	private int getSpanNum(Parameter param,int rowOrcolumn)
	{
		Integer re=1;
		ParameterLayoutDTO parameterLayoutDTO = param.getParameterLayoutDTO();

		if(parameterLayoutDTO!=null)
		{
			switch (rowOrcolumn) 
			{
			case 1:
				re=parameterLayoutDTO.getRowSpan();
				break;
			case 2:
				re=parameterLayoutDTO.getColumnSpan();
				break;
			}
		}
		if(re==null)
			re=1;
		return re ;
	}
	/**
	 * 得到显示参数的html
	 * @param parameters 所有要在表格中显示的参数
	 * @param notInTable 不要在表格中显示的参数
	 * @return
	 */
	private String getHtml(List<ParameterPosition> parameters,List<Parameter> notInTable)
	{
		StringBuilder re=new StringBuilder();
		if(CollectionUtils.isNotEmpty(notInTable))
		{
			for(Parameter param:notInTable)
			{
				re.append(param.getParameterInput().toHtml());
				re.append("\n");
			}
		}
		if(CollectionUtils.isNotEmpty(parameters))
		{
			Integer lastRow=-1;
			boolean firtRow=true;
			Integer lastRowShowedColumn=0;
			re.append("<table border='1'>\n");
			for(ParameterPosition paramPosition:parameters)
			{
				if(!lastRow.equals(paramPosition.getRow()))
				{
					if(!firtRow)
					{
						for(int i=lastRowShowedColumn+1;i<=cloumnNum;i++)
						{
							re.append("<td> </td>");
						}
						re.append("</tr>\n");
					}
					re.append("<tr>\n");
					lastRowShowedColumn=0;
				}
				firtRow=false;
				lastRow=paramPosition.getRow();
				re.append("<td");
				if(paramPosition.getRowSpan()>1)
					re.append(" rowSpan='").append(paramPosition.getRowSpan()).append("'");
				if(paramPosition.getColumnSpan()>1)
					re.append(" columnSpan='").append(paramPosition.getColumnSpan()).append("'");
				re.append(">\n");
				
				if(showTitle)
				{
					re.append(paramPosition.getParameter().getParameterInput().getTitle());
					re.append("：");
				}
				re.append(paramPosition.getParameter().getParameterInput().toHtml());				
				
				re.append("</td>\n");
				lastRowShowedColumn+=paramPosition.getColumnSpan();
			}
			
			for(int i=lastRowShowedColumn+1;i<=cloumnNum;i++)
			{
				re.append("<td> </td>");
			}
			re.append("</tr>\n");
			
			re.append("</table>\n");
		}
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
