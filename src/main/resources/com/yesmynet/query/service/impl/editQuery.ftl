		<#if (!ajaxShow)>	
			<form id="editQueryForm">
			<div id="showQueryEditContainer">
		</#if>
				<input type="hidden" name="command" value="">
				<input type="hidden" name="id" value="${(queryDefinition.id)!""}">
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
				
				
				java代码:<br><textarea rows="30" cols="100" name="javaCode">${(queryDefinition.javaCode)!""}</textarea><br>
				
				<input type="button" value="保存查询定义" id="saveQueryDefinition">
		<#if (!ajaxShow)>	
			</div>
			</form>
			
			<div id="eidtParameterContainer"></div><#-- 显示编辑参数的dialog的容器 -->
		</#if>	
	
	<#if (!ajaxShow)>
		<script type="text/javascript">
			$(document).ready(function() {
			
				//参数的增加、删除的操作
				(function(){
					$(document).delegate("#parameterAddButton", "click", function() {
						//显示参数的dialog
						var tag = $("#eidtParameterContainer");
						var url=$(location).attr('pathname');
						var paramterId='';//得到参数的ID，现在还没有得到，所以每次打开的参数编辑界面都是会新增一个参数
						$.get(url,
							{'SystemQueryId':'queryDefinition','command':'queryParameterGetter','id':paramterId},
							function(json){
								if(json.success && json.success===true)
								{
									tag.html(json.data.html).dialog({modal: true}).dialog({
										minWidth: 300,
										buttons:[
											{
												text:"保存",
												click:function(){
													$("#editParameterForm input[name='command']").val('queryParameterSave');
													$("#editParameterForm input[name='queryDefinition.id']").val($("#editQueryForm input[name='id']").val());//查询定义的ID总是取查询编辑的form中的值
													
													var toSubmitData=$('#editParameterForm').serialize();
													$.post('',
														toSubmitData,
														function(json){
															if(json.success && json.success===true)
															{
																$("#editParameterForm").html(json.data.html);
															}
															else
															{
																$(this).insertBefore(json.msg);
															}	
														},
														'json'
													);
												} 
											},
											{
												text:"取消",
												click:function(){
													$(this).dialog("close"); 
												} 
											}
											
											
											
										]
									});
								}
								else
								{
									alert(json.msg);
								}	
							},
							'json'
						);
					});
					
				})();
				
				//保存查询的按钮
				(function(){
					$(document).delegate("#saveQueryDefinition", "click", function() {
						$("#editQueryForm input[name='command']").val('queryDefinitionSave');
						var toSubmitData=$('#editQueryForm').serialize();
						$.post('',
							toSubmitData,
							function(json){
								if(json.success && json.success===true)
								{
									$("#showQueryEditContainer").html(json.data.html);
								}
								else
								{
									alert(json.msg);
								}	
							},
							'json'
						);
					});
				
				})();
				
			});
	
		</script>
	</#if>