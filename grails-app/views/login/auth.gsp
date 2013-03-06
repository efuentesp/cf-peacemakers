<html>
	<head>
		<meta name="layout" content="login">
		<title><g:message code="springSecurity.login.title"/></title>
	</head>
	
	<body>
	
		<div class="span3 offset2">
			<br><br><br><br><br>
			<img src="${resource(dir: 'images', file: 'logo_program.png')}" alt="The Peacemaker Program"/>
		</div>
		
		<div id='login' class="span4">
			<!-- Page Header -->
			<div class="page-header">
				<h1><g:message code="springSecurity.login.header" default="Login" /></h1>
			</div> <!-- page-header -->

			<g:if test='${flash.message}'>
				<div class="alert alert-error">
					<a class="close" data-dismiss="alert" href="#">×</a>${flash.message}
				</div>
			</g:if>
	
			<form action='${postUrl}' method='POST' id='loginForm' class='well form-vertical' autocomplete='off'>
				<fieldset>
					<div class="control-group">
						<label class="control-label" for='username'><g:message code="springSecurity.login.username.label"/>:</label>
						<div class="controls">
							<div class="input-prepend">
								<span class="add-on">
									<i class="icon-user"></i>
								</span>
								<input type="text" name="j_username" value="" required="" id="username" class="input-large">
								<!-- <input type="text" name="j_username" value="" required="" id="username" class="input-medium">  -->
							</div>
						</div>
					</div>
		
					<div class="control-group">
						<label class="control-label" for='password'><g:message code="springSecurity.login.password.label"/>:</label>
						<div class="controls">
							<div class="input-prepend">
								<span class="add-on">
									<i class="icon-lock"></i>
								</span>
								<input type="password" name="j_password" value="" required="" id="password" class="input-large">
							</div>
						</div>
					</div>
		
					<br>
					
					<!--  
					<p id="remember_me_holder">
						<input type='checkbox' class='chk' name='${rememberMeParameter}' id='remember_me' <g:if test='${hasCookie}'>checked='checked'</g:if>/>
						<label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>
					</p>
					-->
		
					<div class="modal-footer">
						<button type="submit" class="btn btn-large btn-primary">
							<g:message code="springSecurity.login.button" default="Login"/>
							<i class="icon-circle-arrow-right icon-white"></i>
						</button>
					</div>
				</fieldset>
			</form>

		</div> <!-- login -->
		
		<script type='text/javascript'>
			<!--
			(function() {
				document.forms['loginForm'].elements['j_username'].focus();
			})();
			// -->
		</script>

	</body>
</html>
