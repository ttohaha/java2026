<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<%@ taglib prefix="pb"  uri="http://blinov.first/tags" %>
<%@ include file="fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><fmt:message key="files.title"/> — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="app-page">
<%@ include file="fragments/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h2><fmt:message key="files.heading"/></h2>
        <a href="${pageContext.request.contextPath}/controller?command=upload_file" class="btn btn-outline">
            &#8593; <fmt:message key="files.upload"/>
        </a>
    </div>

    <pb:alert type="danger"  message="${errorMsg}"/>
    <pb:alert type="success" message="${successMsg}"/>

    <div class="table-wrapper">
        <c:choose>
            <c:when test="${empty fileList}">
                <div class="empty-state">
                    <div class="empty-icon">&#128193;</div>
                    <p><fmt:message key="files.empty"/></p>
                    <a href="${pageContext.request.contextPath}/controller?command=upload_file" class="btn btn-outline">
                        <fmt:message key="files.upload"/>
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <fmt:message key="files.delete.confirm" var="deleteConfirm"/>
                <fmt:message key="files.download"       var="downloadLabel"/>
                <fmt:message key="files.delete"         var="deleteLabel"/>
                <table class="data-table">
                    <thead><tr>
                        <th><fmt:message key="files.col.preview"/></th>
                        <th><fmt:message key="files.col.name"/></th>
                        <th><fmt:message key="files.col.size"/></th>
                        <th><fmt:message key="files.col.date"/></th>
                        <th><fmt:message key="files.col.actions"/></th>
                    </tr></thead>
                    <tbody>
                    <c:forEach var="file" items="${fileList}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${fn:startsWith(file.contentType, 'image/')}">
                                        <img class="file-thumb"
                                             src="${pageContext.request.contextPath}/controller?command=download_file&fileId=${file.id}"
                                             alt="preview">
                                    </c:when>
                                    <c:otherwise><div class="file-icon">&#128196;</div></c:otherwise>
                                </c:choose>
                            </td>
                            <td><strong>${fn:escapeXml(file.originalFilename)}</strong></td>
                            <td>${file.fileSize / 1024} KB</td>
                            <td>${not empty file.uploadDate ? file.uploadDate.toLocalDate() : '—'}</td>
                            <td>
                                <div class="table-actions">
                                    <pb:actionLink
                                            href="${pageContext.request.contextPath}/controller?command=download_file&fileId=${file.id}"
                                            label="${downloadLabel}"
                                            style="success"/>
                                    <pb:actionLink
                                            href="${pageContext.request.contextPath}/controller?command=delete_file&fileId=${file.id}"
                                            label="${deleteLabel}"
                                            style="danger"
                                            confirm="${deleteConfirm}"/>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
