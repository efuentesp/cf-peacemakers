package com.peacemakers.controller

import org.grails.plugins.imagetools.ImageTool;
import org.springframework.dao.DataIntegrityViolationException;

import com.peacemakers.domain.GenderType;
import com.peacemakers.domain.GroupMember;
import com.peacemakers.domain.Person;
import com.peacemakers.domain.SocialGroup;
import com.peacemakers.domain.SocialGroupType;
import com.peacemakers.security.Role;
import com.peacemakers.security.User;
import com.peacemakers.security.UserRole;

import grails.converters.JSON;
import grails.plugins.springsecurity.Secured;

@Secured(['ROLE_ADMIN'])
class GroupMemberController {
	def springSecurityService
	def SocialGroupService
	def antUtilsService

	static allowedMethods = [save: "POST", update: "POST", show: "POST"]

    def index() {
		redirect(action: "list", params: params)
	}
	
	private def getSocialGroupTree() {
		def socialGroupTree = SocialGroupService.getSocialGroupTree(0)
		return socialGroupTree
	}
	
	def list() {
		println "list: ${params}"
		
		def user = User.get(springSecurityService.principal.id)
		
		def socialGroupId
		if (params.id) {
			socialGroupId = params.id.toLong()
		} else {
			socialGroupId = 0.toLong()
		}
		
		def socialGroup = SocialGroup.get(socialGroupId)
		
		def groupMembers = GroupMember.findAll(sort:"person") {
			socialGroup.id == socialGroupId
		}
		
		[socialGroupSelected:socialGroup, groupMemberList:groupMembers, user:user]
	}
	
	def create() {
		println "create: ${params}"
		def user = User.get(springSecurityService.principal.id)
		
		def socialGroupId
		if (params.id) {
			socialGroupId = params.id.toLong()
		} else {
			socialGroupId = 0.toLong()
		}
		
		def socialGroup = SocialGroup.get(socialGroupId)
		
		[socialGroupSelected:socialGroup, user:user]
	}
	
	def createZip() {
		println "createZip: ${params}"
		
		def user = User.get(springSecurityService.principal.id)
		
		def socialGroupId
		if (params.id) {
			socialGroupId = params.id.toLong()
		} else {
			socialGroupId = 0.toLong()
		}
		
		def socialGroup = SocialGroup.get(socialGroupId)
		if (!socialGroup) {
			socialGroup.errors.each {
				println it
			}
		}
		
		[socialGroupSelected:socialGroup, user:user]
	}
	
	def edit() {
		println "edit: ${params}"
		
		def user = User.get(springSecurityService.principal.id)
		
		def groupMember = GroupMember.get(params.id)
		
		[groupMemberBean:groupMember, user:user]
	}
	
	def delete() {
		println "delete: ${params}"
		
		def user = User.get(springSecurityService.principal.id)
		
		def groupMember = GroupMember.get(params.id)
		
		[groupMemberBean:groupMember, user:user]
	}

	def save() {
		println "save: ${params}"
		
		def photo = request.getFile('photoUpload')
		
		def okcontents = ['image/png', 'image/jpeg', 'image/gif']
		if (! okcontents.contains(photo.getContentType())) {
			flash.message = "Photo must be one of: ${okcontents}"
			render(view:'create')
			return;
		}
		
		def socialGroup = SocialGroup.get(params.socialGroup.toLong())
		if (!socialGroup) {
			socialGroup.errors.each {
				println it
			}
		}
		def gender
		switch (params.gender) {
			case 'M':
				gender = GenderType.MALE
				break
			case 'F':
				gender = GenderType.FEMALE
				break
			default:
				gender = null
		}
		def birthday = (params.birthdate) ? new Date().parse("yyyy-MM-dd", params.birthdate) : null
		def person = new Person(firstName:params.firstName, firstSurname:params.firstSurname, secondSurname:params.secondSurname, gender:gender, birthday:birthday)
		if (!person.save(flush: true)) {
			person.errors.each {
				println it
			}
		}
		
		//def userName = getUserByName(params.firstName, params.firstSurname, params.secondSurname)
		def userName = createUserName(person)
		
		def groupMember = new GroupMember(person:person, user:userName)
		
		def imageTool = new ImageTool()
		imageTool.load(photo.getBytes())
		//imageTool.saveOriginal()
		imageTool.thumbnail(180)
		groupMember.photo = imageTool.getBytes("JPEG")
		
		//groupMember.photo = photo.getBytes()
		//groupMember.photoType = photo.getContentType()
		
		log.info("File uploaded: " + groupMember.photoType)
			
		if (!socialGroup.addToGroupMembers(groupMember).save(flush: true)) {
			socialGroup.errors.each {
				println it
			} 
			render(view: "create", model:[socialGroupSelected:socialGroup, groupMemberBean:groupMember])
			return
		}

		flash.message = message(code: 'default.created.message', args: [message(code: 'groupMember.label', default: 'GroupMember'), groupMember.id])
		redirect(action: "list", id:"${socialGroup.id}")
	}
	
