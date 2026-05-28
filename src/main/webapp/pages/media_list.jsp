<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Phone Book — My Files</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="fragments/navbar.jsp" %>

<div class="container">
    <h2>My Files</h2>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <a href="${pageContext.request.contextPath}/files/upload"
       class="btn btn-primary" style="margin-bottom:16px;">Upload File</a>

    <c:choose>
        <c:when test="${empty files}">
            <p>No files uploaded yet.</p>
        </c:when>
        <c:otherwise>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Original Name</th>
                        <th>Type</th>
                        <th>Size</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="f" items="${files}" varStatus="st">
                    <tr>
                        <td>${st.index + 1}</td>
                        <td>${f.originalFilename}</td>
                        <td>${f.contentType}</td>
                        <td>${f.fileSize} B</td>
                        <td class="actions">
                            <a href="${pageContext.request.contextPath}/files/download?fileId=${f.id}"
                               class="btn btn-sm btn-edit">Download</a>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/files/delete"
                                  style="display:inline;"
                                  onsubmit="return confirm('Delete this file?');">
                                <input type="hidden" name="fileId" value="${f.id}">
                                <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
