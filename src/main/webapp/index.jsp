<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="login-container">
    <h2>Login Form</h2>

    <%-- Replaced 'errorMsg' with constant-aligned 'errorMessage' if you use it in AttributeName --%>
    <c:if test="${not empty errorMessage}">
        <div class="error-msg">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/controller" method="POST">
        <%-- Use the command name exactly as defined in your CommandType Enum (login -> LOGIN) --%>
        <input type="hidden" name="command" value="login" />

        <label for="login">Login:</label>
        <input type="text" id="login" name="login" required />

        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required />

        <input type="submit" value="Submit" />
    </form>

    <p>
        Don't have an account?
        <a href="${pageContext.request.contextPath}/pages/registration.jsp">Sign up</a>
    </p>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>