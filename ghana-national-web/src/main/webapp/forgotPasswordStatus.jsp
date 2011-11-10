<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import="org.springframework.security.*" %>


<div>
    <b>  Hello, an email has been sent to your account.
        <c:out value="${requestScope.message}"/>
    </b>

</div>
