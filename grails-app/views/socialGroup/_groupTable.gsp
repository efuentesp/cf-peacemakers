<g:if test="${socialGroupList}">
	<table class="table">
		<thead>
			<tr>
				<th><g:message code="socialGroup.groupType.stage.label" default="Stage"/></th>
				<th><g:message code="socialGroup.groupType.period.label" default="Period"/></th>
				<th><g:message code="socialGroup.groupType.group.label" default="Group"/></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
	
			<g:each in="${socialGroupList}" status="i" var="socialGroupBean">
			<tr>
				<td>
					${socialGroupBean?.stage.name}
				</td>
				<td>
					${socialGroupBean?.period.name}
				</td>
				<td>
					${fieldValue(bean: socialGroupBean, field: "name")}
				</td>			
				<td class="link">
	
					<div class="btn-toolbar" style="margin: 0;">
						<div class="btn-group">
							<a href="${createLink(uri: "/groupMember/list")}/${fieldValue(bean: socialGroupBean, field: "id")}" class="btn btn-success">
								<i class="icon-group icon-white"></i>
								<g:message code="groupMember.list.header" default="Group Members"/>
							</a>
						</div>
						<div class="btn-group">
							<button class="btn btn-info dropdown-toggle" data-toggle="dropdown"><g:message code="default.navbar.results" default="Results"/> <span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${createLink(uri: '/sociometricTestResults/matrixChart')}/${socialGroupBean.id}"><i class="icon-comment-alt"></i> <g:message code="default.navbar.results.sociometricTests" default="Sociometric Tests"/></a></li>
								<li><a href="${createLink(uri: '/surveyResults/list')}/${socialGroupBean.id}"><i class="icon-check"></i> <g:message code="default.navbar.results.surveys" default="Surveys"/></a></li>
							</ul>
						</div>
						<div class="btn-group">
							<button class="btn dropdown-toggle" data-toggle="dropdown"><g:message code="default.button.action.label" default="Action"/> <span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="${createLink(uri: "/socialGroupSociometricTest/list")}/${fieldValue(bean: socialGroupBean, field: "id")}"><i class="icon-comment-alt"></i> <g:message code="socialGroup.sociometricTest.button.activate.label" default="Activate Sociometric Test"/></a></li>
								<li><a href="${createLink(uri: "/socialGroupSurvey/list")}/${fieldValue(bean: socialGroupBean, field: "id")}"><i class="icon-check"></i> <g:message code="socialGroup.survey.button.activate.label" default="Activate Survey"/></a></li>
								<li class="divider"></li>						
								<li><a href="${createLink(uri: "/socialGroup/${action}Edit")}/${fieldValue(bean: socialGroupBean, field: "id")}"><i class="icon-edit"></i> <g:message code="default.button.edit.label" default="Edit"/></a></li>
								<!-- <li class="divider"></li> -->
								<li><a href="${createLink(uri: "/socialGroup/${action}Delete")}/${fieldValue(bean: socialGroupBean, field: "id")}"><i class="icon-trash"></i> <g:message code="default.button.delete.label" default="Delete"/></a></li>
							</ul>
						</div>
					</div>						
				</td>
			</tr>
			</g:each>
	
		</tbody>
	</table> <!-- table -->
</g:if>
<g:elseif test="${stageTree}">
	<h4><small><g:message code="groupMember.warning.selectGroup.label" default="Empty Groups. Press 'Add' to create Groups."/></small></h4>
</g:elseif>
<g:else>
	<h4><small><g:message code="groupMember.warning.emptySchool.label" default="Empty Groups. Press 'Add' to create Groups."/></small></h4>
</g:else>
