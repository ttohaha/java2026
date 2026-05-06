<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><fmt:message key="profile.title"/> — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="app-page">
<%@ include file="fragments/navbar.jsp" %>
<div class="page-wrapper" style="max-width:600px">
    <div class="page-header"><h2><fmt:message key="profile.heading"/></h2></div>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger"><span class="alert-icon">&#9888;</span><span>${errorMsg}</span></div>
    </c:if>
    <c:if test="${not empty successMsg}">
        <div class="alert alert-success"><span class="alert-icon">&#10003;</span><span>${successMsg}</span></div>
    </c:if>

    <div class="card">
        <form id="profileForm" action="${pageContext.request.contextPath}/controller" method="POST" novalidate>
            <input type="hidden" name="command" value="edit_profile">

            <div class="form-group">
                <label class="form-label" for="lastname"><fmt:message key="profile.field.lastname"/></label>
                <input class="form-control" type="text" id="lastname" name="lastname"
                       value="${user.lastname}" maxlength="100" required>
                <span class="field-error" id="lastnameError"><fmt:message key="profile.field.lastname"/></span>
            </div>

            <div class="form-group">
                <label class="form-label" for="email"><fmt:message key="profile.field.email"/></label>
                <input class="form-control" type="email" id="email" name="email"
                       value="${user.email}" autocomplete="email" required>
                <span class="field-error" id="emailError"><fmt:message key="profile.field.email"/></span>
            </div>

            <div class="form-group">
                <label class="form-label" for="phone"><fmt:message key="profile.field.phone"/></label>
                <input class="form-control" type="tel" id="phone" name="phone"
                       value="${user.phone}" pattern="[0-9+\-\s()]{10,20}">
                <span class="form-hint"><fmt:message key="profile.field.phone.hint"/></span>
            </div>

            <button type="submit" class="btn btn-primary"><fmt:message key="profile.submit"/></button>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
