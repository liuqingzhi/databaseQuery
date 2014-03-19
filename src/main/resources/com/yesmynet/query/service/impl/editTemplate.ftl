<form id="editTemplateForm">
	<input type="hidden" name="SystemQueryId" value="${(systemQueryId)!""}"><#-- 系统参数表示要执行哪个查询 -->
	<input type="hidden" name="command" value="">
	<input type="hidden" name="queryDefinition.id" value="${(resultTemplate.queryDefinition.id)!""}">
	<input type="hidden" name="id" value="${(resultTemplate.id)!""}">
	
	模板代码：<input type="text" name="code" value="${(resultTemplate.code)!""}"><br>
	模板标题：<input type="text" name="title" value="${(resultTemplate.title)!""}"><br>
	模板内容：<br><textarea rows="14" cols="60" name="content">${(resultTemplate.content)!""}</textarea>
</form>		