<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><fmt:message key="register.title"/> — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="auth-page">

<div class="auth-brand">
    <div class="logo">&#128222;</div>
    <h1><fmt:message key="register.heading"/></h1>
    <p><fmt:message key="register.subtitle"/></p>
</div>

<div class="card">
    <div class="locale-switcher locale-switcher--centered">
        <a href="${pageContext.request.contextPath}/controller?command=change_locale&lang=en"
           class="locale-btn ${lang == 'en' || empty lang ? 'active' : ''}">EN</a>
        <a href="${pageContext.request.contextPath}/controller?command=change_locale&lang=ru"
           class="locale-btn ${lang == 'ru' ? 'active' : ''}">RU</a>
    </div>

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

    <form id="registrationForm" action="${pageContext.request.contextPath}/controller" method="POST" novalidate>
        <input type="hidden" name="command" value="add_user">

        <div class="form-group">
            <label class="form-label" for="login"><fmt:message key="register.field.login"/></label>
            <input class="form-control" type="text" id="login" name="login"
                   placeholder="<fmt:message key='register.field.login.hint'/>"
                   autocomplete="username" minlength="3" maxlength="20" required>
            <span class="field-error" id="loginError"><fmt:message key="register.field.login.hint"/></span>
        </div>

        <div class="form-group">
            <label class="form-label" for="email"><fmt:message key="register.field.email"/></label>
            <input class="form-control" type="email" id="email" name="email"
                   placeholder="your@email.com" autocomplete="email" required>
            <span class="field-error" id="emailError">your@email.com</span>
        </div>

        <div class="form-group">
            <label class="form-label" for="password"><fmt:message key="register.field.password"/></label>
            <input class="form-control" type="password" id="password" name="password"
                   placeholder="<fmt:message key='register.field.password.hint'/>"
                   autocomplete="new-password" minlength="6" required>
            <div class="password-strength" id="strengthBars">
                <div class="strength-bar" id="bar1"></div>
                <div class="strength-bar" id="bar2"></div>
                <div class="strength-bar" id="bar3"></div>
            </div>
            <span class="field-error" id="passwordError"><fmt:message key="register.field.password.hint"/></span>
        </div>

        <button type="submit" class="btn btn-primary"><fmt:message key="register.submit"/></button>
    </form>

    <div class="auth-footer">
        <fmt:message key="register.have.account"/>
        <a href="${pageContext.request.contextPath}/index.jsp"><fmt:message key="register.signin"/></a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
