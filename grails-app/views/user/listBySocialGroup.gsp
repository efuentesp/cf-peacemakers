<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="groupMember.password.list.header" default="Group Member Passwords" /></title>
		<link rel="stylesheet" href="${resource(dir: 'fileupload/css', file: 'fileupload.css')}"> 
	</head>
	
	<body>
	
		<ul class="breadcrumb">
			<li><a href="${createLink(uri: "/socialGroup/schoolList?city=${socialGroupSelected?.parent?.geo.id}&country=${socialGroupSelected?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.school.list.header" default="Schools" /></a> <span class="divider">/</span></li>
			<li><a href="${createLink(uri: "/socialGroup/groupList?school=${socialGroupSelected?.parent.id}&stage=${socialGroupSelected?.stage.id}&period=${socialGroupSelected?.period.id}&city=${socialGroupSelected?.parent?.geo.id}&country=${socialGroupSelected?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.group.list.header" default="Groups" /></a> <span class="divider">/</span></li>
			<li><a href="${createLink(uri: "/groupMember/list")}/${socialGroupSelected.id}"><g:message code="groupMember.list.header" default="Group Members" /></a> <span class="divider">/</span></li>
			<li class="active"><g:message code="groupMember.password.list.header" default="Group Member Passwords" /></li>
		</ul>
		
		<!--  Content Panel -->
		<div class="span12">

			<!-- Page Header -->
			<div>
				<h1>
					<i class="icon-unlock"></i> <g:message code="groupMember.password.list.header" default="Group Member Passwords"/>
					<small><strong>${socialGroupSelected?.parent.name} (${socialGroupSelected?.stage.name}, ${socialGroupSelected?.period.name} ${socialGroupSelected.name})</strong></small>
				</h1>
			</div> <!-- page-header -->

			<!-- Action Bar -->
			<g:render template="action"/>
			    	
			<!-- Table -->
			<g:render template="tableSocialGroup"/>
		
			<!-- Pagination --> 
			<g:render template="pagination"/>

		</div> <!-- /span9 -->
	
	</body>
</html>
