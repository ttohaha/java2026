<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration Confirmed</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
<div class="login-container">
    <h2>✅ Registration Confirmed!</h2>
    <p class="success-msg">
        Your account has been successfully activated. You can now log in.
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/index.jsp">Proceed to Login</a>
    </p>
</div>
</body>
</html>