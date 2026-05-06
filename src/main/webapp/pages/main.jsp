<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="pb"  uri="http://blinov.first/tags" %>
<%@ include file="fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><fmt:message key="main.title"/> — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="app-page">
<%@ include file="fragments/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h2><fmt:message key="main.welcome"><fmt:param value="${sessionScope.login}"/></fmt:message></h2>
    </div>

    <pb:alert type="danger"  message="${errorMsg}"/>
    <pb:alert type="success" message="${successMsg}"/>

    <div class="table-wrapper">
        <c:choose>
            <c:when test="${not empty userList}">
                <fmt:message key="main.status.active"   var="labelActive"/>
                <fmt:message key="main.status.inactive" var="labelInactive"/>
                <table class="data-table">
                    <thead><tr>
                        <th><fmt:message key="main.col.lastname"/></th>
                        <th><fmt:message key="main.col.email"/></th>
                        <th><fmt:message key="main.col.phone"/></th>
                        <th><fmt:message key="main.col.status"/></th>
                    </tr></thead>
                    <tbody>
                    <c:forEach var="u" items="${userList}">
                        <tr>
                            <td><strong>${u.lastname}</strong></td>
                            <td>${u.email}</td>
                            <td>${u.phone}</td>
                            <td>
                                <pb:badge
                                        status="${u.active ? 'active' : 'inactive'}"
                                        labelActive="${labelActive}"
                                        labelInactive="${labelInactive}"/>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-icon">&#128101;</div>
                    <p><fmt:message key="main.nodata"/></p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
