package com.peacemakers.domain

class SociometricTest {
	
	static belogsTo = [socialGroup: SocialGroup, sociometricCriteria: SociometricCriteria]
	static hasMany = [sociometricTestResults: SociometricTestResult]

	Integer sequence
	SocialGroup socialGroup
	SociometricCriteria sociometricCriteria
	List sociometricTestResults
	Boolean enabled = true
	
    static constraints = {
		sequence (nullable: false)
		socialGroup (nullable: false)
		sociometricCriteria (nullable: false)
		sociometricTestResults(nullable: true)
    }
	
}
