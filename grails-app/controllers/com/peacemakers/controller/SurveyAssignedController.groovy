package com.peacemakers.controller

import com.peacemakers.domain.GroupMember;
import com.peacemakers.domain.SurveyAnswerChoice;
import com.peacemakers.domain.SurveyAssigned;
import com.peacemakers.domain.SurveyQuestion;
import com.peacemakers.security.User;

import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_STUDENT'])
class SurveyAssignedController {
	def springSecurityService

    def index() {
		redirect(action: "list", params: params)
	}
	
	def list() {
		println "list: ${params}"
		
		// Get User signed
		def userSigned = User.get(springSecurityService.principal.id)
		
		// Find User's Group Member
		def userSignedId = userSigned.id
		def userGroupMember = GroupMember.find {
			user.id == userSignedId
		}
		
		def surveyAssigned = SurveyAssigned.get(params.id)
		
		[	
			user: userSigned,
			groupMember: userGroupMember,
			surveyAssigned: surveyAssigned
		]
	}
	
	def save() {
		println "save: ${params}"
		
		def groupMember = GroupMember.get(params.groupMember)
		def surveyAssigned = SurveyAssigned.get(params.surveyAssigned)
		
		def answers = params.choice
		answers.each { question, answer->
			def surveyQuestion = SurveyQuestion.get(question)
			def surveyAnswerChoice = SurveyAnswerChoice.get(answer)
			surveyAssigned.addToAnswers(groupMember: groupMember, question: surveyQuestion, choiceSelected: surveyAnswerChoice, dateAnswered: new Date()).save(failOnError: true)
		}
		
		redirect(controller: "student", action: "index")
	}
}
