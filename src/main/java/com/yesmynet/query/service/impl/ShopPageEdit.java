package com.yesmynet.query.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.utils.QueryUtils;

public class ShopPageEdit extends AbstractMainQueryService implements QueryDefinitionGetter{
	private final String Parameter_dbId="dbId";
	private final String Parameter_command="command";
	private final String Parameter_pageType="pageType";
	private final String Parameter_module="module";
	private final String Parameter_moduleData="moduleData";
	
	private Map<String,List<ModuleConfig>> positionModules;
	private QueryDefinition queryDefinition;
	@Override
	protected void afterConstructor() {
		super.afterConstructor();
		this.commandQueryMap.put("showPage", new ShowPage());
		this.commandQueryMap.put("getModule", new GetModule());
		this.commandQueryMap.put("showModule", new ShowModule());
		this.commandQueryMap.put("saveModule", new SaveModule());
		
		
		positionModules=new HashMap<String,List<ModuleConfig>>();
		List<ModuleConfig> topList=new ArrayList<ModuleConfig>();
		List<ModuleConfig> leftList=new ArrayList<ModuleConfig>();
		List<ModuleConfig> rightList=new ArrayList<ModuleConfig>();
		positionModules.put("top", topList);
		positionModules.put("left", leftList);
		positionModules.put("right", rightList);
		
		//List<ModuleData> datas
		ModuleConfig userDefine=new ModuleConfig("userdefined","自定义模块",Arrays.asList(new ModuleData("html","自定义的html内容",ComponentDataType.STRING,false)));
		topList.add(userDefine);
		leftList.add(userDefine);
		rightList.add(userDefine);
		
		ModuleConfig shopSign=new ModuleConfig("shopSign","店招模块",Arrays.asList(new ModuleData("image","店招的图片，如果不添加图则使用店铺的信息中配置的图",ComponentDataType.STRING,true)));		
		topList.add(userDefine);
		
		ModuleConfig shopInfo=new ModuleConfig("shopInfo","店铺信息模块",null);		
		leftList.add(shopInfo);
		
		ModuleConfig navigation=new ModuleConfig("navigation","店铺导航条",null);
		topList.add(navigation);
		
		ModuleConfig imageList=new ModuleConfig("imageList","图片",Arrays.asList(
				new ModuleData("height","模块的高度",ComponentDataType.LONG,false),
				new ModuleData("imageList","所有图片，可以多个",ComponentDataType.JSONARRAY,false)
				));
		rightList.add(imageList);
		
		ModuleConfig goodsRecommend=new ModuleConfig("goodsRecommend","推荐商品列表",Arrays.asList(
				new ModuleData("title","模块的标题",ComponentDataType.STRING,false),
				new ModuleData("moreBtn","更多按钮的数据，select表示是否显示，url表示链接,name表示显示文字",ComponentDataType.JSONOBJECT,false),
				new ModuleData("goodsIdList","商品ID，多个商品",ComponentDataType.JSONARRAY,false)
				));
		topList.add(goodsRecommend);
		leftList.add(goodsRecommend);
		rightList.add(goodsRecommend);
		
	}
	@Override
	protected void settingParameterOptions(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		// TODO Auto-generated method stub
		super.settingParameterOptions(queryDefinition, resourceHolder, environment);
	}
	@Override
	protected String getCommand(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public QueryDefinition getQueryDefinition() {
		if(queryDefinition==null)
		{
			queryDefinition=new QueryDefinition();
			List<Parameter> parameters=new ArrayList<Parameter>();
			
			queryDefinition.setParameters(parameters);
			
			parameters.add(QueryUtils.createParameter("选择页面",Parameter_pageType,"",ParameterHtmlType.Select,null,null,null,true,10,1,1));
			//parameters.add(QueryUtils.createParameter("要执行的操作",Parameter_command,"",ParameterHtmlType.Select,null,null,null,true,1000,1,1));
			parameters.add(QueryUtils.createParameter("要操作的数据库",Parameter_dbId,"",ParameterHtmlType.Select,null,null,null,true,20,1,1));
			parameters.add(QueryUtils.createParameter("确定","executeButton","",ParameterHtmlType.Button,"","","onclick='$(\"#queryForm\").submit();'",true,1010,1,1));
			
			parameters.add(QueryUtils.createParameter("模块",Parameter_module,"",ParameterHtmlType.InputHidden,null,null,null,true,1,1,1));
			parameters.add(QueryUtils.createParameter("模块数据",Parameter_moduleData,"",ParameterHtmlType.InputHidden,null,null,null,true,1,1,1));
			
		}

        return queryDefinition;
	}
	/**
	 * 表示模块的配置
	 * @author liuqingzhi
	 *
	 */
	private class ModuleConfig
	{
		/**
		 * 模块的代码
		 */
		private String code;
		/**
		 * 模块的名称
		 */
		private String name;
		/**
		 * 模块的所有数据
		 */
		private List<ModuleData> datas;
		public ModuleConfig(String code, String name, List<ModuleData> datas) {
			super();
			this.code = code;
			this.name = name;
			this.datas = datas;
		}
		
	}
	/**
	 * 模块的一条数据
	 * @author liuqingzhi
	 *
	 */
	private class ModuleData
	{
		/**
		 * 数据的键
		 */
		private String key;
		/**
		 * 对此数据的描述，可以说明此数据在运行时怎么显示
		 */
		private String desc;
		/**
		 * 数据的数据类型
		 */
		private ComponentDataType dataType;
		/**
		 * 该项数据是否可为空
		 */
		private Boolean notNull;
		public ModuleData(String key, String desc,ComponentDataType dataType,
				Boolean notNull) {
			super();
			this.key = key;
			this.desc=desc;
			this.dataType = dataType;
			this.notNull = notNull;
		}
		
	}

/**
 * 表示模块配置的数据的类型
 * 
 * @author zhi_liu
 * 
 */
public enum ComponentDataType {
	STRING {
		@Override
		public Object convertType(String originValue) {
			return originValue;
		}
	},
	LONG {
		@Override
		public Object convertType(String originValue) {
			return Long.parseLong(originValue);
		}
	},
	DATE {
		@Override
		public Object convertType(String originValue) {
			Logger logger=LoggerFactory.getLogger(this.getClass());
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date re=format.parse(originValue);
				return re;
			} catch (ParseException e) {
				logger.error("模块配置的数据转成Date出错了,配置的值="+originValue,e);
			}
			return null;
		}
	},
	JSONOBJECT {
		@Override
		public Object convertType(String originValue) {
			Logger logger=LoggerFactory.getLogger(this.getClass());
			try {
				JSONObject indexjson = JSONObject.fromObject(originValue);
				return indexjson;
			} catch (Exception e) {
				logger.error("模块配置的数据转成JsonObject出错了,配置的值="+originValue,e);
			}
			return null;
		}
	}
	,
	JSONARRAY {
		@Override
		public Object convertType(String originValue) {
			Logger logger=LoggerFactory.getLogger(this.getClass());
			try {
				JSONArray indexjson = JSONArray.fromObject(originValue);
				return indexjson;
			} catch (Exception e) {
				logger.error("模块配置的数据转成JsonArray出错了,配置的值="+originValue,e);
			}
			return null;
		}
	};
	private ComponentDataType() {
		
	}
	public abstract Object convertType(String originValue);
	public static ComponentDataType parseString(String type)
	{
		ComponentDataType re=null;
		for(ComponentDataType comtype:ComponentDataType.values())
		{
			if(comtype.toString().equals(type))
			{
				re=comtype;
				break;
			}
		}
		return re;
	}
}

	/**
	 * 显出页面的配置
	 * @author liuqingzhi
	 *
	 */
	private class ShowPage implements QueryService
	{
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			
			return null;
		}
	}
	/**
	 * 得到一个位置上所有可用的模块
	 * @author liuqingzhi
	 *
	 */
	private class GetModule implements QueryService
	{
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	/**
	 * 显示一个已经添加到页面上的模块，包括模块所包含的数据
	 * @author liuqingzhi
	 *
	 */
	private class ShowModule implements QueryService
	{

		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	/**
	 * 保存模块，包括模块的所有数据
	 * @author liuqingzhi
	 *
	 */
	private class SaveModule implements QueryService
	{
		@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
