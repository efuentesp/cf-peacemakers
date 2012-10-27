<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="groupMember.createZip.header" default="Create Students from ZIP file" /></title>
	</head>
	
	<body>
		
		<ul class="breadcrumb">
			<li><a href="${createLink(uri: "/socialGroup/schoolList?city=${socialGroupSelected?.parent?.geo.id}&country=${socialGroupSelected?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.school.list.header" default="Schools" /></a> <span class="divider">/</span></li>
			<li><a href="${createLink(uri: "/socialGroup/groupList?school=${socialGroupSelected?.parent.id}&stage=${socialGroupSelected?.stage.id}&period=${socialGroupSelected?.period.id}&city=${socialGroupSelected?.parent?.geo.id}&country=${socialGroupSelected?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.group.list.header" default="Groups" /></a> <span class="divider">/</span></li>
			<li><a href="${createLink(uri: "/groupMember/list/${socialGroupSelected.id}")}"><g:message code="groupMember.list.header" default="Group Member" /></a> <span class="divider">/</span></li>
			<li class="active"><g:message code="groupMember.label" default="Group Member" /></li>
		</ul>
		
		<!--  Content Panel -->
		<div class="span9">
			<!-- Page Header -->
			<div class="page-header">
				<h1>
					<i class="icon-group"></i>  <g:message code="groupMember.createZip.header" default="Create Students from ZIP file"/>
					<p><small><strong>${socialGroupSelected?.parent.name} (${socialGroupSelected?.stage.name}, ${socialGroupSelected?.period.name} ${socialGroupSelected.name})</strong></small>
				</h1>
			</div> <!-- page-header -->
			<p></p>
	
			<!-- Error Panel -->
			<g:if test="${flash.message}">
				<div class="alert alert-block alert-alert">
					<a class="close" data-dismiss="alert">&times;</a>
					${flash.message}
				</div>
			</g:if>

			<!--  
			<g:hasErrors bean="${groupMemberBean}">
				<div class="alert alert-block alert-error">
					<a class="close" data-dismiss="alert">&times;</a>
					<ul>
						<g:eachError bean="${groupMemberBean}" var="error">
						<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
							<g:message error="${error}"/>
						</li>
						</g:eachError>
					</ul>
				</div>
			</g:hasErrors>
			-->

	
			<!-- Form -->
			<fieldset>
				<g:uploadForm action="saveZip" method="post" class="form-horizontal">

						<g:hiddenField name="socialGroup" value="${socialGroupSelected?.id}" />
						
						<div class="control-group">
							<label class="control-label"><g:message code="groupMember.zipFile.label" default="ZIP file"/></label>
							<div class="controls">
								<input type="file" id="zipFile" name="zipFile"/>
							</div>
						</div>
												
						<div class="form-actions">
							<button type="submit" class="btn btn-primary">
								<i class="icon-ok icon-white"></i>
								<g:message code="default.button.load.label" default="Load"/>
							</button>
							<a href="${createLink(uri: "/groupMember/list/${socialGroupSelected?.id}")}" class="btn">
								<i class="icon-ban-circle"></i>
								<g:message code="default.button.cancel.label" default="Cancel"/>
							</a> <!-- /btn -->							
						</div>
					
				</g:uploadForm>
			</fieldset>

		</div> <!-- /span -->
    
	</body>
	
</html>
