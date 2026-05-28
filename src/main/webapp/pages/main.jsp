<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Phone Book — Users</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="fragments/navbar.jsp" %>

<div class="container">
    <h2>All Users</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <table class="data-table">
        <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Status</th>
                <th>Role</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}" varStatus="st">
            <tr>
                <td>${st.index + 1}</td>
                <td>${u.lastname}</td>
                <td>${u.email}</td>
                <td>${u.phone}</td>
                <td>
                    <span class="badge ${u.active ? 'badge-success' : 'badge-warning'}">
                        ${u.active ? 'Active' : 'Pending'}
                    </span>
                </td>
                <td>${u.role}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
