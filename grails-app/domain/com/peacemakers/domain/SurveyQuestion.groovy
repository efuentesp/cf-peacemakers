package com.peacemakers.domain

private enum QuestionType {
	MULTI_CHOICE ('MULTI_CHOICE'),
	MULTIPLE_CORRECT ('MULTIPLE_CORRECT'),
	FILL_IN ('FILL_IN')
	
	//final static String id
	String name
	
	QuestionType(String name) {
		this.name = name
	}
}

class SurveyQuestion {
	
	static belongsTo = [survey: Survey]
	static hasMany = [choices: SurveyAnswerChoice]
	
	String code
	Integer sequence
	String description
	QuestionType type
	Long externalId

    static constraints = {
		code(unique: true)
		externalId(nullable: true)
    }
	
	static mapping = {
		sort "sequence"
	}
}
