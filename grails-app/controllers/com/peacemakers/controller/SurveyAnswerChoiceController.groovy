package com.peacemakers.controller

import grails.plugins.springsecurity.Secured;

import com.peacemakers.domain.SurveyAnswerChoice;
import com.peacemakers.domain.SurveyQuestion;
import com.peacemakers.security.User;

@Secured(['ROLE_ADMIN'])
class SurveyAnswerChoiceController {
	def springSecurityService

    def index() {
		redirect(action: "list", params: params)
	}
	
	def list() {
		println "list: ${params}"

		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
				
		def surveyQuestion = SurveyQuestion.get(params.id)
		
		def surveyAnswerChoices = SurveyAnswerChoice.findAllByQuestion(surveyQuestion)
		
		[surveyQuestion: surveyQuestion, surveyAnswerChoices: surveyAnswerChoices, user: user]
	}
	
	def create() {
		println "create: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def surveyQuestion = SurveyQuestion.get(params.id)
		
		[surveyQuestion: surveyQuestion, user: user]
	}
	
	def save() {
		println "save: ${params}"
		
		def surveyQuestion = SurveyQuestion.get(params.surveyQuestion)
				
		// Get last answer choice
		def sequence =  SurveyAnswerChoice.getNextSequence(params.surveyQuestion.toLong())
		
		//def surveyAnswerChoice = new SurveyAnswerChoice(sequence: 0, code: params.code, description: params.description, points: params.points)
		def surveyAnswerChoice = new SurveyAnswerChoice(sequence: sequence, description: params.description, points: params.points)
		if (!surveyQuestion.addToChoices(surveyAnswerChoice).save(flush: true)) {
			render(view: "create", model: [surveyQuestion: surveyQuestion])
			return
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'survey.question.label', default: 'Survey Question'), surveyQuestion.id])
		redirect(action: "list", params: [id: params.surveyQuestion])
	}
}
