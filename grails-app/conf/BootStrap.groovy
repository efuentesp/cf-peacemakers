import com.peacemakers.security.Role;
import com.peacemakers.security.User;
import com.peacemakers.security.UserRole;

class BootStrap {

    def init = { servletContext ->
		
		// Security: Roles
		def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
		def studentRole = Role.findByAuthority('ROLE_STUDENT') ?: new Role(authority: 'ROLE_STUDENT').save(failOnError: true)
		def adminSchoolRole = Role.findByAuthority('ROLE_ADMIN_SCHOOL') ?: new Role(authority: 'ROLE_ADMIN_SCHOOL').save(failOnError: true)
		
		// Security: Super User
		def user0 = User.findByUsername('admin') ?: new User(username: 'admin', enabled: true, password: 'admin').save(failOnError: true)
		if (!user0.authorities.contains(adminRole)) {
			UserRole.create user0, adminRole, true
		}
		
    }
    def destroy = {
    }
}
