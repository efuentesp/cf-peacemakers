package com.peacemakers.service

import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SociometricCriteria;
import com.peacemakers.domain.SociometricTest;
import com.peacemakers.domain.SociometricTestResult;

class SociometricTestResultsService {

    def getSummaryByGroupMember(SociometricTest test, SocialGroup group) {

		def maxPercentage = 30
		
		// Get the Sociometric Criteria from a Sociometric Test
		//def sociometricCriteria = SociometricCriteria.get(test.id)
		def sociometricCriteria = test.sociometricCriteria
		
		// Find all Sociometric Criteria Responses
		def criteriaResponsesArray = []
		sociometricCriteria?.sociometricCriteriaResponses.each { response->
			//criteriaResponsesArray << response.id.toLong()
			criteriaResponsesArray << response
		}
		
		def groupMemberArray = []
		def groupMembersCount = group?.groupMembers.size()	// Total of Group Members in a Social Group
		
		// Count responses by Group Member and compute percentage
		group?.groupMembers.each { groupMember->
			def groupMemberId = groupMember.id.toLong()
			def groupMembersResults = []
			criteriaResponsesArray.each { criteriaResponse->
				//def criteriaResponseId = criteriaResponse.id
				def query = SociometricTestResult.where {
					toGroupMember == groupMember && sociometricCriteriaResponse == criteriaResponse && sociometricTest == test
				}
				def result = query.list()
				def resultsCount = result.size()
				if (resultsCount > 0) {
					def percentage = 100 * ( resultsCount / groupMembersCount )
					if (percentage > maxPercentage) {
						groupMembersResults << [criteriaResponse: criteriaResponse, count: resultsCount, percentage: percentage]
					}
				}
			}
			groupMemberArray << [groupMember: groupMember, results: groupMembersResults]
		}
		
		// Count responses by Criteria Response
		def criteriaResponseResults = [], dataArray = []
		criteriaResponsesArray.each { criteriaResponse ->
			//def criteriaResponseId = criteriaResponse
			def criteriaResponseCount = 0
			groupMemberArray.each { member->
				member.results.each { result ->
					if (result.criteriaResponse == criteriaResponse) {
						criteriaResponseCount++
					}
				}
			}
			def percentage = 100 * ( criteriaResponseCount / groupMembersCount )
			criteriaResponseResults << [criteriaResponse: criteriaResponse, count: criteriaResponseCount, percentage: percentage]
		}
		
		return [detail: groupMemberArray, summary: criteriaResponseResults]
    }
}
