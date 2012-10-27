<table class="table">
	<thead>
		<tr>
			<th><g:message code="groupMember.person.photo.label" default="Photo"/></th>
			<th><g:message code="groupMember.person.fullName.label" default="Full Name"/></th>
			<th><g:message code="groupMember.person.gender.label" default="Gender"/></th>
			<th><g:message code="groupMember.person.birthday.label" default="Birthday"/></th>
			<th></th>
		</tr>
	</thead>
	<tbody>

		<!-- TODO: Show a message when table is empty -->

		<g:each in="${groupMemberList}" var="groupMemberBean">
		<tr>
			<td><img class="photo_small" src="${createLink(controller:'GroupMember', action:'renderPhoto', id:groupMemberBean.id)}"/></td>
			<td>${groupMemberBean.person.firstSurname} ${groupMemberBean.person.secondSurname}, ${groupMemberBean.person.firstName}</td>
			<td>
				<g:message code="groupMember.person.gender.${groupMemberBean.person.gender}.label" default="--"/>
			</td>
			<g:set var="birthday" value="${groupMemberBean.person.birthday}" />
			<td><g:formatDate date="${groupMemberBean.person.birthday}" type="date" style="LONG"/></td>
			<td class="link">
				
				<div class="btn-toolbar" style="margin: 0;">
			
					<div class="btn-group">
						<button class="btn dropdown-toggle" data-toggle="dropdown"><g:message code="default.button.action.label" default="Action"/> <span class="caret"></span></button>
						<ul class="dropdown-menu">
							<li><a href="${createLink(uri: "/groupMember/edit")}/${groupMemberBean.id}"><i class="icon-edit"></i> <g:message code="default.button.edit.label" default="Edit"/></a></li>
							<!-- <li class="divider"></li> -->
							<li><a href="${createLink(uri: "/groupMember/delete")}/${groupMemberBean.id}"><i class="icon-trash"></i> <g:message code="default.button.delete.label" default="Delete"/></a></li>
						</ul>
					</div>
			
				</div>
										
			</td>
		</tr>
		</g:each>

	</tbody>
</table> <!-- table -->
