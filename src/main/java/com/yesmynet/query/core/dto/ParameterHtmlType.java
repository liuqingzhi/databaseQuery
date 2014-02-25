package com.yesmynet.query.core.dto;

/**
 * 表示参数的hmtl显示类型，如一个参数可能显示为单行文本框或多行文本框等
 * 
 * @author 刘庆志
 *
 */
//TODO:可以在这里定义参数在界面上显示时的字符串
public  enum ParameterHtmlType
{
	/**
	 * 单行文本框
	 */
	InputText("单行文本",true,"<input type=''text'' name=''{1}'' style=''{2}'' class=''{3}'' value=''{0}'' {4}>"),
	/**
	 * 多行文本框
	 */
	TextArea("多行文本",true,"<textarea name=''{1}'' style=''{2}'' class=''{3}'' {4}>{0}</textarea>"),
	/**
     * html中的hidden输入控件 
     */
    InputHidden("Hidden输入控件",true,"<input type=''hidden'' name=''{1}''  style=''{2}'' class=''{3}'' value=''{0}'' {4}>"),
    /**
     * html中的select输入控件 
     */
    Select("下拉框",false,"<select name=''{1}''  style=''{2}'' class=''{3}'' {4}>{0}</select>"),
    /**
	 * 单选radio
	 */
	Radio("单选",true,"<input type=''radio'' name=''{1}'' value=''{0}'' style=''{2}'' class=''{3}'' {4} {6}>{5}</input>\n"),
	/**
	 * 多选checkbox
	 */
	Checkbox("多选",false,"<input type=''checkbox'' name=''{1}'' value=''{0}'' style=''{2}'' class=''{3}'' {4} {6}>{5}</input>\n"),
	/**
	 * 按钮Button
	 */
	Button("按钮",true,"<input type=''button'' name=''{1}'' value=''{0}'' style=''{2}'' class=''{3}'' {4}/>\n")
    ;
	/**
	 * 显示在界面上的类型名称
	 */
    private String title;
    /**
     * 是否只有一个值。
     * 因为html中有些输入框有些只能输入一个值，有些可以输入多个值；前者如：单行文本框、多行文本框，后者如
     * checkbox框，多选的下拉框。
     */
    private boolean onlyOneValue;
    /**
     * 显示本类型的参数时的html模板
     */
    private String htmlTemplate;
    private ParameterHtmlType(String title,boolean onlyOneValue,String htmlTemplate)
    {
        this.title=title;
        this.htmlTemplate=htmlTemplate;
        this.onlyOneValue=onlyOneValue;
    }
    /**
     * 得到显示的名称
     * @return
     * @author 刘庆志
     */
    public String getTitle()
    {
        return title;
    }
    /**
     * 得到显示本类型的参数时的html模板
     *
     * @return
     */
	public String getHtmlTemplate() {
		return htmlTemplate;
	}
	/**
	 * 是否只有一个值。
	 * 因为html中有些输入框有些只能输入一个值，有些可以输入多个值；前者如：单行文本框、多行文本框，后者如
     * checkbox框，多选的下拉框。
	 * @return true表示只能有一个值，false表示可以有多个值
	 */
	public boolean isOnlyOneValue() {
		return onlyOneValue;
	}
}