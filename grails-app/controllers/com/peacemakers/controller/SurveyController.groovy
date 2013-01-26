package com.peacemakers.controller

import grails.plugins.springsecurity.Secured;

import com.peacemakers.domain.Survey;
import com.peacemakers.security.User;

@Secured(['ROLE_ADMIN'])
class SurveyController {
	def springSecurityService

    def index() {
		redirect(action: "list", params: params)
	}
	
	def list() {
		println "list: ${params}"

		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
				
		def surveys = Survey.list()
		
		[surveys: surveys, user: user]
	}
	
	def create() {
		println "create: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		[user: user]
	}
	
	def save() {
		println "save: ${params}"
		
		//def survey = new Survey(code: params.code, name: params.name)
		def survey = new Survey(name: params.name)
		if (!survey.save(flush: true)) {
			render(view: "create", model: [surveyBean: survey])
			return
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'survey.label', default: 'Survey'), survey.id])
		redirect(action: "list")
	}
}
