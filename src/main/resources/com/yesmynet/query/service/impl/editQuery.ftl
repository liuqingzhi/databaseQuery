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
					
					var tag = $("#eidtParameterContainer");
					tag.dialog({
					      modal: true, title: '参数删除', zIndex: 10000, autoOpen: true,
					      width: 'auto', resizable: false,
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

				
			});
		
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
						//todo:要重新加载数据
						//location.reload(true);
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
										$("#editParameterForm input[name='command']").val('queryParameterSave');
										$("#editParameterForm input[name='queryDefinition.id']").val($("#editQueryForm input[name='id']").val());//查询定义的ID总是取查询编辑的form中的值
										
										var toSubmitData=$('#editParameterForm').serialize();
										$.post(url,
											toSubmitData,
											function(json){
												if(json.success && json.success===true)
												{
													tag.html(json.data.html);
													//todo:要重新加载数据
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
		</script>
	</#if>