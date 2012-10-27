package com.peacemakers.domain

class SurveyAnswerChoice {
	
	static belongsTo = [question: SurveyQuestion]
	
	Integer sequence
	String code
	String description
	Integer points = 0
	Long externalId

    static constraints = {
		code(unique: true)
		externalId(nullable: true)
    }
}
