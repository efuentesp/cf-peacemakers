package com.peacemakers.controller

import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SociometricTest;
import com.peacemakers.domain.SurveyAssigned;
import com.peacemakers.security.User;
import com.peacemakers.service.SurveyService;

import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_ADMIN', 'ROLE_ADMIN_SCHOOL'])
class SurveyResultsController {
	def SurveyService
	def springSecurityService
	def SociometricTestResultsService
	
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
			def surveyGroupMemberTotal = getSummaryByGroupMember(surveyAssigned, socialGroup)
			surveyArray << [surveyAssigned: surveyAssigned, surveyResults: surveyGroupMemberTotal]
			
			def surveyQuestion = getSummaryByQuestion(surveyAssigned)
			questionArray << [surveyAssigned: surveyAssigned, surveyResults: surveyQuestion]
		}
		//println surveyArray
		//println questionArray
		
		[socialGroup: socialGroup, surveys: surveyArray, questions: questionArray, user: user]
	}
	
	def getSummaryByGroupMember(SurveyAssigned surveyAssigned, SocialGroup socialGroup) {
		
		// Sociometric Test reslts
		def sociometricTestResults = getSociometricTestResults(socialGroup, 30)
		println "SociometricResults: ${sociometricTestResults}"
		
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
			def criteriaArray = []
			sociometricTestResults.each { criteria ->
				//println "Criteria: ${criteria.criteria}"
				def testArray = []
				criteria.tests.each { test ->
					//println "   Test: ${test.test}"
					def resultArray = []
					test.results.each { result ->
						//println "      Result: ${result.groupMember}"
						if (result.groupMember == groupMember) {
							result.results.each { r ->
								//println "         Vote: ${r.criteriaResponse.question}"
								resultArray << [criteriaResponse: r.criteriaResponse, percentage: r.percentage]
							}
						}
					}
					if (resultArray) {
						testArray << [test: test.test, results: resultArray]
					}
				}
				if (testArray) {
					criteriaArray << [criteria: criteria.criteria, tests: testArray]
				}
			}
			println "GroupMember: ${groupMember}, Results: ${criteriaArray}"
			
			def surveyPoints = 0
			surveyAssigned.answers.each { answer->
				if (answer.groupMember == groupMember) {
					surveyPoints += answer.choiceSelected.points
				}
			}
			groupMemberArray << [groupMember: groupMember, points: surveyPoints, percentage: (surveyPoints/totalPoints)*100, sociometricTestResults: criteriaArray]
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
	
	def getSociometricTestResults(SocialGroup socialGroup, int maxPercentage) {
		
		def selectedSocialGroupId = socialGroup.id
		
		// Find all Sociometric Tests assigned to the Social Group
		def sociometricTests = SociometricTest.findAll(sort: "sociometricCriteria") {
			socialGroup.id == selectedSocialGroupId
		}
		
		def sociometricCriteriaArray = []
		sociometricTests.each { test->
			if (!(test.sociometricCriteria in sociometricCriteriaArray)) {
				sociometricCriteriaArray << test.sociometricCriteria
			}
		}
		
		def testResults = []
		sociometricCriteriaArray.each { criteria->
			//println "Sociometric Criteria => ${criteria}"
			def sociometricCriteriaId = criteria.id
			sociometricTests = SociometricTest.findAll {
				sociometricCriteria.id == sociometricCriteriaId && socialGroup.id == selectedSocialGroupId
			}
			def testArray = []
			sociometricTests.each { test->
				//println "    Sociometric Test => ${test}"
				def socialGroupResults = SociometricTestResultsService.getSummaryByGroupMember(test, socialGroup, maxPercentage)
				//println "       Sociometric Test Results => ${socialGroupResults.detail}"
				testArray << [test: test, results: socialGroupResults.detail]
			}
			testResults << [criteria: criteria, tests: testArray]
		}
		//println ">> Test results : ${testResults}"
		return testResults
	}
}
