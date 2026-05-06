<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>
        <c:choose>
            <c:when test="${not empty entry.id}"><fmt:message key="entry.title.edit"/></c:when>
            <c:otherwise><fmt:message key="entry.title.add"/></c:otherwise>
        </c:choose> — <fmt:message key="app.title"/>
    </title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="app-page">
<%@ include file="fragments/navbar.jsp" %>
<div class="page-wrapper" style="max-width:600px">
    <div class="page-header">
        <h2>
            <c:choose>
                <c:when test="${not empty entry.id}"><fmt:message key="entry.title.edit"/></c:when>
                <c:otherwise><fmt:message key="entry.title.add"/></c:otherwise>
            </c:choose>
        </h2>
    </div>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger"><span class="alert-icon">&#9888;</span><span>${errorMsg}</span></div>
    </c:if>

    <div class="card">
        <form id="entryForm" action="${pageContext.request.contextPath}/controller" method="POST" novalidate>
            <input type="hidden" name="command" value="${not empty entry.id ? 'edit_entry' : 'add_entry'}">
            <c:if test="${not empty entry.id}">
                <input type="hidden" name="entryId" value="${entry.id}">
            </c:if>

            <div class="form-group">
                <label class="form-label" for="contactName"><fmt:message key="entry.field.name"/></label>
                <input class="form-control" type="text" id="contactName" name="contactName"
                       value="${entry.contactName}" maxlength="100" required>
                <span class="field-error" id="nameError"><fmt:message key="entry.field.name"/></span>
            </div>

            <div class="form-group">
                <label class="form-label" for="contactPhone"><fmt:message key="entry.field.phone"/></label>
                <input class="form-control" type="tel" id="contactPhone" name="contactPhone"
                       value="${entry.contactPhone}" pattern="[0-9+\-\s()]{10,20}" maxlength="20" required>
                <span class="field-error" id="phoneError"><fmt:message key="entry.field.phone"/></span>
            </div>

            <div class="form-group">
                <label class="form-label" for="contactEmail">
                    <fmt:message key="entry.field.email"/>
                    <span style="color:var(--color-text-muted);font-weight:400">
                        (<fmt:message key="entry.field.email.optional"/>)
                    </span>
                </label>
                <input class="form-control" type="email" id="contactEmail" name="contactEmail"
                       value="${entry.contactEmail}" maxlength="100">
                <span class="field-error" id="emailError"><fmt:message key="entry.field.email"/></span>
            </div>

            <div style="display:flex;gap:12px">
                <button type="submit" class="btn btn-primary" style="flex:1">
                    <c:choose>
                        <c:when test="${not empty entry.id}"><fmt:message key="entry.submit.edit"/></c:when>
                        <c:otherwise><fmt:message key="entry.submit.add"/></c:otherwise>
                    </c:choose>
                </button>
                <a href="${pageContext.request.contextPath}/controller?command=list_entries"
                   class="btn btn-outline" style="flex:0 0 auto"><fmt:message key="entry.cancel"/></a>
            </div>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
