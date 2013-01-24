
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
								<div style="position:relative; height:120px;">
								<img class="photo_big" src="${createLink(controller:'student', action:'renderPhoto', id:groupMemberBean.id)}" style="z-index:0;position:absolute;"/>
								<img class="photo_big" src="${resource(dir: 'fileupload/img', file: 'cover.gif')}" style="z-index:1;position:absolute;" />
								</div>
							</div>
							<div class="student-name">
								<h4>${groupMemberBean.getFullName()}</h4>
							</div>					
							<div class="student-vote">
								<g:each in="${sociometricCriteriaResponseList}" var="sociometricCriteriaResponse">
									<label>
										<input type="radio" name="vote.${groupMemberBean.id}" id="sociometricCriteriaResponse${sociometricCriteriaResponse.sequence}" value="${sociometricCriteriaResponse.id}">
										<g:message code="${sociometricCriteriaResponse.question}" default="${sociometricCriteriaResponse.question}"/>
									</label>								
								</g:each>
								<label>
									<input type="radio" name="vote.${groupMemberBean.id}" id="sociometricCriteriaResponse0" value="0">
									<g:message code="sociometriccriteria.question.default.name" default="None"/>
								</label>								
								<!-- 
								<select name="vote.${groupMemberBean.id}">
									<option value=""><g:message code="sociometriccriteria.question.default.name" default="-- Select one --"/></option>
									<g:each in="${sociometricCriteriaResponseList}" var="sociometricCriteriaResponse">
										<option value="${sociometricCriteriaResponse.id}">
											<g:message code="${sociometricCriteriaResponse.question}" default="${sociometricCriteriaResponse.question}"/>
										</option>
									</g:each>
								</select>
								-->						
							</div>
						</div>
					</li>
				</g:each>
			</ul>
		</fieldset>
		
		<div class="form-actions">
			<div class="span3">
				<button id="finish" type="submit" class="btn btn-primary btn-large btn-block" disabled>
					<i class="icon-ok-sign icon-white"></i>
					<g:message code="sociometriccriteria.button.finish.label" default="V O T E"/>
				</button>
			</div>						
		</div>
	</g:form>
	
	<script type="text/javascript">
	    $("input[type='radio']").change(function() {
	        //console.log($("input[type='radio']:checked").val());
	        //console.log($("input[type='radio']:checked"));
	        var i = 0;
	    	$.each(
	    		$("input[type='radio']:checked"),
	   			function( intIndex, obj ) {
		   			if (obj.value > 0) {
			   			i++;
		   				//console.log(obj.value);
		   			}
	   			}
	    	);
	    	if (i > 0) {
	    		$('#finish').removeAttr("disabled");
		    	//console.log("SI se puede salir");
		    } else {
		    	$('#finish').attr("disabled", "disabled");;
			    //console.log("NO se puede salir")
			}
	    });		
	</script>
