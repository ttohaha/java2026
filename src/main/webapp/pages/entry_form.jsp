<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Contact Form</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/forms.css">
</head>
<body>
<div class="login-container">
    <h2>${param.entryId != null ? 'Edit Contact' : 'Add Contact'}</h2>

    <c:if test="${not empty errorMsg}">
        <div class="error-msg">${errorMsg}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/controller" method="POST">
        <input type="hidden" name="command" value="${param.entryId != null ? 'edit_entry' : 'add_entry'}">
        <c:if test="${not empty param.entryId}">
            <input type="hidden" name="entryId" value="${param.entryId}">
        </c:if>

        <label for="contactName">Name:</label>
        <input type="text" id="contactName" name="contactName"
               value="${entry.contactName}"
               required maxlength="100">

        <label for="contactPhone">Phone:</label>
        <input type="tel" id="contactPhone" name="contactPhone"
               value="${entry.contactPhone}"
               required pattern="[0-9+\-\s()]{10,20}" maxlength="20">

        <label for="contactEmail">Email:</label>
        <input type="email" id="contactEmail" name="contactEmail"
               value="${entry.contactEmail}"
               maxlength="100">

        <input type="submit" value="${param.entryId != null ? 'Update' : 'Add'} Contact">
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/controller?command=list_entries">Cancel</a>
    </p>
</div>
</body>
</html>