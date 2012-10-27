package com.peacemakers.domain

class SurveyAssigned {
	
	static hasMany = [answers: SurveyAnswer]
	
	Integer sequence
	SocialGroup socialGroup
	Survey survey
	Boolean enabled = true

    static constraints = {
    }
}
