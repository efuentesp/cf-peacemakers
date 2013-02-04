<!doctype html>
<html>
	<head>
		<sec:ifAllGranted roles="ROLE_ADMIN">
			<meta name="layout" content="main">
		</sec:ifAllGranted>
		<sec:ifAllGranted roles="ROLE_ADMIN_SCHOOL">
			<meta name="layout" content="schoolAdmin">
		</sec:ifAllGranted>
		<title><g:message code="groupMember.list.header" default="Group Members" /></title>
		<link rel="stylesheet" href="${resource(dir: 'fileupload/css', file: 'fileupload.css')}"> 
	</head>
	
	<body>
	
		<ul class="breadcrumb">
			<sec:ifAllGranted roles="ROLE_ADMIN">
				<li><a href="${createLink(uri: "/socialGroup/schoolList?city=${socialGroupSelected?.parent?.geo.id}&country=${socialGroupSelected?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.school.list.header" default="Schools" /></a> <span class="divider">/</span></li>
				<li><a href="${createLink(uri: "/socialGroup/groupList?school=${socialGroupSelected?.parent.id}&stage=${socialGroupSelected?.stage.id}&period=${socialGroupSelected?.period.id}&city=${socialGroupSelected?.parent?.geo.id}&country=${socialGroupSelected?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.group.list.header" default="Groups" /></a> <span class="divider">/</span></li>
			</sec:ifAllGranted>
			<sec:ifAllGranted roles="ROLE_ADMIN_SCHOOL">
				<li><a href="${createLink(uri: "/schoolAdmin/groupList?school=${socialGroupSelected?.parent.id}&stage=${socialGroupSelected?.stage.id}&period=${socialGroupSelected?.period.id}&city=${socialGroupSelected?.parent?.geo.id}&country=${socialGroupSelected?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.group.list.header" default="Groups" /></a> <span class="divider">/</span></li>
			</sec:ifAllGranted>
			<li class="active"><g:message code="groupMember.list.header" default="Group Members" /></li>
		</ul>
		
		<!--  Content Panel -->
		<div class="span9">

			<!-- Page Header -->
			<div>
				<h1>
					<i class="icon-group"></i> <g:message code="groupMember.list.header" default="Group Members"/> <small><strong>${socialGroupSelected?.parent.name} (${socialGroupSelected?.stage.name}, ${socialGroupSelected?.period.name} ${socialGroupSelected.name})</strong></small>
				</h1>
			</div> <!-- page-header -->

			<g:form action="bulkDelete" method="post">
				<g:hiddenField name="socialGroup" value="${socialGroupSelected.id}" />
			
				<!-- Action Bar -->
				<g:render template="action"/>
				    	
				<!-- Table -->
				<g:render template="table"/>
			</g:form>
		
			<!-- Pagination --> 
			<g:render template="pagination"/>

		</div> <!-- /span9 -->
	
	</body>
</html>
