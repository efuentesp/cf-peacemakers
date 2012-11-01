class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller: "/student")
		"/admin"(controller: '/socialGroup')
		"500"(view:'/error')
	}
}
