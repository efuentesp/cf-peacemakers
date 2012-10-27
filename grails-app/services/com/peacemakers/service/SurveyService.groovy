package com.peacemakers.service

import com.peacemakers.domain.GroupMember;
import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.Survey;
import com.peacemakers.domain.SurveyAssigned;

class SurveyService {

    def getSummaryByQuestion(SurveyAssigned surveyAssigned) {

		def groupMemberArray = []
		surveyAssigned.socialGroup.groupMembers.each { groupMember->
			println "groupMember : ${groupMember}"
			def surveyPoints = 0
			surveyAssigned.answers.each { answer->
				if (answer.groupMember == surveyAssigned.socialGroup.groupMembers) {
					println "   ${answer}"
					surveyPoints += choiceSelected.points
				}
			}
			groupMemberArray << [groupMember: groupMember, points: surveyPoints]
		}
		
		return groupMemberArray
    }
}
