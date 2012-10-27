<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="socialGroup.school.edit.header" default="Edit School" /></title>
	</head>
	
	<body>
		<!-- Left Panel -->
		<g:render template="schoolLeftPanel"/>
		
		<!--  Content Panel -->
		<div class="span9">
			<!-- Page Header -->
			<div class="page-header">
				<h1>
					<i class="icon-bell"></i> <g:message code="socialGroup.school.edit.header" default="Edit School"/>
				</h1>
			</div> <!-- page-header -->
			<p></p>
	
			<!-- Error Panel -->
			<g:if test="${flash.message}">
				<div class="alert alert-block alert-info">
					<a class="close" data-dismiss="alert">&times;</a>
					${flash.message}
				</div>
			</g:if>
	
			<!-- Form -->
			<fieldset>
				<g:form action="schoolUpdate" method="post" class="form-horizontal">

					<g:hiddenField name="geo" value="${geoBean?.id}" />
					<g:hiddenField name="city" value="${city}" />
					<g:hiddenField name="country" value="${country}" />					
					<g:hiddenField name="id" value="${schoolBean?.id}" />
					<g:hiddenField name="version" value="${schoolBean?.version}" />

					<tb:controlGroup name="name"
									bean="schoolBean"
									labelMessage="${g.message(code:"socialGroup.name.label", default:"Name")}"
									error="${hasErrors(bean:schoolBean, field:'name', 'error')}"
									errors="${g.renderErrors(bean:schoolBean, field:'name', as:'list')}">
						<g:field type="text" name="schoolName" id="schoolName" class="input-xxlarge" required="" value="${schoolBean.name}" autocomplete='off'/>
					</tb:controlGroup>
					
					<div class="control-group">
						<label class="control-label"><g:message code="socialGroup.groupCategory.label" default="Catagory"/></label>
						<div class="controls">
							<label class="radio">
								<input type="radio" name="groupCategory" id="public" value="PUBLIC" <g:if test = "${schoolBean?.groupCategory.toString() == 'PUBLIC'}"> checked </g:if>>
								<g:message code="socialGroup.groupCategory.PUBLIC.label" default="Public"/>
							</label>							
							<label class="radio">
								<input type="radio" name="groupCategory" id="private" value="PRIVATE" <g:if test = "${schoolBean?.groupCategory.toString() == 'PRIVATE'}"> checked </g:if>>
								<g:message code="socialGroup.groupCategory.PRIVATE.label" default="Private"/>
							</label>
						</div>
					</div>
					
					<tb:controlGroup name="street"
									bean="schoolBean"
									labelMessage="${g.message(code:"socialGroup.address.street.label", default:"Street")}"
									error="${hasErrors(bean:schoolBean, field:'address?.street', 'error')}"
									errors="${g.renderErrors(bean:schoolBean, field:'address.street', as:'list')}">
						<g:textArea name="schoolStreet" rows="5" class="input-xxlarge" required="" value="${schoolBean?.address?.street}" autocomplete='off'/> <br>
						<span class="input-xxlarge uneditable-input">${geoBean.name}, ${geoBean?.parent.name} (${geoBean?.parent?.parent.name})</span>
					</tb:controlGroup>					
					
					<div class="form-actions">
						<button type="submit" class="btn btn-success">
							<i class="icon-ok icon-white"></i>
							<g:message code="default.button.update.label" default="Update"/>
						</button>
						<a href="${createLink(uri: "/socialGroup/${action}List?city=${city}&country=${country}")}" class="btn">
							<i class="icon-ban-circle"></i>
							<g:message code="default.button.cancel.label" default="Cancel"/>
						</a> <!-- /btn -->	
					</div>
					
				</g:form>
			</fieldset>

		</div> <!-- /span9 -->
	
	</body>
</html>
