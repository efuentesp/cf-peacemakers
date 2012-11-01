import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class LogoutController {
	def logoutHandlers

	/**
	 * Index action. Redirects to the Spring security logout uri.
	 */
	def index = {
		// TODO: put any pre-logout code here
		redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
	}
	
	def student = {
	    // Logout programmatically
	    Authentication auth = SecurityContextHolder.context.authentication
	    if (auth) {
	        logoutHandlers.each  { handler->
	            handler.logout(request,response,auth)
	        }
	    }
	    //redirect uri:params.redirect
		redirect(controller:"student", action: "main", params: params)
	}
}
