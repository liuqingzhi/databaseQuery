
				名称：<input type="text" name="name" value="${(queryDefinition.name)!""}"><br>
				描述:<textarea rows="3" cols="100" name="description">${(queryDefinition.description)!""}</textarea><br>
				定义参数：<br>
				<table id="parameterListTable">
					<tr>
						<th>ID</th>
						<th>参数名称</th>
						<th>参数标题</th>
						<th>css style</th>
						<th>css class</th>
						<th>参数描述</th>
						<th>参数html类型</th>
						<th>参数验证器</th>
					</tr>
					<#if (queryDefinition.parameters)?exists>
	    			<#list queryDefinition.parameters as param>
	    			<tr>
	    				<td>
	    					<input type="checkbox" name="parametersToDelete" value="${param.id}">
	    					<input type="hidden" name="parameters[${param_index}].id" value="${param.id}">
	    				</td>
	    				<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.customName" value="${(param.parameterInput.customName)!""}">
	    				</td>
						<td>
							<input type="text" name="parameters[${param_index}].parameterInput.title" value="${(param.parameterInput.title)!""}">
						</td>
						<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.style" value="${(param.parameterInput.style)!""}">
	    				</td>
	    				<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.styleClass" value="${(param.parameterInput.styleClass)!""}">
	    				</td>	    				
	    				<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.description" value="${(param.parameterInput.description)!""}">
	    				</td>
	    				
	    				<td>
	    					<select name="parameters[${param_index}].parameterInput.htmlType">
						        <#list allHtmlTypes as htmlType1>
						        <option value="${htmlType1}" <#if (param.parameterInput.htmlType)?exists && param.parameterInput.htmlType==htmlType1>selected</#if>>${htmlType1.title}</option>
						        </#list>
						    </select>
	    				</td>
	    				<td>
	    					<table>
	    						<tr>
	    							<th>验证器类型</th>
	    							<th>验证器数据</th>
	    						</tr>
	    						<#if (param.parameterValidatorRecordDtos)?exists>
		    					<#list param.parameterValidatorRecordDtos as parameterValidatorRecordDto>
		    						<tr>
		    							<td>
				    						<select name="parameters[${param_index}].parameterValidatorRecordDtos[${parameterValidatorRecordDto_index}].validatorType">
						    					<#list allValidatorDefines as validatorDefine>
										        	<option value="${validatorDefine.validatorType}" <#if (parameterValidatorRecordDto.validatorType)?exists && parameterValidatorRecordDto.validatorType==validatorDefine.validatorType>selected</#if>>${validatorDefine.name}</option>
										        </#list>
									        </select>
							        	</td>
							        	<td>
							        		${parameterValidatorRecordDto.showHtml}
							        	</td>
							        </tr>
						        </#list>
						        </#if>	
	    					</table>
	    					
	    				</td>
	    			</tr>	
	    			</#list>
	        		</#if>
	        		<tr>
	        			<td colspan='5'>
	        				<input type="button" value="增加一行" id="parameterAddButton">
	        				<input type="button" value="删除选中的行" id="parameterDeleteButton">
	        			</td>
	        		</tr>		
				</table>
				
				
				java代码:<br><textarea rows="60" cols="100" name="javaCode">${(queryDefinition.javaCode)!""}</textarea><br>
				
				<input type="submit" value="保存" name="saveQuery">
	
	<script id="parameterInsertTemplate" type="text/x-jquery-tmpl">
				<#noparse>
					<tr>
	    				<td>
							<input type="checkbox" name="parametersToDelete" value="-1">
	    					<input type="hidden" name="parameters[${param_index}].id" value="-1">
	    				</td>
	    				<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.customName" value="">
	    				</td>
						<td>
							<input type="text" name="parameters[${param_index}].parameterInput.title" value="">
						</td>
						<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.style" value="">
	    				</td>
						<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.styleClass" value="">
	    				</td>	    				
	    				<td>
	    					<input type="text" name="parameters[${param_index}].parameterInput.description" value="">
	    				</td>
	    				
	    				<td>
	    					<select name="parameters[${param_index}].parameterInput.htmlType">
					</#noparse>
						        <#list allHtmlTypes as htmlType1>
						        <option value="${htmlType1}" <#if (param.htmlType)?exists && param.htmlType==htmlType1>selected</#if>>${htmlType1.title}</option>
						        </#list>
					<#noparse>
						    </select>
	    				</td>
						<td>
							
						</td>
	    			</tr>
				</#noparse>
	</script>
	<script id="parameterValidatorTemplate" type="text/x-jquery-tmpl">
		<tr>
			<td>
				<#noparse>
				<select name="parameters[${param_index}].parameterValidatorRecordDtos[${validator_index}].validatorType">
				</#noparse>
					<#list allValidatorDefines as validatorDefine>
		    			<option value="${validatorDefine.validatorType}">${validatorDefine.name}</option>
		    		</#list>
				</select>
			</td>
			<td>
				
			</td>
		</tr>
	</script>
	
	<#list allValidatorDefines as validatorDefine>
	<%//这里显示所有验证器的数据输入界面 %>	
    	<script id="parameterValidator_${validatorDefine.validatorType}_template" type="text/x-jquery-tmpl">
			
		</script>
    </#list>
	
	<script type="text/javascript">
		$(document).ready(function() {
			var rowCount = $('#parameterListTable tr').length;
			
			$("#parameterAddButton").click(function()
			{
				var newRowIndex=rowCount-2;
				
				var parameterRow=$( "#parameterInsertTemplate" ).tmpl({param_index:newRowIndex});
				
				$('#parameterListTable tr:last').before(parameterRow);
				rowCount=rowCount+1;
			});
			
			$("#parameterDeleteButton").click(function()
			{
				$('#parameterListTable input[type=checkbox]:checked').parents("tr").remove();
				
			});
			
		});

	</script>

