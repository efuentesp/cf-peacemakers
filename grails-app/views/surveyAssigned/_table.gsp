<div>
	<g:form action="save" name="surveyForm">
	
		<g:hiddenField name="groupMember" value="${groupMember.id}" />
		<g:hiddenField name="surveyAssigned" value="${surveyAssigned.id}" />

		<ul>	
		<g:each in="${surveyAssigned.survey.questions}" var="question">
			<li>
				<fieldset>
					<legend>
						<strong>${question.sequence}. ${question.description}</strong>
					</legend>
					<div>
						<!-- Survey Question Choices -->
						<div class="controls">
						<g:each in="${question.choices}" var="choice">
			
								<label class="radio">
									<input type="radio" name="choice.${question.id}" id="choice${choice.id}" value="${choice.id}">
									${choice.description}
								</label>
			
						</g:each>
						</div>
					</div>
				</fieldset>
			</li>
		</g:each>
		</ul>
		
		<div class="form-actions">
			<div class="span3">
				<button type="submit" class="btn btn-primary btn-large btn-block">
					<i class="icon-ok-sign icon-white"></i>
					<g:message code="surveyAssigned.button.finish.label" default="F I N I S H"/>
				</button>
			</div>						
		</div>
	</g:form>
</div>