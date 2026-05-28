<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Phone Book — ${not empty entry.id && entry.id != 0 ? 'Edit' : 'Add'} Contact</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="fragments/navbar.jsp" %>

<div class="container">
    <h2>${not empty entry.id && entry.id != 0 ? 'Edit Contact' : 'Add Contact'}</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}${formAction}">
        <c:if test="${not empty entry.id && entry.id != 0}">
            <input type="hidden" name="entryId" value="${entry.id}">
        </c:if>

        <div class="form-group">
            <label for="contactName">Name *</label>
            <input type="text" id="contactName" name="contactName"
                   value="${entry.contactName}" required>
        </div>
        <div class="form-group">
            <label for="contactPhone">Phone *</label>
            <input type="tel" id="contactPhone" name="contactPhone"
                   value="${entry.contactPhone}" required>
        </div>
        <div class="form-group">
            <label for="contactEmail">Email</label>
            <input type="email" id="contactEmail" name="contactEmail"
                   value="${entry.contactEmail}">
        </div>

        <button type="submit" class="btn btn-primary">Save</button>
        <a href="${pageContext.request.contextPath}/phonebook" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</body>
</html>
