		<#if (!ajaxShow)>	
			<form id="editQueryForm">
			<div id="showQueryEditContainer">
		</#if>
				<input type="hidden" name="SystemQueryId" value="${(systemQueryId)!""}">
				<input type="hidden" name="command" value="">
				<input type="hidden" name="id" value="${(queryDefinition.id)!""}">
				名称：<input type="text" name="name" value="${(queryDefinition.name)!""}"><br>
				描述:<textarea rows="3" cols="100" name="description">${(queryDefinition.description)!""}</textarea><br>
				定义参数：<br>
				<table id="parameterListTable">
					<tr>
						<th>参数名称</th>
						<th>参数标题</th>
						<th>css style</th>
						<th>css class</th>
						<th>参数描述</th>
						<th>参数html类型</th>
						<th>操作</th>
					</tr>
					<#if (queryDefinition.parameters)?exists>
	    			<#list queryDefinition.parameters as param>
	    			<tr data-parameterId="${param.id}">
	    				<td>
	    					${(param.parameterInput.name)!""}
	    				</td>
						<td>
							${(param.parameterInput.title)!""}
						</td>
						<td>
	    					${(param.parameterInput.style)!""}
	    				</td>
	    				<td>
	    					${(param.parameterInput.styleClass)!""}
	    				</td>	    				
	    				<td>
	    					${(param.parameterInput.description)!""}
	    				</td>
	    				<td>
	    					<#if (param.parameterInput.htmlType)?exists>
	    						${param.parameterInput.htmlType.title}
	    					</#if>
	    				</td>
	    				<td>
	    					<input type="button" value="修改" class="parameterModifyButton">
	    					<input type="button" value="删除" class="parameterDeleteButton">
	    				</td>
	    			</tr>	
	    			</#list>
	        		</#if>
	        		<tr>
	        			<td colspan='7'>
	        				<input type="button" value="新增参数" id="parameterAddButton">
	        			</td>
	        		</tr>		
				</table>
				
				
				
				freemarker模板：<br>
				<table>
					<tr>
						<th>模板代码</th>
						<th>模板标题</th>
						<th>操作</th>
					</tr>
					<#if (queryDefinition.templates)?exists>
	    			<#list queryDefinition.templates as resultTemplate>
	    			<tr data-templateId="${resultTemplate.id}">
	    				<td>
	    					${(resultTemplate.code)!""}
	    				</td>
						<td>
							${(resultTemplate.title)!""}
						</td>
	    				<td>
	    					<input type="button" value="修改" class="templateModifyButton">
	    					<input type="button" value="删除" class="templateDeleteButton">
	    				</td>
	    			</tr>	
	    			</#list>
	        		</#if>
	        		<tr>
	        			<td colspan='3'>
	        				<input type="button" value="新增模板" id="templateAddButton">
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
			
				//参数的增加的按钮
				$(document).delegate("#parameterAddButton", "click", function() {
					showParameterEdit();
				});
				//参数的修改按钮
				$(document).delegate(".parameterModifyButton", "click", function() {
					var parameterId=$(this).closest('tr').data("parameterid");//jquery data方法中的参数必须是小写的，因为html element中属性不区分大小写
					showParameterEdit(parameterId);
				});
				//参数的删除按钮
				$(document).delegate(".parameterDeleteButton", "click", function() {
					var parameterId=$(this).closest('tr').data("parameterid");
					var queryId=$("#editQueryForm input[name='id']").val();
					
					var tag = $("#eidtParameterContainer").html("确认要删除这个参数吗？");
					tag.dialog({
					      modal: true, title: '参数删除', zIndex: 10000, autoOpen: true,
					      width: 'auto', resizable: true,
					      buttons: [
					      		{
									text:"确认",
									click:function(){
										deleteParameter(parameterId,queryId);
										$(this).dialog("close"); 
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
					
						
				});
								
				
				//保存查询的按钮
				$(document).delegate("#saveQueryDefinition", "click", function() {
						$("#editQueryForm input[name='command']").val('queryDefinitionSave');
						var toSubmitData=$('#editQueryForm').serialize();
						var url=$(location).attr('pathname');
						$.post(url,
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
				
				//模板的增加的按钮
				$(document).delegate("#templateAddButton", "click", function() {
					showTemplateEdit();
				});
				//模板的修改按钮
				$(document).delegate(".templateModifyButton", "click", function() {
					var templateId=$(this).closest('tr').data("templateid");//jquery data方法中的参数必须是小写的，因为html element中属性不区分大小写
					showTemplateEdit(templateId);
				});
				//模板的删除按钮
				$(document).delegate(".templateDeleteButton", "click", function() {
					var templateId=$(this).closest('tr').data("templateid");
					var queryId=$("#editQueryForm input[name='id']").val();
					
					var tag = $("#eidtParameterContainer").html("确认要删除这个模板吗？");
					tag.dialog({
					      modal: true, title: '参数删除', zIndex: 10000, autoOpen: true,
					      width: 'auto', resizable: true,
					      buttons: [
					      		{
									text:"确认",
									click:function(){
										deleteTemplate(templateId,queryId);
										$(this).dialog("close"); 
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
					
						
				});
				
				
				
				
				
				
				
			});
		
		/**
		显示查询定义的数据
		*/
		function showQueryDefinition(queryId,htmlContainer)
		{
			var url=$(location).attr('pathname');
			var systemQueryId=$("#editQueryForm input[name='SystemQueryId']").val();
			var queryId=$("#editQueryForm input[name='id']").val();
			var toSubmitData={"SystemQueryId":systemQueryId,"command":"queryDefinitionAjaxGetter","id":queryId};
			
			$.get(url,
				toSubmitData,
				function(json){
					if(json.success && json.success===true)
					{
						htmlContainer.html(json.data.html);
					}
					else
					{
						alert(json.msg);
					}	
				},
				'json'
			);
		}
		/**
		删除参数
		*/
		function deleteParameter(parameterId,queryId)
		{
			var url=$(location).attr('pathname');
			var systemQueryId=$("#editQueryForm input[name='SystemQueryId']").val();
			var toSubmitData={"SystemQueryId":systemQueryId,"command":"queryParameterDeleter","toDeleteParameterId":parameterId,"id":queryId};
			
			
			$.post(url,
				toSubmitData,
				function(json){
					if(json.success && json.success===true)
					{
						showQueryDefinition(queryId,$("#showQueryEditContainer"));
					}
					alert(json.msg);
				},
				'json'
			);
		}	
	
		/**
		显示编辑参数的dialog,
		参数的ID
		*/
		function showParameterEdit(parameterId)
		{
			//显示参数的dialog
			var tag = $("#eidtParameterContainer");
			var url=$(location).attr('pathname');
			$.get(url,
				{'SystemQueryId':'queryDefinition','command':'queryParameterGetter','id':parameterId},
				function(json){
					if(json.success && json.success===true)
					{
						tag.html(json.data.html).dialog({modal: true}).dialog({
							minWidth: 300,
							buttons:[
								{
									text:"保存",
									click:function(){
										var queryId=$("#editQueryForm input[name='id']").val();
										$("#editParameterForm input[name='command']").val('queryParameterSave');
										$("#editParameterForm input[name='queryDefinition.id']").val(queryId);//查询定义的ID总是取查询编辑的form中的值
										
										var toSubmitData=$('#editParameterForm').serialize();
										$.post(url,
											toSubmitData,
											function(json){
												if(json.success && json.success===true)
												{
													tag.html(json.data.html);
													showQueryDefinition(queryId,$("#showQueryEditContainer"));
												}
												else
												{
													tag.append(json.msg);
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
		}
		/**
		显示模板的编辑界面
		*/
		function showTemplateEdit(templateId)
		{
			//显示模板的dialog
			var tag = $("#eidtParameterContainer");
			var url=$(location).attr('pathname');
			$.get(url,
				{'SystemQueryId':'queryDefinition','command':'templateGetter','id':templateId},
				function(json){
					if(json.success && json.success===true)
					{
						tag.html(json.data.html).dialog({modal: true}).dialog({
							minWidth: 700,
							buttons:[
								{
									text:"保存",
									click:function(){
										var queryId=$("#editQueryForm input[name='id']").val();
										$("#editTemplateForm input[name='command']").val('templateSave');
										$("#editTemplateForm input[name='queryDefinition.id']").val(queryId);//查询定义的ID总是取查询编辑的form中的值
										
										var toSubmitData=$('#editTemplateForm').serialize();
										$.post(url,
											toSubmitData,
											function(json){
												if(json.success && json.success===true)
												{
													tag.html(json.data.html);
													showQueryDefinition(queryId,$("#showQueryEditContainer"));
												}
												else
												{
													tag.append(json.msg);
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
		}
		/**
		删除模板
		*/
		function deleteTemplate(templateId,queryId)
		{
			var url=$(location).attr('pathname');
			var systemQueryId=$("#editQueryForm input[name='SystemQueryId']").val();
			var toSubmitData={"SystemQueryId":systemQueryId,"command":"templateDeleter","ToDeleteTemplateId":templateId,"id":queryId};
			
			
			$.post(url,
				toSubmitData,
				function(json){
					if(json.success && json.success===true)
					{
						showQueryDefinition(queryId,$("#showQueryEditContainer"));
					}
					alert(json.msg);
				},
				'json'
			);
		}
		</script>
	</#if>