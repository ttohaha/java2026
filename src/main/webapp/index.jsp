<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phone Book — Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-box">
        <h2>Sign In</h2>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty logoutMessage}">
            <div class="alert alert-success">${logoutMessage}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/auth/login">
            <div class="form-group">
                <label for="login">Login</label>
                <input type="text" id="login" name="login" required autofocus>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary">Sign In</button>
        </form>

        <p class="auth-link">
            No account?
            <a href="${pageContext.request.contextPath}/auth/register">Register here</a>
        </p>
    </div>
</div>
</body>
</html>
