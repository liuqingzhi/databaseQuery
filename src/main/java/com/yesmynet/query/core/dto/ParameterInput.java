package com.yesmynet.query.core.dto;

import java.awt.Button;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.query.utils.MessageFormatUtils;


/**
 * 表示参数的输入控件，如：input、select、radio等
 * @author 刘庆志
 *
 */
public class ParameterInput extends BaseDto
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 参数显示时使用的html类型，如：一个单行文本框或一个多行文本框。
     */
    private ParameterHtmlType htmlType;
    /**
     * 参数名称
     * 表示本查询参数在运行时通过HttpRequest收集参数时，在HttpRequest中的参数名。
     */
    private String name;
    /**
     * 在页面上显示参数时的样式，如：可以使用css定义输入框的大小.
     * 本参数只要style="..."，双引号里的内容，不要带上style=""，如下是好的样式：width:150px;font-size:24px;
     */
    private String style;
    /**
     * 在页面上显示参数时的样式的class，
     * 本参数只要class="..."，双引号里的内容，不要带上class=""。
     */
    protected String styleClass;
    /**
     * 参数运行时的值
     */
    private String[] values;
    /**
     * 参数的可选值，对于文本框就是默认值，对于下拉框则可以有多个可选择值
     */
    private List<SelectOption> OptionValues;
    /**
     * 是否擦除提交的值，如果为true 表示擦除，即不回显
     */
    private Boolean eraseValue;
    /**
     * 选项的获取器的Key值，用这个值得到合适的{@link com.yesmynet.query.core.service.ParameterOptionGetter}对象。
     */
    private String optionGetterKey;
    /**
     * 直接在输入框的<input >中输出的内容,最后形成的html如下：<input elementHtml>。
     * 这种方式用来输出一些javascript，如：把本属性设为onlick='alter("aaa")',则最后生成的
     * html如下：<input onlick='alter("aaa")'>。
     */
    private String elementHtml;
    /**
     * 表示不显示,false表示显示，true表示不显示。
     * 有时定义一个参数只是为了得到参数的值却不希望在界面上显示；例如，我定义一个参数，在执行查询后，查询结果中会显示一个输入框，
     * 由用户输入，我不希望在查询的参数列表中显示参数，即使使用hidden也不符合我的意愿，因为使用hidden，如果我再显示一个输入框，
     * 那会导致同名的输入框有两个，提交后，服务端得到两个值。
     */
    private Boolean notShow;
    
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public ParameterHtmlType getHtmlType()
    {
        return htmlType;
    }
    public void setHtmlType(ParameterHtmlType htmlType)
    {
        this.htmlType = htmlType;
    }
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStyle()
    {
        return style;
    }
    public void setStyle(String style)
    {
        this.style = style;
    }
    public String getStyleClass()
    {
        return styleClass;
    }
    public void setStyleClass(String styleClass)
    {
        this.styleClass = styleClass;
    }
    public String[] getValues()
    {
        return values;
    }
    public void setValues(String[] values)
    {
        this.values = values;
    }
    public List<SelectOption> getOptionValues()
    {
        return OptionValues;
    }
    public void setOptionValues(List<SelectOption> optionValues)
    {
        OptionValues = optionValues;
    }
    public Boolean getEraseValue()
    {
        return eraseValue;
    }
    public void setEraseValue(Boolean eraseValue)
    {
        this.eraseValue = eraseValue;
    }
	public String getOptionGetterKey() {
		return optionGetterKey;
	}
	public void setOptionGetterKey(String optionGetterKey) {
		this.optionGetterKey = optionGetterKey;
	}
	public String getElementHtml() {
		return elementHtml;
	}
	public void setElementHtml(String elementHtml) {
		this.elementHtml = elementHtml;
	}
	public Boolean getNotShow() {
		return notShow;
	}
	public void setNotShow(Boolean notShow) {
		this.notShow = notShow;
	}
	/**
     * 转成html代码
     * @return
     */
    public String toHtml()
    {
        String re=null;
        ParameterHtmlType htmlType2 = this.getHtmlType();
        String parameterName = this.getName();
        switch(htmlType2)
        {
        case InputText:
        case TextArea:
        case InputHidden:
        case Button:
        	String inputValue=getValue();
        	if(ParameterHtmlType.Button.equals(htmlType2))
        	{
        		inputValue=getTitle();
        	}
            re=MessageFormatUtils.format(htmlType2.getHtmlTemplate(), inputValue,parameterName,style,styleClass,elementHtml);
            break;
        case Select:
            re=MessageFormatUtils.format(htmlType2.getHtmlTemplate(), getOptions(),parameterName,style,styleClass,elementHtml);
            break;
        case Radio:
        case Checkbox:
            re=toHtmlCheckBoxRadio();
            break;
        }
        
        
        return re;
    }
    private String toHtmlCheckBoxRadio()
    {
        StringBuilder re=new StringBuilder();
        ParameterHtmlType htmlType2 = this.getHtmlType();
        String htmlTemplate = htmlType2.getHtmlTemplate();
        List<SelectOption> value2 = this.getOptionValues();
        String parameterName=this.getName();
        
        if(!CollectionUtils.isEmpty(value2))
        {
            for(SelectOption option:value2)
            {
                String selected=isOptionSelected(option,this.getValues())?"checked":"";
                re.append(MessageFormatUtils.format(htmlTemplate, option.getValue(),parameterName,style,styleClass,elementHtml,option.getText(),selected));
            }
        }
        return re.toString();
    }
    /**
     * 对于只有一个值的输入框（如：单选文本框，多行文本框等），得到输入的值
     * @return
     */
    public String getValue()
    {
        String value2=null;
        if(!ArrayUtils.isEmpty(this.getValues()))
            value2=this.getValues()[0];
        
        if((getEraseValue()!=null && getEraseValue() ))
        {
            value2=null;
        }
        /*
        if(!ParameterHtmlType.TextArea.equals(getHtmlType()) && StringUtils.hasText(value2))
        {
        	value2=value2.replaceAll("\n", "&#10;");//这个会导致一个换行变成二个换行，还不清楚是什么原因
            value2=value2.replaceAll("\"", "&#034;");
            value2=value2.replaceAll("'", "&#039;");
        }
    	*/
        return value2;
    }
    /**
     * 得到下拉框的所有选项的html字符串
     * @return
     */
    private String getOptions()
    {
        StringBuilder re=new StringBuilder();
        List<SelectOption> value2 = this.getOptionValues();
        if(!CollectionUtils.isEmpty(value2))
        {
            String optionTempalte="<option value='%1$s' %2$s>%3$s</option>\n";
            for(SelectOption option:value2)
            {
                String selected=isOptionSelected(option,this.getValues())?"selected":"";
                String format = String.format(optionTempalte, option.getValue(),selected,option.getText());
                re.append(format);
            }
        }
        return re.toString();
    }
    /**
     * 判断选项是否选中
     * @param curOption
     * @param allSelected
     * @return
     */
    private boolean isOptionSelected(SelectOption curOption,String[] allSelected )
    {
        boolean re=false;
        if(!ArrayUtils.isEmpty(allSelected))
        {
            for(String o:allSelected)
            {
                String curValue = curOption.getValue();
                if(StringUtils.hasText(curValue) && StringUtils.hasText(o) && curValue.equals(o))
                    re=true;
            }
        }
        return re;
    }   
}
