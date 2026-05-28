<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Phone Book — Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="fragments/navbar.jsp" %>

<div class="container">
    <h2>My Profile</h2>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/profile">
        <div class="form-group">
            <label>Login</label>
            <input type="text" value="${user.lastname}" disabled>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email"
                   value="${user.email}" required>
        </div>
        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="tel" id="phone" name="phone"
                   value="${user.phone}">
        </div>
        <button type="submit" class="btn btn-primary">Save Changes</button>
    </form>
</div>
</body>
</html>
