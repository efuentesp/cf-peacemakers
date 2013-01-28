package com.peacemakers.controller

import com.peacemakers.domain.GroupMember;
import com.peacemakers.domain.Person;
import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SociometricCriteria;
import com.peacemakers.domain.SociometricCriteriaResponse;
import com.peacemakers.domain.SociometricTest;
import com.peacemakers.domain.SociometricTestResult;
import com.peacemakers.security.User;
import com.sun.j3d.utils.scenegraph.io.state.javax.media.j3d.LinkState;

import grails.converters.JSON;
import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_ADMIN'])
class SociometricTestResultsController {
	def SocialGroupService
	def SociometricTestResultsService
	def springSecurityService
	
	def index() {
		redirect(action: "matrixChart", params: params)
	}
	
	def matrixChart() {
		println "matrixChart: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def selectedSocialGroupId = params.id.toLong()
		
		def socialGroup = SocialGroup.get(selectedSocialGroupId)
		
		// Find all Sociometric Tests assigned to the Social Group
		def sociometricTests = SociometricTest.findAll(sort:"sequence") {
			socialGroup.id == selectedSocialGroupId
		}
		def sociometricTestArray = []
		sociometricTests.each { sociometricTest ->
			sociometricTestArray << sociometricTest
		}
		//println "sociometricTest: ${sociometricTestArray.size()}"
		
		[socialGroup: socialGroup, sociometricTests: sociometricTestArray, user: user, action: params.action]
		
	}

	def matrix() {
		// TODO: Separate Sociometric Tests for Criteria do not mix them
		
		println "matrix: ${params}"
		
		// Find all Group Members from a Social Group
		def socialGroupId = params.id.toLong()
		def groupMembers = GroupMember.findAll {
			socialGroup.id == socialGroupId
		}
		def i = 1
		def groupMemberArray = []
		groupMembers.each { groupMember ->
			groupMemberArray << [id: "${groupMember.id}", seq: "A${i++}", fullname: "${groupMember.getFullName()}"]
		}
		
		// Find all Sociometric Tests assigned to the Social Group
		def sociometricTests = SociometricTest.findAll(sort:"sequence") {
			socialGroup.id == socialGroupId
		}
		def sociometricTestArray = []
		sociometricTests.each { sociometricTest ->
			sociometricTestArray << sociometricTest
		}

		// Find all Sociometric Test Results from a Social Group
		def query = SociometricTestResult.where {
			socialGroup.id == socialGroupId
		}
		def sociometricTestResults = query.list()
		
		// Create a matrix
		def from, to, test
		def sociometricTestResultsArray = new Object[groupMemberArray.size()][groupMemberArray.size()][sociometricTestArray.size()]
		sociometricTestResults.each { result ->
			from = groupMemberArray.findIndexOf {
				it.id.toLong() == result?.fromGroupMember.id.toLong()
			}
			to = groupMemberArray.findIndexOf {
				it.id.toLong() == result?.toGroupMember.id.toLong()
			}
			test = sociometricTestArray.findIndexOf {
				it.id.toLong() == result?.sociometricTest.id.toLong()
			}
			
			sociometricTestResultsArray[to][from][test]=result.sociometricCriteriaResponse.color
		}

		// Generates the response
		def tArray = []
		for (int t=0; t < sociometricTestArray.size(); t++) {
			//println "t=${t}"
			def yArray=[]
			for (int y=0; y < groupMemberArray.size(); y++) {
				//println "   y=${y}"
				def xArray=[]
				for (int x=0; x < groupMemberArray.size(); x++) {
					def result = (sociometricTestResultsArray[y][x][t]) ? sociometricTestResultsArray[y][x][t] : 'tile_default' 
					xArray << [test: result]
					//println "      x=${x} => ${sociometricTestResultsArray[y][x][t]}"
				}
				yArray << xArray
			}
			
			tArray << [name: "${g.message(code: 'sociometricTest.list.header', default:'Test')} ${sociometricTestArray[t].sequence}", tiles: yArray]
		}
		
		//println tArray
		
		def data = [ headers: groupMemberArray, tests: tArray ]
		
		render data as JSON
	}
	
	def barChart() {
		println "barChart: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def socialGroupId = params.id.toLong()
		
		def socialGroup = SocialGroup.get(socialGroupId)
		
		// Find all Sociometric Tests assigned to the Social Group
		def sociometricTests = SociometricTest.findAll(sort: "sociometricCriteria") {
			socialGroup.id == socialGroupId
		}
		
		def sociometricCriteriaArray = []
		sociometricTests.each { test->
			if (!(test.sociometricCriteria in sociometricCriteriaArray)) {
				sociometricCriteriaArray << test.sociometricCriteria
			}
		}
		
		def maxPercentage = 30
		if (params.maxPercentage) {
			maxPercentage = params.maxPercentage.toInteger()
		} else {
			maxPercentage = 30
		}
		
		def testResults = []
		sociometricCriteriaArray.each { criteria->
			println "Sociometric Criteria => ${criteria}"
			def sociometricCriteriaId = criteria.id
			sociometricTests = SociometricTest.findAll {
				sociometricCriteria.id == sociometricCriteriaId && socialGroup.id == socialGroupId
			}
			def testArray = []
			sociometricTests.each { test->
				println "    Sociometric Test => ${test}"
				def socialGroupResults = SociometricTestResultsService.getSummaryByGroupMember(test, socialGroup, maxPercentage.toInteger())
				println "       Sociometric Test Results => ${socialGroupResults.detail}"
				testArray << [test: test, results: socialGroupResults.detail]
			}
			testResults << [criteria: criteria, tests: testArray]
		}
		println ">> Test results : ${testResults}"
		
		[socialGroup: socialGroup, sociometricTestResults: testResults, user: user, maxPercentage: maxPercentage, action: params.action]
	}
	
