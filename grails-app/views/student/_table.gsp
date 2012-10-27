
	<g:form action="vote" name="votingForm">
		<fieldset>
			<g:hiddenField name="socialGroup" value="${groupMember?.socialGroup.id}" />
			<g:hiddenField name="sociometricTest" value="${sociometricTest.id}" />
			<g:hiddenField name="fromGroupMember" value="${groupMember.id}" />
		
			<ul class="vote-list">
				<g:each in="${groupMemberList}" var="groupMemberBean">
					<li>
						<div class="student-card">
							<div class="student-photo">
								<img class="photo_big" src="${createLink(controller:'student', action:'renderPhoto', id:groupMemberBean.id)}"/>
							</div>
							<div class="student-name">
								<h4>${groupMemberBean.getFullName()}</h4>
							</div>					
							<div class="student-vote">
								<!--<g:each in="${sociometricCriteriaResponseList}" var="sociometricCriteriaResponse">
									<label>
										<input type="radio" name="vote.${groupMemberBean.id}" id="sociometricCriteriaResponse${sociometricCriteriaResponse.sequence}" value="${sociometricCriteriaResponse.id}">
										<g:message code="${sociometricCriteriaResponse.question}" default="${sociometricCriteriaResponse.question}"/>
									</label>								
								</g:each>-->
								<select name="vote.${groupMemberBean.id}">
									<option value=""><g:message code="sociometriccriteria.question.default.name" default="-- Select one --"/></option>
									<g:each in="${sociometricCriteriaResponseList}" var="sociometricCriteriaResponse">
										<option value="${sociometricCriteriaResponse.id}">
											<g:message code="${sociometricCriteriaResponse.question}" default="${sociometricCriteriaResponse.question}"/>
										</option>
									</g:each>
								</select> 							
							</div>
						</div>
					</li>
				</g:each>
			</ul>
		</fieldset>
		
		<div class="form-actions">
			<div class="span3">
				<button type="submit" class="btn btn-primary btn-large btn-block">
					<i class="icon-ok-sign icon-white"></i>
					<g:message code="sociometriccriteria.button.finish.label" default="V O T E"/>
				</button>
			</div>						
		</div>
	</g:form>
