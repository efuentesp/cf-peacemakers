<head>
	<sec:ifAllGranted roles="ROLE_ADMIN">
		<meta name="layout" content="main">
	</sec:ifAllGranted>
	<sec:ifAllGranted roles="ROLE_ADMIN_SCHOOL">
		<meta name="layout" content="schoolAdmin">
	</sec:ifAllGranted>
	<sec:ifAllGranted roles="ROLE_STUDENT">
		<meta name="layout" content="simple">
	</sec:ifAllGranted>
	<title><g:message code="springSecurity.denied.title" /></title>
</head>

<body>
	<div class='body'>
		<div class='errors'><g:message code="springSecurity.denied.message" /></div>
	</div>
</body>