	def saveZip() {
		println "saveZip: ${params}"

		def userDir

		def acceptContentType = ['application/zip', 'application/x-zip-compressed']
		//def studentRole = Role.findByAuthority('ROLE_STUDENT') ?: new Role(authority: 'ROLE_STUDENT').save(failOnError: true)
		
		def uploadedFile = request.getFile('zipFile')
		def socialGroup = SocialGroup.get(params.socialGroup)
		
		println "Content type: ${uploadedFile.contentType}"
		
		if(!uploadedFile.empty && acceptContentType.contains(uploadedFile.contentType)) {
			
			println "Class: ${uploadedFile.class}"
			println "Name: ${uploadedFile.name}"
			println "OriginalFileName: ${uploadedFile.originalFilename}"
			println "Size: ${uploadedFile.size}"
			println "ContentType: ${uploadedFile.contentType}"
			
			def webRootDir = servletContext.getRealPath("/")
			userDir = new File(webRootDir, "/zipFiles/${socialGroup.id}/")
			userDir.mkdirs()
			def uploadedZipFile = new File(userDir, uploadedFile.originalFilename)
			uploadedFile.transferTo(uploadedZipFile)
			println "Uploaded directory: ${uploadedZipFile.toString()}"
			println "User Dir: ${userDir.toString()}"
			
			unzip(uploadedZipFile, userDir)
		
			addBulkGroupMembers(userDir, socialGroup)
		
			userDir.deleteDir()
			
			flash.message = message(code: 'default.created.message', args: [message(code: 'socialGroup.label', default: 'Social Group'), params.socialGroup])
			redirect(action: "list", id: params.socialGroup)
		} else {
			flash.message = message(code: 'groupMember.typeMismatch.file', args: [message(code: 'socialGroup.label', default: 'Social group'), params.socialGroup])
			redirect(action: "createZip", id: params.socialGroup)
		}
		
	}
	
	private def unzip(uploadedZipFile, userDir) {
		try {
			antUtilsService.unzip(uploadedZipFile.toString(), userDir.toString(), true)
		} catch (org.grails.plugins.grailsant.UnzipException e) {
			println e.message
			println e.fileName
		}
	}
	
	private def addBulkGroupMembers(userDir, socialGroup) {
		
		def i=0
		userDir.eachFileMatch(~/.*.(?:csv|txt)/) { file ->
			
			file.splitEachLine(",") { field ->
				
				i++
				
				//println "GroupMember[${field[0]}]:"
				//println "   First Name: ${field[4]}"
				//println "   First Surname: ${field[2]}"
				//println "   Second Surname: ${field[3]}"
				//println "   Gender: ${field[5]}"
				//println "   Birthday: ${field[6]}"
				//def birthday = (field[6]) ? new Date().parse("yyyy-MM-dd", "${field[6]}") : null
				//println "   ->Birthay: ${birthday}"

				def firstName = field[0]
				def firstSurname = field[1]
				def secondSurname = field[2]
				def gender = field[3]
				def birthday = (field[4]) ? new Date().parse("yyyy-MM-dd", "${field[4]}") : null
				def nationalIdNumber = field[5]
				
				println "GroupMember [${i}]:"
				println "   First Name: ${firstName}"
				println "   First Surname: ${firstSurname}"
				println "   Second Surname: ${secondSurname}"
				println "   Gender: ${gender}"
				println "   Birthday: ${birthday}"
								
				switch (gender) {
					case 'M':
						gender = GenderType.MALE
						break
					case 'F':
						gender = GenderType.FEMALE
						break
					default:
						gender = null
				}
				
				def person = new Person(nationalIdNumber: nationalIdNumber, firstName: firstName, firstSurname: firstSurname, secondSurname: secondSurname, birthday:birthday, gender:gender)
				if (!person.save(flush: true)) {
					person.errors.each {
						println it
					}
				}
				
				/*
				userId = field[4].toLowerCase().replaceAll(~/ /, "") + '.' + field[2].toLowerCase().replaceAll(~/ /, "")
				password = field[4].toLowerCase().replaceAll(~/ /, "")
				println "   User: ${userId}"
				userName = User.findByUsername(userId) ?: new User(username: userId, enabled: true, password: password).save(failOnError: true)
				if (!userName.authorities.contains(studentRole)) {
					UserRole.create userName, studentRole, true
				}
				*/
				
				//def userName = getUserByName(firstName, firstSurname, secondSurname)
				def userName = createUserName(person)
				
				def imageTool = new ImageTool()
				imageTool.load("${userDir}/${i}.jpg")
				//imageTool.saveOriginal()
				imageTool.thumbnail(180)
				//groupMember.photo = imageTool.getBytes("JPEG")
				
				//def groupMemberPhoto = new File(userDir, "${i}.jpg")
				def groupMember = new GroupMember(person:person, photo: imageTool.getBytes("JPEG"), user:userName)
				
				socialGroup.addToGroupMembers(groupMember).save(failOnError: true)
				
			}

		}

	}
	
