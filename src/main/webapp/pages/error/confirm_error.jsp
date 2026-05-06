<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="../fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="confirm.error.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="auth-page">
<div class="card" style="max-width:440px;text-align:center">
    <div style="font-size:3rem;margin-bottom:16px">&#9888;&#65039;</div>
    <h2 style="margin-bottom:16px"><fmt:message key="confirm.error.heading"/></h2>
    <c:choose>
        <c:when test="${confirmationStatus == 'invalid_token'}">
            <div class="alert alert-danger" style="text-align:left"><fmt:message key="confirm.error.invalid"/></div>
            <a href="${pageContext.request.contextPath}/pages/registration.jsp" class="btn btn-primary">
                <fmt:message key="confirm.error.register"/>
            </a>
        </c:when>
        <c:when test="${confirmationStatus == 'already_confirmed'}">
            <div class="alert alert-warning" style="text-align:left"><fmt:message key="confirm.error.already"/></div>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">
                <fmt:message key="confirm.error.login"/>
            </a>
        </c:when>
        <c:when test="${confirmationStatus == 'expired'}">
            <div class="alert alert-danger" style="text-align:left"><fmt:message key="confirm.error.expired"/></div>
            <a href="${pageContext.request.contextPath}/pages/registration.jsp" class="btn btn-primary">
                <fmt:message key="confirm.error.register"/>
            </a>
        </c:when>
        <c:otherwise>
            <div class="alert alert-danger" style="text-align:left"><fmt:message key="confirm.error.default"/></div>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">
                <fmt:message key="error.back"/>
            </a>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
