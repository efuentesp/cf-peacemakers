package com.peacemakers.controller

import grails.plugins.springsecurity.Secured;

import com.peacemakers.domain.QuestionType;
import com.peacemakers.domain.Survey;
import com.peacemakers.domain.SurveyQuestion;
import com.peacemakers.security.User;

@Secured(['ROLE_ADMIN'])
class SurveyQuestionController {
	def springSecurityService

    def index() {
		redirect(action: "list", params: params)
	}
	
	def list() {
		println "list: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def survey = Survey.get(params.id)
		
		[survey: survey, user: user]
	}
	
	def create() {
		println "create: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def survey = Survey.get(params.id)
		
		[survey: survey, user: user]
	}
	
	def save() {
		println "save: ${params}"
		
		def survey = Survey.get(params.survey)

		def type
		switch (params.type) {
			case 'MULTI_CHOICE':
				type = QuestionType.MULTI_CHOICE
				break
			case 'MULTIPLE_CORRECT':
				type = QuestionType.MULTIPLE_CORRECT
				break
			default:
				type = null
		}
				
		def surveyQuestion = new SurveyQuestion(sequence: params.sequence, code: params.code, description: params.description, type: type)
		if (!survey.addToQuestions(surveyQuestion).save(flush: true)) {
			render(view: "create", model: [survey: survey])
			return
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'survey.question.label', default: 'Survey Question'), survey.id])
		redirect(action: "list", params: [id: params.survey])
	}
}
