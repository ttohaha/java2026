<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Registration</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="login-container">
    <h2>Регистрация</h2>

    <c:if test="${not empty errorMsg}">
        <div class="error-msg">${errorMsg}</div>
    </c:if>

    <c:if test="${not empty successMsg}">
        <div class="success-msg">${successMsg}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/controller" method="POST">
        <input type="hidden" name="command" value="add_user">

        <input type="text" name="login" placeholder="Придумайте логин" required>
        <input type="password" name="password" placeholder="Придумайте пароль" required>
        <input type="email" name="email" placeholder="Email" required>

        <input type="submit" value="Зарегистрироваться">
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/index.jsp">Назад к логину</a>
    </p>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>