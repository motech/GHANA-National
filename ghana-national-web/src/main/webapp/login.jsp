<%@ page import="org.springframework.security.*" %>

<form action="j_spring_security_check">
	<label for="j_username">Username</label>
	<input type="text" name="j_username" id="j_username" />
	<br/>
	<label for="j_password">Password</label>
	<input type="password" name="j_password" id="j_password"/><a href="">Forgot Password</a>
	<br/>
	<input type="submit" value="Login"/>
</form>