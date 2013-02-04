class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller: "/home")
		"/admin"(controller: '/socialGroup')
		"/school"(controller: '/schoolAdmin')
		"500"(view:'/error')
	}
}
