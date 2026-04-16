<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<div class="login-container">
    <h2>Registration</h2>

    <%-- Error message display --%>
    <c:if test="${not empty errorMessage}">
        <div class="error-msg">${errorMessage}</div>
    </c:if>

    <%-- Success message display --%>
    <c:if test="${not empty successMessage}">
        <div class="success-msg">${successMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/controller" method="POST">
        <%-- Command name matches ADD_USER in CommandType Enum --%>
        <input type="hidden" name="command" value="add_user">

        <label for="login">Login:</label>
        <input type="text" id="login" name="login" placeholder="Create login" required>

        <label for="password">Password:</label>
        <input type="password" id="password" name="password" placeholder="Create password" required>

        <label for="email">Email:</label>
        <input type="email" id="email" name="email" placeholder="Enter email" required>

        <input type="submit" value="Sign Up">
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/index.jsp">Back to Login</a>
    </p>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>