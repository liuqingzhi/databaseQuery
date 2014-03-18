<form id="editParameterForm">
	<input type="hidden" name="command" value="">
	<input type="hidden" name="queryDefinition.id" value="${(parameter.queryDefinition.id)!""}">
	<input type="hidden" name="id" value="${(parameter.id)!""}">
	
	参数名称：<input type="text" name="parameterInput.name" value="${(parameter.parameterInput.name)!""}"><br>
	参数标题：<input type="text" name="parameterInput.title" value="${(parameter.parameterInput.title)!""}"><br>
	参数描述：<input type="text" name="parameterInput.description" value="${(parameter.parameterInput.description)!""}"><br>
	输入框类型：<select name="parameterInput.htmlType">
				<#list allHtmlTypes as htmlType1>
					<option value="${htmlType1}" <#if (parameter.parameterInput.htmlType)?exists && parameter.parameterInput.htmlType==htmlType1>selected</#if>>${htmlType1.title}</option>
				</#list>
			</select><br>
	样式style：<input type="text" name="parameterInput.style" value="${(parameter.parameterInput.style)!""}"><br>
	样式class：<input type="text" name="parameterInput.styleClass" value="${(parameter.parameterInput.styleClass)!""}"><br>
	不回显参数值：<select name="parameterInput.eraseValue">
				<#list yesOrNoOptions as opt>
					<option value="${opt.value}" <#if (parameter.parameterInput.eraseValue)?exists && parameter.parameterInput.eraseValue?string("1","0")==opt.value>selected</#if>>${opt.text}</option>
				</#list>
			</select><br>
	直接写在input元素中的html片断： <input type="text" name="parameterInput.elementHtml" value="${(parameter.parameterInput.elementHtml)!""}"><br>
	不在页面上显参数值：<select name="parameterInput.notShow">
				<#list yesOrNoOptions as opt>
					<option value="${opt.value}" <#if (parameter.parameterInput.notShow)?exists && parameter.parameterInput.notShow?string("1","0")==opt.value>selected</#if>>${opt.text}</option>
				</#list><br>
</form>		