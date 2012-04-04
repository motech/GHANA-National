<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ page import="org.springframework.security.*" %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=8"/>

    <title><tiles:insertAttribute name="title" ignore="true" defaultValue="Ghana National - Login"/></title>
    <spring:url value="/resources/js/lib/jquery-1.6.3.min.js" var="jquery_url"/>
    <spring:url value="/resources/js/lib/formly.min.js" var="formly_url"/>
    <spring:url value="/resources/js/lib/modernizr.custom.js" var="modernizr_url"/>
    <spring:url value="/resources/css/application.css" var="css_url"/>
    <spring:url value="/resources/css/lib/formly.min.css" var="formly_css_url"/>

    <link rel="stylesheet" type="text/css" media="screen" href="${formly_css_url}"></link>
    <link rel="stylesheet" type="text/css" media="screen" href="${css_url}"></link>
    <script src="${jquery_url}" type="text/javascript"><!-- required for FF3 and Opera --></script>
    <script src="${formly_url}" type="text/javascript"><!-- required for FF3 and Opera --></script>
    <script src="${modernizr_url}" type="text/javascript"><!-- required for FF3 and Opera --></script>
    <script type="text/javascript">
        $(document).ready(function() {
           $("form").formly({'onBlur':false, 'theme':'Light'});
        });
        </script>
</head>
<body>
<div id="page-wrap">
<jsp:include page="WEB-INF/views/header.jspx" />

<div id="menu"><nav><ul></ul></nav></div>

<h1>Login:</h1>
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
    <br/>
    <br/>
    <a href="<c:out value="${forgotPasswordURL}"/>"
       onclick="this.href=this.href+document.getElementById('j_username').value">Forgot Password</a>

    <br/>
    <span>Please enter your email address and then click the above link to receive the password</span>
</form>
    </div>
</body>
