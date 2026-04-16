<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Page Not Found - 404</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="error-container">
    <div class="error-code">404</div>
    <div class="error-message">Oops! Page Not Found</div>
    <p>The page you are looking for might have been removed or is temporarily unavailable.</p>
    <a href="${pageContext.request.contextPath}/index.jsp" class="back-link">Return to Home Page</a>
</div>

</body>
</html>