<#--
          头部信息
   
-->
<#macro head  title="数据库查询" contentType="text/html; charset=UTF-8">
	 <head>
	    <#if contentType!="">
	        <meta http-equiv="Content-Type" content="${contentType}" />
	    </#if>
	    <title>${title}</title>
	    <!-- <link rel="stylesheet" href="${requestContext.contextPath}/css/style.css" type="text/css" media="screen" title="Stylesheet"/>这个样式会导致 jquery ui显示的样式不对 -->
		<link rel="stylesheet" href="${requestContext.contextPath}/css/jquery-ui.css" />
		
  		<script src="${requestContext.contextPath}/js/jquery-1.10.1.js"></script>
  		<script src="${requestContext.contextPath}/js/jquery-ui.js"></script>
  		<script src="${requestContext.contextPath}/js/shortcut.js"></script>
  		<script type="text/javascript">
  			var requestContext="${requestContext.contextPath}"; 
  		</script>
	   <#nested>
	 </head>
	 
</#macro>