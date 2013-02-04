<!doctype html>
<html>
	<head>
		<sec:ifAllGranted roles="ROLE_ADMIN">
			<meta name="layout" content="main">
		</sec:ifAllGranted>
		<sec:ifAllGranted roles="ROLE_ADMIN_SCHOOL">
			<meta name="layout" content="schoolAdmin">
		</sec:ifAllGranted>
		<title><g:message code="user.list.header" default="Users" /></title> 
	</head>
	
	<body>
	
		<!-- Left Panel -->
		<g:render template="leftPanel"/>
			
		<!--  Content Panel -->
		<div class="span9">

			<!-- Page Header -->
			<div class="page-header">
				<h1>
					<i class="icon-group"></i> <g:message code="user.list.header" default="Users"/>
				</h1>
			</div> <!-- page-header -->

			<!-- Action Bar -->
			<g:render template="action"/>
			    	
			<!-- Table -->
			<g:if test="${users}">
				<g:render template="table"/>
			</g:if>
		
			<!-- Pagination --> 
			<g:render template="pagination"/>

		</div> <!-- /span9 -->
	
	</body>
</html>
