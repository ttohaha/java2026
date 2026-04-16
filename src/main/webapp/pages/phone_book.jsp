<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Phone Book</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<div class="container">
    <h2>My Phone Book</h2>

    <c:if test="${not empty errorMsg}">
        <div class="error-msg">${errorMsg}</div>
    </c:if>
    <c:if test="${not empty successMsg}">
        <div class="success-msg">${successMsg}</div>
    </c:if>

    <p>
        <!-- FIX: All actions go through controller commands for consistent session handling -->
        <a href="${pageContext.request.contextPath}/controller?command=add_entry">Add New Contact</a> |
        <a href="${pageContext.request.contextPath}/controller?command=logout">Sign Out</a>
    </p>

    <c:choose>
        <c:when test="${empty entryList}">
            <p class="info-msg">No contacts yet. <a href="${pageContext.request.contextPath}/controller?command=add_entry">Add one</a>.</p>
        </c:when>
        <c:otherwise>
            <table class="data-table">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Phone</th>
                    <th>Email</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="entry" items="${entryList}">
                    <tr>
                        <td>${entry.contactName}</td>
                        <td>${entry.contactPhone}</td>
                        <td>${entry.contactEmail}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/controller?command=edit_entry&entryId=${entry.id}">Edit</a>
                            |
                            <a href="${pageContext.request.contextPath}/controller?command=delete_entry&entryId=${entry.id}"
                               onclick="return confirm('Delete this contact?');">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <p>
        <a href="${pageContext.request.contextPath}/controller?command=edit_profile">Back to Profile</a>
    </p>
</div>
</body>
</html>