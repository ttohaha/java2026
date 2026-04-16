<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Internal Server Error - 500</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="error-container">
    <div class="error-code">500</div>
    <div class="error-message">Internal Server Error</div>
    <p>Something went wrong on our side. Please try again later.</p>

    <%--
      Optional: Show exception message only in development.
      In production, it's better to log this on the server side.
    --%>
    <div class="error-details">
        <strong>Error details:</strong>
        <c:out value="${exception.message != null ? exception.message : 'Unknown technical error'}"/>
    </div>

    <br/>
    <a href="${pageContext.request.contextPath}/index.jsp" class="back-link">Back to Home Page</a>
</div>

</body>
</html>