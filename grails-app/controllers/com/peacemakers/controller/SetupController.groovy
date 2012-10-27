package com.peacemakers.controller

import org.grails.plugins.imagetools.ImageTool;

import com.peacemakers.domain.Address;
import com.peacemakers.domain.GeoType;
import com.peacemakers.domain.Geography;
import com.peacemakers.domain.GroupMember;
import com.peacemakers.domain.Person;
import com.peacemakers.domain.QuestionType;
import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SocialGroupCategory;
import com.peacemakers.domain.SocialGroupPeriod;
import com.peacemakers.domain.SocialGroupStage;
import com.peacemakers.domain.SocialGroupType;
import com.peacemakers.domain.SociometricCriteria;
import com.peacemakers.domain.SociometricCriteriaResponse;
import com.peacemakers.domain.SociometricTest;
import com.peacemakers.domain.SociometricTestResult;
import com.peacemakers.domain.Survey;
import com.peacemakers.domain.SurveyAnswerChoice;
import com.peacemakers.domain.SurveyAssigned;
import com.peacemakers.domain.SurveyQuestion;
import com.peacemakers.security.Role;
import com.peacemakers.security.User;
import com.peacemakers.security.UserRole;

import grails.converters.JSON;
import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_ADMIN'])
class SetupController {

	def index() {
		println "index: ${params}"
	}
	
    def geography() {
		println "geography: ${params}"
		
		def file = request.getFile('jsonGeoUpload')
		//println file.inputStream.text
		
		def okcontents = ['application/json']
		
		if (! okcontents.contains(file.getContentType())) {
			flash.message = "File must be one of: ${okcontents}"
			render(view:'index')
			return;
		}
		
		def json = JSON.parse(file.getInputStream(),"UTF-8")
		
		println json
		
		// Geo: Countries
		json.countries.each { country->
			new Geography(isoCode: country.isoCode, name: country.name, geoType: GeoType.COUNTRY).save(failOnError: true)
		}
		
		// Geo: States
		json.states.each { state->
			def country = Geography.findByIsoCode(state.parent)
			new Geography(isoCode: state.isoCode, name: state.name, abbreviation: state.abbreviation, geoType: GeoType.STATE, parent: country).save(failOnError: true)
		}
		
		// Geo: Cities
		json.cities.each { city->
			def state = Geography.findByIsoCode(city.parent)
			new Geography(isoCode: city.isoCode, name: city.name, geoType: GeoType.CITY, parent: state).save(failOnError: true)
		}
		
		[json: json]
	}
	
	def sociometricCriteria() {
		println "sociometricCriteria: ${params}"
		
		def file = request.getFile('jsonSociometricCriteriaUpload')
		//println file.inputStream.text
		
		def okcontents = ['application/json']
		
		if (! okcontents.contains(file.getContentType())) {
			flash.message = "File must be one of: ${okcontents}"
			render(view:'index')
			return;
		}
		
		def json = JSON.parse(file.getInputStream(),"UTF-8")
		
		println json
		
		json.sociometricCriterias.each { criteria->
			def c = new SociometricCriteria(code: criteria.code, name: criteria.name, description: criteria.description)
			
			criteria.responses.each { response ->
				def r = new SociometricCriteriaResponse(sequence: response.sequence, question: response.question, color: response.color, rgbHex: response.rgbHex)
				c.addToSociometricCriteriaResponses(r)
			}

			c.save(failOnError: true)
		}
		
		[json: json]
	}
	
	def survey() {
		println "survey: ${params}"
		
		def file = request.getFile('jsonSurveyUpload')
		//println file.inputStream.text
		
		def okcontents = ['application/json']
		
		if (! okcontents.contains(file.getContentType())) {
			flash.message = "File must be one of: ${okcontents}"
			render(view:'index')
			return;
		}
		
		def json = JSON.parse(file.getInputStream(),"UTF-8")
		
		println json

		json.surveys.each { survey->
			def surveyBean = new Survey(code: survey.code, name: survey.name).save(failOnError: true)
			
			survey.questions.each { question->
				//println question
				
				def q = new SurveyQuestion(sequence: question.sequence, description: question.description, code: question.code, type: QuestionType.MULTI_CHOICE, externalId: question.id)
				
				question.answerChoices.each { answer->
					q.addToChoices(new SurveyAnswerChoice(sequence: answer.sequence, code: answer.code, description: answer.description, points: answer.points, externalId: answer.id))
				}
				
				surveyBean.addToQuestions(q).save(failOnError: true)
			}
		}
		
		[json: json]
	}
	
	def socialGroup() {
		println "socialGroup: ${params}"
		
		def file = request.getFile('jsonSocialGroupUpload')
		//println file.inputStream.text
		
		def okcontents = ['application/json']
		
		if (! okcontents.contains(file.getContentType())) {
			flash.message = "File must be one of: ${okcontents}"
			render(view:'index')
			return;
		}
		
		def json = JSON.parse(file.getInputStream(),"UTF-8")
		
		println json

		// Social Group Stages
		json.socialGroupStages.each { socialGroupStage->
			new SocialGroupStage(name: socialGroupStage.name).save(failOnError: true)
		}

		// Social Group Periods
		json.socialGroupPeriods.each { socialGroupPeriod->
			new SocialGroupPeriod(name: socialGroupPeriod.name).save(failOnError: true)
		}
		
		[json: json]
	}
	
