<!doctype html>
<html>
	<head>
		<sec:ifAllGranted roles="ROLE_ADMIN">
			<meta name="layout" content="main">
		</sec:ifAllGranted>
		<sec:ifAllGranted roles="ROLE_ADMIN_SCHOOL">
			<meta name="layout" content="schoolAdmin">
		</sec:ifAllGranted>
		<title><g:message code="surveyResults.header" default="Surveys" /></title>
	</head>
	
	<body>
	
		<ul class="breadcrumb">
			<sec:ifAllGranted roles="ROLE_ADMIN">
				<li><a href="${createLink(uri: "/socialGroup/schoolList?city=${socialGroup?.parent?.geo.id}&country=${socialGroup?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.school.list.header" default="Schools" /></a> <span class="divider">/</span></li>
				<li><a href="${createLink(uri: "/socialGroup/groupList?school=${socialGroup?.parent.id}&stage=${socialGroup?.stage.id}&period=${socialGroup?.period.id}&city=${socialGroup?.parent?.geo.id}&country=${socialGroup?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.group.list.header" default="Groups" /></a> <span class="divider">/</span></li>
			</sec:ifAllGranted>
			<sec:ifAllGranted roles="ROLE_ADMIN_SCHOOL">
				<li><a href="${createLink(uri: "/schoolAdmin/groupList?school=${socialGroup?.parent.id}&stage=${socialGroup?.stage.id}&period=${socialGroup?.period.id}&city=${socialGroup?.parent?.geo.id}&country=${socialGroup?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.group.list.header" default="Groups" /></a> <span class="divider">/</span></li>
			</sec:ifAllGranted>
			<li class="active"><g:message code="default.navbar.results" default="Sociometric Test Results" /></li>
		</ul>
		
		<!-- Page Header -->
		<div>
			<h1>
				<i class="icon-check"></i> <g:message code="surveyResults.header" default="Surveys"/> <small><strong>${socialGroup?.parent.name} (${socialGroup?.stage.name}, ${socialGroup?.period.name} ${socialGroup.name})</strong></small>
			</h1>
		</div> <!-- page-header -->
		
		<g:hiddenField id= "socialGroup" name="socialGroup" value="${socialGroup.id}" />
		
		<div>
			
			<ul>
			<g:each in="${surveys}" var="survey">
				<li>
					<g:message code="${survey.surveyAssigned.survey.name}" default="${survey.surveyAssigned.survey.name}"/> ${survey.surveyAssigned.id}
					<ul>
					<g:each in="${survey.surveyResults}" var="${result}">
						<li>
							${result.groupMember} = 
							<g:formatNumber number="${result.percentage}" type="number" maxFractionDigits="2" roundingMode="HALF_DOWN" />%
						</li>
					</g:each>
					</ul>
				</li>
			</g:each>
			</ul>
			
			<ul>
			<g:each in="${questions}" var="question">
				<li>
					<g:message code="${question.surveyAssigned.survey.name}" default="${question.surveyAssigned.survey.name}"/> ${question.surveyAssigned.id}
					<ul>
					<g:each in="${question.surveyResults}" var="${result}">
						<li>
							<g:message code="${result.question.description}" default="${result.question.description}"/> =
							<g:formatNumber number="${result.percentage}" type="number" maxFractionDigits="2" roundingMode="HALF_DOWN" />%
						</li>
					</g:each>
					</ul>
				</li>
			</g:each>
			</ul>			

		</div>		
	
	</body>
</html>