<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Confirmation Error</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/errors.css">
</head>
<body>
<div class="login-container">
    <h2>⚠️ Confirmation Error</h2>

    <c:choose>
        <c:when test="${confirmationStatus == 'invalid_token'}">
            <p class="error-msg">Confirmation link is invalid or has already been used.</p>
            <p><a href="${pageContext.request.contextPath}/pages/registration.jsp">Register again</a></p>
        </c:when>
        <c:when test="${confirmationStatus == 'already_confirmed'}">
            <p class="error-msg">This account has already been confirmed.</p>
            <p><a href="${pageContext.request.contextPath}/index.jsp">Proceed to Login</a></p>
        </c:when>
        <c:when test="${confirmationStatus == 'expired'}">
            <p class="error-msg">Confirmation link has expired. Please register again.</p>
            <p><a href="${pageContext.request.contextPath}/pages/registration.jsp">Register again</a></p>
        </c:when>
        <c:otherwise>
            <p class="error-msg">An error occurred during confirmation. Please try again.</p>
            <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>