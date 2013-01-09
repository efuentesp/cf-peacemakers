package com.peacemakers.controller

import grails.converters.JSON;
import grails.plugins.springsecurity.Secured;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.servlet.support.RequestContextUtils as RCU

import com.peacemakers.domain.Address;
import com.peacemakers.domain.GeoType;
import com.peacemakers.domain.Geography;
import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SocialGroupCategory;
import com.peacemakers.domain.SocialGroupPeriod;
import com.peacemakers.domain.SocialGroupStage;
import com.peacemakers.domain.SocialGroupType;
import com.peacemakers.security.User;

@Secured(['ROLE_ADMIN'])
class SocialGroupController {
	def springSecurityService
	
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		params.lang = user.lang
		
		redirect(action: "schoolList", params: params)
	}
	
	def schoolList() {
		println "schoolList: ${params}"
		
		def lang = RCU.getLocale(request)
		//def lang = session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE']
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def socialGroupList, city, country, countrySelectedId=0, citySelectedId=0
		
		if (params.city) {
			
			// Get Country selected
			country = Geography.get(params.country.toLong())
			//countrySelectedId = country.id.toInteger()
			
			// Get City selected
			city = Geography.get(params.city.toLong())
			//citySelectedId = city.id.toInteger()
			
			// Find all Schools from a City
			//socialGroupList = SocialGroup.findAllByGroupType(SocialGroupType.SCHOOL)
			socialGroupList = SocialGroup.findAll {
				geo == city
			}
			
		}
		
		if (params.schoolName) {
			socialGroupList = SocialGroup.findByName(params.schoolName)
			//println socialGroupList
			
			if (socialGroupList) {
				city = socialGroupList.geo
				country = socialGroupList.geo?.parent
			}
		}
		
		// Find all Countries to use in combo-box
		def countries = Geography.findAllByGeoType(GeoType.COUNTRY)
		
		// List of Schools to use in Typehead search
		def schoolList = SocialGroup.findAll {
				groupType == SocialGroupType.SCHOOL
		} 
		def schoolJSON = schoolList.name as JSON
		
		[country: country, city: city, countries: countries, socialGroupList: socialGroupList, schoolJSON: schoolJSON, user: user, lang: lang, action: 'school']
	}
	
	def schoolCreate() {
		//println "schoolCreate: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		// Find all Countries to use in combo-box
		def countries = Geography.findAllByGeoType(GeoType.COUNTRY)
		
		// Get City selected
		def geo = Geography.get(params.city.toLong())
		
		[countries: countries, geoBean: geo, city: geo.id, country: geo?.parent?.parent.id, user: user, action: 'school']
	}
	
	def schoolEdit() {
		//println "schoolEdit: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		// Find all Countries to use in combo-box
		def countries = Geography.findAllByGeoType(GeoType.COUNTRY)
		
		def school = SocialGroup.get(params.id)
		def geo = Geography.get(school.geo.id)
		if (!school) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), params.id])
            redirect(action: "schoolList")
            return
        }

        [countries: countries, schoolBean:school, geoBean:geo, city: geo.id, country: geo?.parent?.parent.id, user: user, action:'school']
	}
	
	def schoolDelete() {
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		// Find all Countries to use in combo-box
		def countries = Geography.findAllByGeoType(GeoType.COUNTRY)
		
		def school = SocialGroup.get(params.id)
		def geo = Geography.get(school.geo.id)
		if (!school) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), params.id])
			redirect(action: "schoolList")
			return
		}

		[countries: countries, schoolBean: school, geoBean:geo, city: geo.id, country: geo?.parent?.parent.id, user: user, action:'school']
	}
	
	def schoolSave() {
		//println "schoolSave ${params}"
		
		def geoBean = Geography.get(params.geo)
		def address = new Address(street:params.schoolStreet)
		def groupCategory
		
		switch (params.groupCategory) {
			case 'PUBLIC':
				groupCategory = SocialGroupCategory.PUBLIC
				break
			case 'PRIVATE':
				groupCategory = SocialGroupCategory.PRIVATE
				break
			default:
				groupCategory = null
		}
		
		def school = new SocialGroup(name:params.schoolName, groupType:SocialGroupType.SCHOOL, groupCategory: groupCategory, geo:geoBean, address:address)
		if (!school.save(flush: true)) {
			render(view: "schoolCreate", model: [schoolBean: school, action:'school'])
			return
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), school.id])
		redirect(action: "schoolList", params: [city: params.city, country: params.country])
	}
	
	def schoolUpdate() {
		//println "schoolUpdate: ${params}"
        def school = SocialGroup.get(params.id)
        if (!school) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), params.id])
			//println flash.message
            redirect(action: "schoolEdit", params: [schoolBean: school, action:'school'])
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (school.version > version) {
                school.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'socialGroup.groupType.school.label', default: 'School')] as Object[],
                          "Another user has updated this School while you were editing")
                render(view: "schoolEdit", model: [schoolBean: school, action:'school'])
                return
            }
        }

		school.name = params.schoolName
		school.address.street = params.schoolStreet
		switch (params.groupCategory) {
			case 'PUBLIC':
				school.groupCategory = SocialGroupCategory.PUBLIC
				break
			case 'PRIVATE':
				school.groupCategory = SocialGroupCategory.PRIVATE
				break
			default:
				groupCategory = null
		}

        if (!school.save(flush: true)) {
            render(view: "schoolEdit", model: [schoolBean: school, action:'school'])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), school.id])
		//println flash.message
        redirect(action: "schoolList", params: [city: params.city, country: params.country])
	}
	
	def schoolRemove() {
		def school = SocialGroup.get(params.id)
		if (!school) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), params.id])
			redirect(action: "schoolList")
			return
		}

		try {
			school.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), params.id])
			redirect(action: "schoolList", params: [city: params.city, country: params.country])
		}
		catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'socialGroup.groupType.school.label', default: 'School'), params.id])
			redirect(action: "schoolDelete", id: params.id)
		}
	}
	
	def stageList() {
		//println "stageList: ${params}"
		def socialGroupList = []
		def school = SocialGroup.findAllByGroupType(SocialGroupType.SCHOOL)
		if (params.school) {
			def schoolId = params.school.toLong()
			socialGroupList = SocialGroup.findAll {
				parent.id == schoolId
			}
		}
		[socialGroupList: socialGroupList, schoolList:school, schoolSelected:params.school, action: 'stage']
	}
	
	def periodList() {
		def socialGroupList = SocialGroup.findAllByGroupType(SocialGroupType.PERIOD)
		def school = SocialGroup.findAllByGroupType(SocialGroupType.SCHOOL)
		[socialGroupList: socialGroupList, schoolList:school, schoolSelected:params.school, action: 'period']
	}
	
	// Get all Cities from a State
	def getCitiesByCountry() {
		//println "getCitiesByCountry: ${params}"
		
		if (params.country) {
			def lst = []
			def countrySelectedId = params.country.toLong()
			
			def cities = []
			def allCities = Geography.findAllByGeoType(GeoType.CITY)
			allCities.each { city->
				//println "${city.name} (${city?.parent.name}, ${city?.parent?.parent.name})"
				if (city?.parent?.parent.id == countrySelectedId) {
					cities << city
				}
			}
			
			cities.each { city->
				def parent = Geography.get(city.parent.id)
				lst << [id: city.id, name: city.name, parent: parent.name]
			}
		
			render g.selectWithOptGroup(from: lst, name: "city", optionKey: 'id', optionValue:'name', groupBy: 'parent', required: "", noSelection: ['':'-- Seleccionar --'], class: "span11")
		} else {
			render g.select(name: "city", from: "", disabled: 'true', class: "span11")
		}
		
	}
	
	
	def groupList() {
		println "groupList: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		// Get School
		def schoolBean = SocialGroup.get(params.school)
		
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
	
	def groupCreate() {
		//println "groupCreate: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def schoolBean = SocialGroup.get(params.school)
		
		def groups = SocialGroup.findAll {
			parent == schoolBean
		}
		
		def stageTree = getSocialGroupTree(groups)
		
		def stageJSON = SocialGroupStage.list().name as JSON
		
		def periodJSON = SocialGroupPeriod.list().name as JSON
		
		[stageTree: stageTree, stageJSON: stageJSON, periodJSON: periodJSON, schoolBean: schoolBean, school: params.school, stage: params.stage, period: params.period, city: params.city, country: params.country, user:user, action: 'group']
	}
	
	def groupSave() {
		//println "groupSave: ${params}"
		
		def stage = SocialGroupStage.findByName(params.stage)
		if (!stage) {
			stage = new SocialGroupStage(name: params.stage).save(failOnError: true)
		}
		def period = SocialGroupPeriod.findByName(params.period)
		if (!period) {
			period = new SocialGroupPeriod(name: params.period).save(failOnError: true)
		}
		def school = SocialGroup.get(params.school)

		// Finds if Group already exist
		def groupName = params.group
		def groupSchool = school
		def groupStage = stage
		def groupPeriod = period
		def groupTypeEnum = SocialGroupType.GROUP
		def existingGroup = SocialGroup.findAll {
			name == groupName && parent == groupSchool && stage == groupStage && period == groupPeriod && groupType == groupTypeEnum
		}
		
		if (!existingGroup) {
				
			def group = new SocialGroup(name: params.group, groupType: SocialGroupType.GROUP, parent: school, stage: stage, period: period)
	
			if (!group.save(flush: true)) {
				render(view: "groupCreate", model: [groupBean: group, action:'group'])
				return
			}
	
			flash.message = message(code: 'default.created.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), group.id])
			redirect(action: "groupList", params: [school: params.school, period: period.id, stage: stage.id, city: params.city, country: params.country])
		
		} else {
			println "GROUP ALREADY EXISTS !!!!"
			
			flash.message = message(code: 'default.duplicated.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), existingGroup.id])
			redirect(action: "groupCreate", params: [school: params.school, period: params.period, stage: params.stage, country: params.country, city: params.city])
			return
		}

	}
	
	def groupEdit() {
		//println "groupEdit: ${params}"

		// Get User signed in
		def user = User.get(springSecurityService.principal.id)

		def group = SocialGroup.get(params.id)
		
		def school = SocialGroup.get(group?.parent.id)

		def city = school?.geo.id
		def country = school?.geo?.parent?.parent.id
		
		def schoolBean = SocialGroup.get(school.id)
		
		def groups = SocialGroup.findAll {
			parent == schoolBean
		}
		
		def stageTree = getSocialGroupTree(groups)
		
		def stageJSON = SocialGroupStage.list().name as JSON
		
		def periodJSON = SocialGroupPeriod.list().name as JSON
		
		[stageTree: stageTree, stageJSON: stageJSON, periodJSON: periodJSON, schoolBean: schoolBean, groupBean: group, school: school.id, stage: group.stage.id, period: group.period.id, city: city, country: country, user:user, action: 'group']

	}
	
	def groupUpdate() {
		//println "groupUpdate: ${params}"
	
		def group = SocialGroup.get(params.groupId)
		if (!group) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), params.groupId])
			//println flash.message
			redirect(action: "groupEdit", params: [groupBean: group, action:'group'])
			return
		}

		if (params.version) {
			def version = params.version.toLong()
			if (group.version > version) {
				group.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [message(code: 'socialGroup.groupType.school.label', default: 'School')] as Object[],
						  "Another user has updated this School while you were editing")
				render(view: "groupEdit", model: [groupBean: group, action:'group'])
				return
			}
		}

		def stage = SocialGroupStage.findByName(params.stage)
		if (!stage) {
			stage = new SocialGroupStage(name: params.stage).save(failOnError: true)
		}
		def period = SocialGroupPeriod.findByName(params.period)
		if (!period) {
			period = new SocialGroupPeriod(name: params.period).save(failOnError: true)
		}
		
		// Finds if Group already exist
		def groupName = params.group
		def groupSchool = SocialGroup.get(params.school)
		def groupStage = stage
		def groupPeriod = period
		def groupTypeEnum = SocialGroupType.GROUP
		def existingGroup = SocialGroup.findAll {
			name == groupName && parent == groupSchool && stage == groupStage && period == groupPeriod && groupType == groupTypeEnum
		}
		
		if (!existingGroup) {
				
			group.name = params.group
			group?.stage = stage
			group?.period = period
			
			if (!group.save(flush: true)) {
				render(view: "groupCreate", model: [groupBean: group, action:'group'])
				return
			}
	
			flash.message = message(code: 'default.created.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), group.id])
			redirect(action: "groupList", params: [school: params.school, period: period.id, stage: stage.id, city: params.city, country: params.country])
		
		} else {
			println "GROUP ALREADY EXISTS !!!!"
			
			flash.message = message(code: 'default.duplicated.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), existingGroup.id])
			redirect(action: "groupEdit", params: [id: params.groupId])
			return
		}

	}
	
	def groupDelete() {
		println "groupDelete: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)

		def group = SocialGroup.get(params.id)
		
		def school = SocialGroup.get(group?.parent.id)

		def city = school?.geo.id
		def country = school?.geo?.parent?.parent.id
		
		def schoolBean = SocialGroup.get(school.id)
		
		def groups = SocialGroup.findAll {
			parent == schoolBean
		}
		
		def stageTree = getSocialGroupTree(groups)
		
		def stageJSON = SocialGroupStage.list().name as JSON
		
		def periodJSON = SocialGroupPeriod.list().name as JSON
		
		[stageTree: stageTree, stageJSON: stageJSON, periodJSON: periodJSON, schoolBean: schoolBean, groupBean: group, school: school.id, stage: group.stage.id, period: group.period.id, city: city, country: country, user:user, action: 'group']

	}
	
	def groupRemove() {
		println "groupRemove: ${params}"

		def group = SocialGroup.get(params.groupId)
		if (!group) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), params.groupId])
			//println flash.message
			redirect(action: "groupDelete", params: [id: params.groupId])
			return
		}
		
		if (params.version) {
			def version = params.version.toLong()
			if (group.version > version) {
				group.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [message(code: 'socialGroup.groupType.school.label', default: 'School')] as Object[],
						  "Another user has updated this School while you were editing")
				render(view: "groupEdit", model: [groupBean: group, action:'group'])
				return
			}
		}

		try {
			group.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), params.groupId])
			redirect(action: "groupList", params: [school: params.school, period: group?.period.id, stage: group?.stage.id, city: params.city, country: params.country])
		}
		catch (Exception e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'socialGroup.groupType.group.label', default: 'Group'), params.groupId])
			redirect(action: "groupDelete", params: [id: params.groupId])
		}
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
