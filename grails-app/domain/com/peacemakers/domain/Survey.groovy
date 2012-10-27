package com.peacemakers.domain

class Survey {
	
	static hasMany = [questions: SurveyQuestion]
	
	String code
	String name

    static constraints = {
		code(unique: true)
		questions(nullable: true)
    }
}
