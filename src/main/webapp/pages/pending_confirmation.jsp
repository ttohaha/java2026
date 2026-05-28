<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration Pending</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-box">
        <h2>Check Your Email</h2>
        <p>
            Registration successful! We have sent a confirmation link to your email address.
            Please click the link to activate your account before signing in.
        </p>
        <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Login</a>
    </div>
</div>
</body>
</html>
