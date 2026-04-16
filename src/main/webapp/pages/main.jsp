<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<div class="container">
    <h2>Welcome, ${sessionScope.login}!</h2>

    <c:if test="${not empty successMsg}">
        <div class="success-msg">${successMsg}</div>
    </c:if>
    <c:if test="${not empty errorMsg}">
        <div class="error-msg">${errorMsg}</div>
    </c:if>

    <h3>Registered Users List</h3>
    <c:choose>
        <c:when test="${not empty userList}">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Lastname</th>
                    <th>Email</th>
                    <th>Phone</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="user" items="${userList}">
                    <tr>
                        <td>${user.lastname}</td>
                        <td>${user.email}</td>
                        <td>${user.phone}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p class="info-msg">No data available</p>
        </c:otherwise>
    </c:choose>

    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/controller?command=edit_profile">My Profile</a>
        <a href="${pageContext.request.contextPath}/controller?command=list_entries">Phone Book</a>
        <a href="${pageContext.request.contextPath}/controller?command=list_files">My Files</a>
        <a href="${pageContext.request.contextPath}/controller?command=logout">Logout</a>
    </div>
</div>
</body>
</html>