	def update() {
		//println params
		
		def photo, imageTool
		
		if (!request.getFile('photoUpload').empty) {
			
			photo = request.getFile('photoUpload')
			
			imageTool = new ImageTool()
			imageTool.load(photo.getBytes())
			//imageTool.saveOriginal()
			imageTool.thumbnail(180)
			
			def okcontents = ['image/png', 'image/jpeg', 'image/gif']
			if (! okcontents.contains(photo.getContentType())) {
				flash.message = "Photo must be one of: ${okcontents}"
				render(view:'edit')
				return;
			}
		}
		
		def socialGroup = SocialGroup.get(params.socialGroup.toLong())
		if (!socialGroup) {
			socialGroup.errors.each {
				println it
			}
		}
		
		def groupMember = GroupMember.get(params.id)
		def person = Person.get(groupMember?.person.id)

		if (params.version) {
			def version = params.version.toLong()
			if (groupMember.version > version) {
				groupMember.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [message(code: 'groupMember.label', default: 'Group Member')] as Object[],
						  "Another user has updated this Group Member while you were editing")
				render(view: "edit", model: [countryBean: country, action:'country'])
				return
			}
		}
		
		def gender
		switch (params.gender) {
			case 'M':
				gender = GenderType.MALE
				break
			case 'F':
				gender = GenderType.FEMALE
				break
			default:
				gender = null
		}
		
		// TODO: Use date previously selected
		def birthday = (params.birthdate) ? new Date().parse("yyyy-MM-dd", params.birthdate) : null
		
		person.firstName = params.firstName
		person.firstSurname = params.firstSurname
		person.secondSurname = params.secondSurname
		person.gender = gender
		person.birthday = birthday

		if (!person.save(flush: true)) {
			person.errors.each {
				println it
			}
		}

		if (photo) {
			groupMember.photo = imageTool.getBytes("JPEG")
			//groupMember.photo = photo.getBytes()
			//groupMember.photoType = photo.getContentType()
			log.info("File uploaded: " + groupMember.photoType)
		}
		
		if (!groupMember.save(flush: true)) {
			render(view: "edit", model:[socialGroupSelected:socialGroup, groupMemberBean:groupMember])
			return
		}

		flash.message = message(code: 'default.edited.message', args: [message(code: 'groupMember.label', default: 'GroupMember'), groupMember.id])
		redirect(action: "list", id:"${socialGroup.id}")
	}
	
	def remove() {
		def groupMember = GroupMember.get(params.id)
		if (!groupMember) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'groupMember.label', default: 'GroupMember'), params.id])
			redirect(action: "list")
			return
		}
		
		def user = User.get(groupMember.id)

		try {
			groupMember.delete(flush: true)
			// TODO: Delete orphan users
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'groupMember.label', default: 'GroupMember'), params.id])
			redirect(action: "list", id:groupMember?.socialGroup.id)
		}
		catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'groupMember.label', default: 'GroupMember'), params.id])
			redirect(action: "delete", id: params.id)
		}
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
	
	private def getUserByName(String firstName, String firstSurname, String secondSurname) {
		def studentRole = Role.findByAuthority('ROLE_STUDENT') ?: new Role(authority: 'ROLE_STUDENT').save(failOnError: true)
		
		def userId = firstName.toLowerCase().replaceAll(~/ /, "") + '.' + firstSurname.toLowerCase().replaceAll(~/ /, "")
		def password = firstName.toLowerCase().replaceAll(~/ /, "")
		
		println "   User: ${userId}"
		
		// TODO: Encode password
		def userName = User.findByUsername(userId) ?: new User(	username: userId,
																enabled: true,
																password: password)
																.save(failOnError: true)
		if (!userName.authorities.contains(studentRole)) {
			UserRole.create userName, studentRole, true
		}
		
		return userName
	}
	
	private def createUserName(Person person) {
		def studentRole = Role.findByAuthority('ROLE_STUDENT') ?: new Role(authority: 'ROLE_STUDENT').save(failOnError: true)
		
		def firstName = person.firstName.split()
		
		//def userId = person.firstName.toLowerCase().replaceAll(~/ /, "") + person.id
		def userId = firstName[0].toLowerCase().replaceAll(~/ /, "") + person.id
		def password = person.firstSurname.toLowerCase().replaceAll(~/ /, "")
		
		println "   User: ${userId}"
		
		// TODO: Encode password
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
		
	def tree() {
		def socialGroupTree = SocialGroupService.getSocialGroupTree(0)
		
		render socialGroupTree as JSON
	}
	

}
