package com.peacemakers.controller

import grails.plugins.springsecurity.Secured;

import com.peacemakers.domain.GroupMember;
import com.peacemakers.domain.SocialGroup;
import com.peacemakers.security.Role;
import com.peacemakers.security.User;
import com.peacemakers.security.UserRole;

@Secured(['ROLE_ADMIN'])
class UserController {
	def springSecurityService
	
	def exportService
	def grailsApplication  //inject GrailsApplication

    def index() {
		redirect(action: "list", params: params)
	}
	
	def list() {
		println "list: ${params}"
		
		def user = User.get(springSecurityService.principal.id)
		
		def roles = Role.list()
		
		def userRoles, role
		if (params.id) {
			role = Role.get(params.id) 
			//println "Role: ${role}"
			userRoles = UserRole.findAllByRole(role)
			//println "UserRoles: ${userRoles}"
		}
		
		[roles: roles, users: userRoles?.user, selectedRole: role]
	}
	
	def listBySocialGroup() {
		println "listBySocialGroup: ${params}"
		
		def user = User.get(springSecurityService.principal.id)
		
		def socialGroup = SocialGroup.get(params.id)
		
		def groupMembers = GroupMember.findAll(sort:"person") {
			socialGroup.id == params.id.toLong()
		}
		
		if(params?.format && params.format != "html") {
			def group = []
			groupMembers.each { gm ->
				group << [name: gm.getFullName(), user: gm.user.username, password: gm.user.unencode]
			}
			
			def filename = "passwords.${params.extension}"
			//def filename = "${socialGroup.name}.${params.extension}"
			
			response.contentType = grailsApplication.config.grails.mime.types[params.format]
			response.setHeader("Content-disposition", "attachment; filename=${filename}")
			
			List fields = ["name", "user", "password"]
			Map labels = ["name": "Name", "user": "User", "password": "Password"]
			
			exportService.export(params.format, response.outputStream, group, fields, labels, [:], ["csv.encoding":"UTF-8"])
		}
		
		[socialGroupSelected:socialGroup, groupMemberList:groupMembers, user:user]
		
	}
	
	def edit() {
		println "edit: ${params}"
		
		def roles = Role.list()
		
		def user = User.get(params.user)
		
		def groupMember
		if (params.groupMember) {
			groupMember = GroupMember.get(params.groupMember)
		}
		
		[roles: roles, user: user, role: params.role, socialGroup: params.socialGroup, groupMember: groupMember]
	}
	
	def update() {
		println "update: ${params}"
		
		def user = User.get(params.user.toLong())
		if (!user) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'user.label', default: 'User'), params.user])
			redirect(action: "list")
			return
		}

		if (params.version) {
			def version = params.version.toLong()
			if (user.version > version) {
				user.errors.rejectValue("version", "default.optimistic.locking.failure",
						  [message(code: 'geography.geoType.country.label', default: 'Country')] as Object[],
						  "Another user has updated this Country while you were editing")
				render(view: "edit", model: [user: params.user, role: params.role])
				return
			}
		}

		user.enabled = params.userEnabled.toBoolean()
		user.password = params.password
		user.unencode = params.password
		

		if (!user.save(flush: true)) {
			render(view: "edit", model: [user: params.user, role: params.role])
			return
		}

		if (params.socialGroup) {
			flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
			redirect(controller: "user", action: "listBySocialGroup", id: params.socialGroup)
		} else {
			flash.message = message(code: 'default.updated.message', args: [message(code: 'user.label', default: 'User'), user.id])
			redirect(action: "list", id: params.role)
		}
	}
}
