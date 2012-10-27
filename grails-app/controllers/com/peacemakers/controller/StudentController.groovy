package com.peacemakers.controller

import com.peacemakers.domain.GroupMember;
import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SociometricTest;
import com.peacemakers.domain.SociometricTestResult;
import com.peacemakers.domain.SociometricCriteria;
import com.peacemakers.domain.SociometricCriteriaResponse;
import com.peacemakers.domain.SurveyAssigned;
import com.peacemakers.security.User;

import grails.plugins.springsecurity.Secured;
import grails.plugins.springsecurity.SpringSecurityService;

@Secured(['ROLE_STUDENT'])
class StudentController {
	def springSecurityService
	def SociometricTestGroupMemberService
	
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
		//redirect(action: "list", params: params)
		
		// Get User signed
		def userSigned = User.get(springSecurityService.principal.id)

		// Find User's Group Member
		def userSignedId = userSigned.id
		def userGroupMember = GroupMember.find {
			user.id == userSignedId
		}
		
		def userSocialGroupId = userGroupMember?.socialGroup.id
		
		// Find Sociometric Tests assigned to the Social Group
		def sociometricTests = SociometricTest.findAll(sort: "sequence", order: "asc") {
			socialGroup.id == userSocialGroupId
		}
		
		def sociometricTestsApplied = []
		sociometricTests.each { s->
			sociometricTestsApplied << [sociometricTest: s, applied: SociometricTestGroupMemberService.isSociometricTestTaken(s, userGroupMember)]
		}
		
		// Find Surveys assigned to the Social Group
		def surveysAssigned = SurveyAssigned.findAll {
			socialGroup.id == userSocialGroupId
		}
		
		def surveysApplied = []
		surveysAssigned.each { s->
			surveysApplied << [surveyAssigned: s, applied: SociometricTestGroupMemberService.isSurveyTaken(s, userGroupMember)]
		}
		
		[	
			user: userSigned,
			groupMember: userGroupMember,
			sociometricTestsApplied: sociometricTestsApplied,
			surveysApplied: surveysApplied
		]
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
		
		// Find all Group Members of the same Social Group
		def userSocialGroupId = userGroupMember?.socialGroup.id
		def groupMembers = GroupMember.findAll {
			socialGroup.id == userSocialGroupId
		}
		
		// Find Sociometric Tests assigned to the Social Group
		//def sociometricTests = SociometricTest.findAll(sort:"sequence") {
		//	socialGroup.id == userSocialGroupId
		//}
		
		// Search for the first SociometricTest not taken by the Group Member
		//def sociometricTest
		//for (s in sociometricTests) {
			//println s
		//	if (!SociometricTestGroupMemberService.isSociometricTestTaken(s, userGroupMember)) {
		//		sociometricTest = s
		//		break
		//	}
		//}
		def sociometricTest = SociometricTest.get(params.id)
		
		// Get Sociometric Criteria Responses from Sociometric Criteria not answered by the Group Member 
		def sociometricCriteriaResponse = sociometricTest?.sociometricCriteria?.sociometricCriteriaResponses
		
		[	groupMemberList: groupMembers,
			sociometricCriteriaResponseList: sociometricCriteriaResponse,
			user: userSigned,
			groupMember: userGroupMember,
			sociometricTest: sociometricTest
		]
	}
	
	def vote() {
		//println "vote: ${params}"
		def socialGroup = SocialGroup.get(params.socialGroup)
		def sociometricTest = SociometricTest.get(params.sociometricTest)
		def fromGroupMember = GroupMember.get(params.fromGroupMember)
		def sociometricCriteriaResponse
		def sociometricTestResult
		def toGroupMember
		
		params.vote.each { groupMemberId, vote->
			if (vote) {
				//println "[${groupMemberId}] => ${vote}"
				toGroupMember = GroupMember.get(groupMemberId)
				sociometricCriteriaResponse = SociometricCriteriaResponse.get(vote)

				//sociometricTestResult = new SociometricTestResult(socialGroup: socialGroup, sociometricTest: sociometricTest, testDate: new Date(), fromGroupMember: fromGroupMember, toGroupMember: toGroupMember, sociometricCriteriaResponse: sociometricCriteriaResponse).save(failOnError: true)
				sociometricTest.addToSociometricTestResults(new SociometricTestResult(socialGroup: socialGroup, testDate: new Date(), fromGroupMember: fromGroupMember, toGroupMember: toGroupMember, sociometricCriteriaResponse: sociometricCriteriaResponse)).save(failOnError: true)
				//test1.addToSociometricTestResults(new SociometricTestResult(socialGroup:group1, testDate:new Date(), fromGroupMember:groupMember1, toGroupMember:groupMember2, sociometricCriteriaResponse:criteria1.sociometricCriteriaResponses[0])).save(failOnError: true)
			}
		}
		
		redirect(action: "index")
	}
	
	def renderPhoto() {
		def groupMemberPhoto = GroupMember.get(params.id)
		if (!groupMemberPhoto || !groupMemberPhoto.photo || !groupMemberPhoto.photoType) {
			response.sendError(404)
			return;
		}
		response.setContentType(groupMemberPhoto.photoType)
		response.setContentLength(groupMemberPhoto.photo.size())
		OutputStream out = response.getOutputStream()
		out.write(groupMemberPhoto.photo)
		out.close()
	}
}
