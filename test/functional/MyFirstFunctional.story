/* ExampleStory.groovy */
import com.thoughtworks.selenium.*
import java.util.regex.Pattern

before "start selenium", {
	given "selenium is up and running", {
		selenium = new DefaultSelenium("localhost",
			4444, "*firefox", "http://localhost:8080/")
		selenium.start()
	}
}

scenario "login admin", {
	given "a valid admin user"
	when "login with a user and password", {
		selenium.open("/cf-peacemakers/admin/")
		selenium.type("j_username", "admin")
		selenium.type("j_password", "admin")
	}
	and "the submit link has been clicked", {
		selenium.click("submit")
	}
	then "a new example shuld be added with the data provided", {
		selenium.waitForPageToLoad("5000")
		selenium.isTextPresent("BUSCAR POR ESCUELA").shouldBe true
	}
}

after "stop selenium", {
	then "selenium should be shutdown", {
		selenium.stop()
	}
}