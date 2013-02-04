package com.peacemakers.controller

import java.util.List;

import com.peacemakers.domain.SocialGroup;
import com.peacemakers.security.User;

import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_ADMIN_SCHOOL'])
class SchoolAdminController {
	def springSecurityService

    def index() {
		redirect(action: "groupList", params: params)
	}

	def groupList() {
		println "groupList: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		// Find User's School
		//def userSignedId = user.id
		def schoolBean = SocialGroup.find {
			admin == user
		}
		
		// Get School
		//def schoolBean = SocialGroup.get(params.school)
		
		// Find all Groups within School
		def groups = SocialGroup.findAll {
			parent == schoolBean
		}
		
		// Get a tree of Stages and Periods from a School
		def stageTree = getSocialGroupTree(groups)

		// Find all Social Groups from a School, Stage and Period selected
		def socialGroupList = []
		if (params.stage && params.period) {
			groups.each { g ->
				if (g.stage.id == params.stage.toLong() && g.period.id == params.period.toLong()) {
					socialGroupList << g
				}
			}
		}
		
		[stageTree: stageTree, schoolBean: schoolBean, socialGroupList: socialGroupList, school: params.school, stage: params.stage, period: params.period, city: params.city, country: params.country, user:user, action: 'group']
	}

	private def getSocialGroupTree(List groups) {
		def stages = []
		groups.each { group ->
			if (!stages.contains(group.stage)) {
				stages << group.stage
			}
		}
		
		
		def stageTree = []
		stages.each { s ->
			def periodsArray = []
			def periods = []
			groups.each { g ->
				if (g.stage == s) {
					if (!periods.contains(g.period)) {
						periods << g.period
						periodsArray << [id: g.period.id, name: g.period.name]
					}
				}
			}
			stageTree << [stage: [id: s.id, name: s.name], periods: periodsArray]
		}
		//println stageTree
		
		return stageTree
	}

}