	def school() {
		println "school: ${params}"
		
		def file = request.getFile('jsonSchoolUpload')
		//println file.inputStream.text
		
		def okcontents = ['application/json']
		
		if (! okcontents.contains(file.getContentType())) {
			flash.message = "File must be one of: ${okcontents}"
			render(view:'index')
			return;
		}
		
		def json = JSON.parse(file.getInputStream(),"UTF-8")
		
		println json

		// Social Groups: Schools 
		json.schools.each { school->
			def groupCategory
			switch (school.groupCategory) {
				case 'PUBLIC':
					groupCategory = SocialGroupCategory.PUBLIC
					break
				case 'PRIVATE':
					groupCategory = SocialGroupCategory.PRIVATE
					break
				default:
					groupCategory = null
			}
			def city = Geography.findByIsoCode(school.geo)
			def address = new Address(street: school.address.street)
			def schoolBean = new SocialGroup(name: school.name, groupType: SocialGroupType.SCHOOL, groupCategory: groupCategory, geo: city, address: address).save(failOnError: true)
			
			// Social Groups: Groups
			school.groups.each { group->
				//println "Group: ${group}"
				def stage = SocialGroupStage.findByName(group.stage)
				def period = SocialGroupPeriod.findByName(group.period)
				def socialGroup = new SocialGroup(name: group.name, groupType: SocialGroupType.GROUP, parent: schoolBean, stage: stage, period: period).save(failOnError: true)
				
				// Group Members
				group.groupMembers.each { member->
					def person = new Person(firstName: member.firstName, firstSurname: member.firstSurname, secondSurname: member.secondSurname).save(failOnError: true)
					def imageTool = new ImageTool()
					imageTool.load("/Users/Edgar/Documents/workspace-ggts-3.0.0.M2/Grails/PeaceMakerProgram/web-app/zipFiles/bootstrap/${group.id}/${member.photo}")
					imageTool.thumbnail(180)
					def photo = imageTool.getBytes("JPEG")
					def userName = createUserName(person)
					def groupMember = new GroupMember(person: person, photo: photo, user: userName, externalId: member.id)
					//println "Group Member: ${groupMember.externalId.toString()}"
					socialGroup.addToGroupMembers(groupMember).save(failOnError: true)
				}
				
				// Surveys
				group.surveysApplied.each { surveyApplied->
					def survey = Survey.findByCode(surveyApplied.code)
					def surveyAppliedBean = new SurveyAssigned(sequence: surveyApplied.sequence, socialGroup: socialGroup, survey: survey).save(failOnError: true)
					surveyApplied.surveyAnswers.each { answer->
						println answer
						def member = socialGroup.groupMembers.find { m->
							m.externalId.toString() == answer.groupMember.toString()
						}
						def question = survey.questions.find { q->
							q.externalId.toString() == answer.question.toString()
						}
						def choiceSelected = question.choices.find { ch->
							ch.externalId.toString() == answer.answer.toString()
						}
						surveyAppliedBean.addToAnswers(groupMember: member, question: question, choiceSelected: choiceSelected, dateAnswered: new Date()).save(failOnError: true)
					}
				}
				
				// Sociometric Tests
				group.sociometricTests.each { sociometricTest->
					def criteria = SociometricCriteria.findByCode(sociometricTest.sociometricCriteria)
					def test = new SociometricTest(sequence: sociometricTest.sequence, socialGroup: socialGroup, sociometricCriteria: criteria)
						.save(failOnError: true)
					
					// Sociometric Test Results
					sociometricTest.sociometricTestResults.each { result->
						//println "SociometricTestResult: ${result}"
						//def from = GroupMember.findByExternalId(result.fromGroupMember.toLong())
						def from = socialGroup.groupMembers.find { member->
							member.externalId.toString() == result.fromGroupMember.toString()
						}
						//println "   fromGroupMember: [${result.fromGroupMember.toString()}] ${from}"
						//def to = GroupMember.findByExternalId(result.toGroupMember.toLong())
						def to = socialGroup.groupMembers.find { member->
							member.externalId.toString() == result.toGroupMember.toString()
						}
						//println "   toGroupMember: [${result.toGroupMember.toString()}] ${to}"
						def criteriaResponse = SociometricCriteriaResponse.findByQuestion(result.sociometricCriteriaResponse)
						println "   SociometricCriteriaResponse: ${criteriaResponse}"
						test.addToSociometricTestResults(new SociometricTestResult(socialGroup: socialGroup, testDate: new Date(), fromGroupMember: from, toGroupMember: to, sociometricCriteriaResponse: criteriaResponse)).save(failOnError: true)
					}
				}
			}
		}

		[json: json]
	}
	
	private def createUserName(Person person) {
		def studentRole = Role.findByAuthority('ROLE_STUDENT') ?: new Role(authority: 'ROLE_STUDENT').save(failOnError: true)
		
		def firstName = person.firstName.split()
		
		//def userId = person.firstName.toLowerCase().replaceAll(~/ /, "") + person.id
		def userId = firstName[0].toLowerCase().replaceAll(~/ /, "") + person.id
		def password = person.firstSurname.toLowerCase().replaceAll(~/ /, "")
		
		println "   User: ${userId}"
		
		def userName = User.findByUsername(userId) ?: new User(	username: userId,
																enabled: true,
																password: password,
																unencode: password)
																.save(failOnError: true)
		if (!userName.authorities.contains(studentRole)) {
			UserRole.create userName, studentRole, true
		}
		
		return userName
	}
}
