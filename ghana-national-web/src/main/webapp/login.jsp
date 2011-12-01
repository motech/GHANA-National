<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="org.springframework.security.*" %>

<form name="f" action="/ghana-national-web/j_spring_security_check" method="POST">
    <label for="j_username">Username</label>
    <input type="text" name="j_username" id="j_username"/>
    <br/>
    <label for="j_password">Password</label>
    <input type="password" name="j_password" id="j_password"/>

    <br/>
    <input type="submit" value="Login"/>
    <br/>
    <c:url var="forgotPasswordURL" value="/forgotPassword">
        <c:param name="emailId" value="${j_username}"/>
    </c:url>
    <a href="<c:out value="${forgotPasswordURL}"/>"
       onclick="this.href=this.href+document.getElementById('j_username').value">Forgot Password</a>

    <br/>
    <span>Please enter your email address and then click the above link to receive the password</span>
</form>