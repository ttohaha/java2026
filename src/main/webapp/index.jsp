<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="login-container">
    <h2>Login Form</h2>

    <c:if test="${not empty errorMsg}">
        <div class="error-msg">${errorMsg}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/controller" method="POST">
        <input type="hidden" name="command" value="login" />

        <label for="login">Login:</label>
        <input type="text" id="login" name="login" required />

        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required />

        <input type="submit" value="Submit" />
    </form>

    <p>
        Нет аккаунта? <a href="pages/registration.jsp">Зарегистрироваться</a>
    </p>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>