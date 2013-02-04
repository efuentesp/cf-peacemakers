package com.peacemakers.controller

import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SurveyAssigned;
import com.peacemakers.security.User;
import com.peacemakers.service.SurveyService;

import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_SCHOOL'])
class SurveyResultsController {
	def SurveyService
	def springSecurityService
	
	def list() {
		println "list: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def selectedSocialGroupId = params.id.toLong()
		
		def socialGroup = SocialGroup.get(selectedSocialGroupId)
		
		// Find all Surveys assigned to the Social Group
		def surveysAssigned = SurveyAssigned.findAll(sort:"sequence") {
			socialGroup.id == selectedSocialGroupId
		}
		
		def surveyArray = []
		def questionArray = []
		surveysAssigned.each { surveyAssigned ->
			def surveyGroupMemberTotal = getSummaryByGroupMember(surveyAssigned)
			surveyArray << [surveyAssigned: surveyAssigned, surveyResults: surveyGroupMemberTotal]
			
			def surveyQuestion = getSummaryByQuestion(surveyAssigned)
			questionArray << [surveyAssigned: surveyAssigned, surveyResults: surveyQuestion]
		}
		println surveyArray
		println questionArray
		
		[socialGroup: socialGroup, surveys: surveyArray, questions: questionArray, user: user]
	}
	
	def getSummaryByGroupMember(SurveyAssigned surveyAssigned) {
		
		def totalPoints = 0
		surveyAssigned.survey.questions.each { question->
			question.choices.each { choice->
				//println choice
				totalPoints += choice.points
			}
		}
		//println "Total: ${totalPoints}"
		
		def groupMemberArray = []
		surveyAssigned.socialGroup.groupMembers.each { groupMember->
			def surveyPoints = 0
			surveyAssigned.answers.each { answer->
				if (answer.groupMember == groupMember) {
					surveyPoints += answer.choiceSelected.points
				}
			}
			groupMemberArray << [groupMember: groupMember, points: surveyPoints, percentage: (surveyPoints/totalPoints)*100]
		}
		
		return groupMemberArray
	}
	
	def getSummaryByQuestion(SurveyAssigned surveyAssigned) {
		
		def totalSocialGroup = surveyAssigned.socialGroup.groupMembers.size()
		
		def questionArray = []
		surveyAssigned.survey.questions.each { question ->
			def questionTotal = 0
			surveyAssigned.answers.each { answer->
				if (answer.question == question) {
					questionTotal += answer.choiceSelected.points
				}
			}
			questionArray << [question: question, points: questionTotal, percentage: (questionTotal/totalSocialGroup)*100]
		}
		return questionArray
	}
}
