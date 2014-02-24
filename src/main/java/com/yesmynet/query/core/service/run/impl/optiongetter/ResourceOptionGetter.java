package com.yesmynet.query.core.service.run.impl.optiongetter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.core.service.ParameterOptionGetter;
import com.yesmynet.query.core.service.ResourceHolder;
/**
 * 把当前用户可操作的所有资源生成成一个下拉框的选项，选项值就是资源ID，选项名称就是资源的名称 
 * @author zhi_liu
 *
 */
public class ResourceOptionGetter implements ParameterOptionGetter{
	/**
	 * 表示资源类型
	 * @author zhi_liu
	 *
	 */
	public enum ResourceTypeEnum{
		DB,
		Redis;
	}
	/**
	 * 表示要得到哪种资源类型
	 */
	private ResourceTypeEnum resourceType=ResourceTypeEnum.DB;
	/**
	 * 是否生成一个空的选项
	 */
	private boolean generateAnEmptyOption=false;
	@Override
	public List<SelectOption> getOptions(ParameterInput parameterInput, ResourceHolder systemALLResourceHolder, ResourceHolder resourceHolder,Environment environment) {
		
		List<SelectOption> re=new ArrayList<SelectOption>();
		switch(resourceType)
		{
		case DB:
			re=getDBOptions(resourceHolder.getDataSourceConfigs());
			break;
		case Redis:
			break;
		}
		return re;
	}
	private List<SelectOption> getDBOptions(List<DataSourceConfig> dbs)
	{
		List<SelectOption> re=new ArrayList<SelectOption>();
		if(generateAnEmptyOption)
		{
			SelectOption option=new SelectOption();
			option.setValue("");
			option.setText("");
			re.add(option);
		}
		
		if(!CollectionUtils.isEmpty(dbs))
		{
			for(DataSourceConfig db:dbs)
			{
				SelectOption option=new SelectOption();
				option.setValue(db.getId());
				option.setText(db.getName());
				re.add(option);
			}
		}
		
		return re;
	}
	public ResourceTypeEnum getResourceType() {
		return resourceType;
	}
	public void setResourceType(ResourceTypeEnum resourceType) {
		this.resourceType = resourceType;
	}
	public boolean isGenerateAnEmptyOption() {
		return generateAnEmptyOption;
	}
	public void setGenerateAnEmptyOption(boolean generateAnEmptyOption) {
		this.generateAnEmptyOption = generateAnEmptyOption;
	}

}
