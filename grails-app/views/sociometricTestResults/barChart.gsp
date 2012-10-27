<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="sociometricTestResults.barChart.header" default="Bar Chart" /></title>
		
		<link rel="stylesheet" href="${resource(dir: 'fileupload/css', file: 'fileupload.css')}">
		<link rel="stylesheet" href="${resource(dir: 'd3/css', file: 'd3.css')}"> 
	</head>
	
	<body>
	
		<ul class="breadcrumb">
			<li><a href="${createLink(uri: "/socialGroup/schoolList?city=${socialGroup?.parent?.geo.id}&country=${socialGroup?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.school.list.header" default="Schools" /></a> <span class="divider">/</span></li>
			<li><a href="${createLink(uri: "/socialGroup/groupList?school=${socialGroup?.parent.id}&stage=${socialGroup?.stage.id}&period=${socialGroup?.period.id}&city=${socialGroup?.parent?.geo.id}&country=${socialGroup?.parent?.geo?.parent.id}")}"><g:message code="socialGroup.group.list.header" default="Groups" /></a> <span class="divider">/</span></li>
			<li class="active"><g:message code="default.navbar.results" default="Sociometric Test Results" /></li>
		</ul>
		
		<!-- Page Header -->
		<div>
			<h1>
				<i class="icon-th"></i> <g:message code="sociometricTestResults.barChart.header" default="Bar Chart"/> <small><strong>${socialGroup?.parent.name} (${socialGroup?.stage.name}, ${socialGroup?.period.name} ${socialGroup.name})</strong></small>
			</h1>
		</div> <!-- page-header -->		
	
		<g:hiddenField id= "socialGroup" name="socialGroup" value="${socialGroup.id}" />
		<!--<g:hiddenField id= "sociometricTest" name="sociometricTest" value="${socialGroup.id}" />-->
		
		<div>		
			
			<g:render template="submenu"/>
			
			<g:each in="${sociometricTestResults}" var="criteria">
			<div class="sociometricCriteria">
				Tipo de Votación: ${criteria.criteria.name}
				<g:each in="${criteria.tests}" var="test">
				<div class="row-fluid">
				<div class="sociometricTest">
					<p>Votación ${test.test.sequence}</p>
					<ul class="vote-list">
					<g:each in="${test.results}" var="member">
					<g:if test="${member.results.size() > 0}">
					<li>
					<div class="groupMember">
						<div>
							<div class="groupMemberPhoto">
								<img class="photo" src="${createLink(controller: 'GroupMember', action: 'renderPhoto', id: member.groupMember.id)}"/>
							</div>
							<div class="groupMemberFullName">
								<h4>${member.groupMember}</h4>
							</div>
						</div>
						<div class="sociometricTestResults">
							<ul>
							<g:each in="${member.results}" var="result">
								<li>
									<label>
										<g:message code="${result.criteriaResponse.question}" default="${result.criteriaResponse.question}"/>
									</label>
									<tb:progressBar value="${result.percentage}" color="${result.criteriaResponse.rgbHex}"></tb:progressBar>									
								</li>
							</g:each>
							</ul>
							<br>
						</div>
					</div>
					</li>
					</g:if>
					</g:each>
					</ul>
				</div>
				</div>
				</g:each>
			</div>
			</g:each>
			
		</div>		
	
	</body>
</html>