<%@ page isErrorPage="true" contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="../fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="error.403.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
<div class="error-page">
    <div class="error-card">
        <div class="error-code">403</div>
        <div class="error-title"><fmt:message key="error.403.heading"/></div>
        <p class="error-desc"><fmt:message key="error.403.text"/></p>
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">
            &#8592; <fmt:message key="error.back"/>
        </a>
    </div>
</div>
</body>
</html>
