<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Profile</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/forms.css">
</head>
<body>
<div class="login-container">
    <h2>My Profile</h2>

    <c:if test="${not empty errorMsg}">
        <div class="error-msg">${errorMsg}</div>
    </c:if>
    <c:if test="${not empty successMsg}">
        <div class="success-msg">${successMsg}</div>
    </c:if>
    <!-- Внутри формы в profile.jsp -->
    <form action="${pageContext.request.contextPath}/controller" method="POST">
        <input type="hidden" name="command" value="edit_profile">

        <label for="lastname">Lastname:</label>
        <input type="text" id="lastname" name="lastname"
               value="${user.lastname}" required>

        <label for="phone">Phone:</label>
        <input type="tel" id="phone" name="phone"
               value="${user.phone}" pattern="[0-9+\-\s()]{10,20}">

        <label for="email">Email:</label>
        <input type="email" id="email" name="email"
               value="${user.email}" required>

        <input type="submit" value="Update Profile">
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/controller?command=list_entries">Manage Phone Book</a> |
        <a href="${pageContext.request.contextPath}/controller?command=logout">Sign Out</a>
    </p>
</div>
</body>
</html>