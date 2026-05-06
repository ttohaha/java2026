<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="pages/fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><fmt:message key="login.title"/> — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="auth-page">

<div class="auth-brand">
    <div class="logo">&#128222;</div>
    <h1><fmt:message key="app.title"/></h1>
    <p><fmt:message key="login.subtitle"/></p>
</div>

<div class="card">
    <div class="locale-switcher locale-switcher--centered">
        <a href="${pageContext.request.contextPath}/controller?command=change_locale&lang=en"
           class="locale-btn ${lang == 'en' || empty lang ? 'active' : ''}">EN</a>
        <a href="${pageContext.request.contextPath}/controller?command=change_locale&lang=ru"
           class="locale-btn ${lang == 'ru' ? 'active' : ''}">RU</a>
    </div>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger">
            <span class="alert-icon">&#9888;</span>
            <span>${errorMsg}</span>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">
            <span class="alert-icon">&#9888;</span>
            <span>${errorMessage}</span>
        </div>
    </c:if>
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">
            <span class="alert-icon">&#10003;</span>
            <span>${successMessage}</span>
        </div>
    </c:if>

    <form id="loginForm" action="${pageContext.request.contextPath}/controller" method="POST" novalidate>
        <input type="hidden" name="command" value="login">

        <div class="form-group">
            <label class="form-label" for="login"><fmt:message key="login.field.login"/></label>
            <input class="form-control" type="text" id="login" name="login"
                   value="${cookie.rememberedLogin.value}"
                   autocomplete="username" required>
            <span class="field-error" id="loginError"><fmt:message key="login.field.login"/></span>
        </div>

        <div class="form-group">
            <label class="form-label" for="password"><fmt:message key="login.field.password"/></label>
            <input class="form-control" type="password" id="password" name="password"
                   autocomplete="current-password" required>
            <span class="field-error" id="passwordError"><fmt:message key="login.field.password"/></span>
        </div>

        <div class="form-group" style="flex-direction:row;align-items:center;gap:8px;margin-bottom:20px">
            <input type="checkbox" id="rememberMe" name="rememberMe"
                   style="width:16px;height:16px;accent-color:var(--color-primary);cursor:pointer"
            ${not empty cookie.rememberedLogin.value ? 'checked' : ''}>
            <label for="rememberMe" style="font-size:.875rem;color:var(--color-text-muted);cursor:pointer;margin:0">
                <fmt:message key="login.remember.me"/>
            </label>
        </div>

        <button type="submit" class="btn btn-primary"><fmt:message key="login.submit"/></button>
    </form>

    <div class="auth-footer">
        <fmt:message key="login.no.account"/>
        <a href="${pageContext.request.contextPath}/pages/registration.jsp"><fmt:message key="login.signup"/></a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