	def jqPlotBarChart() {
		println "jqPlotbarChart: ${params}"
		
		// Get User signed in
		def user = User.get(springSecurityService.principal.id)
		
		def socialGroupId = params.id.toLong()
		
		def socialGroup = SocialGroup.get(socialGroupId)
		
		// Find all Sociometric Tests assigned to the Social Group
		def sociometricTests = SociometricTest.findAll(sort: "sociometricCriteria") {
			socialGroup.id == socialGroupId
		}
		
		def sociometricCriteriaArray = []
		sociometricTests.each { test->
			if (!(test.sociometricCriteria in sociometricCriteriaArray)) {
				sociometricCriteriaArray << test.sociometricCriteria
			}
		}
		
		[socialGroup: socialGroup, sociometricCriterias: sociometricCriteriaArray, maxPercentage: params.maxPercentage, user: user, action: params.action]
	}
	
	def piejson() {
		println "piejson: ${params}"
		
		def criteriaId = params.criteria.toLong()
		def groupId = params.group.toLong()
		
		def sociometricCriteria = SociometricCriteria.get(criteriaId)
		def socialGroup = SocialGroup.get(groupId)
		
		def sociometricTests = SociometricTest.findAll {
			socialGroup.id == groupId && sociometricCriteria.id == criteriaId 
		}
		
		def criteriaResponses = []
		sociometricCriteria.sociometricCriteriaResponses.each{ criteriaResponse->
			criteriaResponses << [ id: criteriaResponse.id ]
		}
		//println "series: ${series}"

		def maxPercentage = 30
		if (params.maxPercentage) {
			maxPercentage = params.maxPercentage.toInteger()
		} else {
			maxPercentage = 30
		}
				
		def ticks = [],
			matrix = new Object[criteriaResponses.size()][sociometricTests.size()],
			percentage = new Object[criteriaResponses.size()][sociometricTests.size()],
			t = 0
		sociometricTests.each { test ->
			def socialGroupResults = SociometricTestResultsService.getSummaryByGroupMember(test, socialGroup, maxPercentage)
			//println "++++ test: ${test}, socialGroup: ${socialGroup}"
			socialGroupResults.summary.each { result->
				//println result
				def criteriaResponse = criteriaResponses.findIndexOf {
					it.id.toLong() == result.criteriaResponse.id.toLong()
				}
				matrix[criteriaResponse][t] = result.count
				percentage[criteriaResponse][t] = g.formatNumber(number: result.percentage, type: "number", maxFractionDigits: "1") + '%'
				//println "CriteriaResponse index: ${criteriaResponse}"
			}
			//println "Social Group Results: ${socialGroupResults}"
			ticks << g.message(code: 'sociometricTest.list.header', default: 'Test') + ' ' + test.sequence
			t++
		}
		//println "matrix: ${matrix}"
		//println "ticks: ${ticks}"

		def series = [], res=0
		sociometricCriteria.sociometricCriteriaResponses.each{ criteriaResponse->
			series << [ label: g.message(code: criteriaResponse.question, default: criteriaResponse.question),
						pointLabels: [labels: percentage[res]],
						color: criteriaResponse.rgbHex
					  ]
			//println ":: ${percentage[res]}"
			res++
		}
		//println "series: ${series}"
		
				
		def title = [text: sociometricCriteria.name, show: true]
		
		def r = [ 	title: title,
			size: socialGroup.groupMembers.size(),
			data: matrix,
			ticks: ticks,
			series: series
		]
		
		/*
		def r = [ 	title: sociometricCriteria.name,
					size: 5,
					data: [ [4, 3, 5], [3, 6, 2], [5, 2, 3], [4, 3, 4] ],
					ticks: ['1st', '2nd', '3th'],
					series: [
						            [label: 'Victima', color:'#FDD200'],
						            [label: 'Rechazado', color: '#304CE3'],
						            [label: 'Peacemaker', color: '#F77A00'],
						            [label: 'Agresor', color: '#CB002D']
						    ]
				] */
		
		//println ">> Results to Chart: ${r as JSON}"
		
		render r as JSON
	}
	
	def socialGroupDetailResults() {
		println "socialGroupResults: ${params}"
		
		// Find Sociometric Criteria
		def sociometricTestId = params.id.toLong()
		def sociometricTest = SociometricTest.get(sociometricTestId)
		def socialGroup = SocialGroup.get(sociometricTest?.socialGroup.id)
		
		def socialGroupResults = SociometricTestResultsService.getSummaryByGroupMember(sociometricTest, socialGroup)
			
		render socialGroupResults.detail as JSON

	}

	def socialGroupSummaryResults() {
		println "socialGroupResults: ${params}"
		
		// Find Sociometric Criteria
		def sociometricTestId = params.id.toLong()
		def sociometricTest = SociometricTest.get(sociometricTestId)
		def socialGroup = SocialGroup.get(sociometricTest?.socialGroup.id)
		
		def socialGroupResults = SociometricTestResultsService.getSummaryByGroupMember(sociometricTest, socialGroup)
			
		render socialGroupResults.summary as JSON

	}
	
}
