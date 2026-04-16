<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>My Files</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/tables.css">
</head>
<body>
<div class="container">
  <h2>My Files</h2>
  <c:if test="${not empty errorMsg}"><div class="error-msg">${errorMsg}</div></c:if>
  <c:if test="${not empty successMsg}"><div class="success-msg">${successMsg}</div></c:if>

  <p>
    <a href="${pageContext.request.contextPath}/controller?command=upload_file">Upload New File</a> |
    <a href="${pageContext.request.contextPath}/controller?command=logout">Sign Out</a>
  </p>

  <c:choose>
    <c:when test="${empty fileList}">
      <p class="info-msg">No files uploaded yet.</p>
    </c:when>
    <c:otherwise>
      <table class="data-table">
        <thead>
        <tr>
          <th>Preview</th>
          <th>Original Name</th>
          <th>Size</th>
          <th>Uploaded</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="file" items="${fileList}">
          <tr>
            <td>
              <c:choose>
                <c:when test="${fn:startsWith(file.contentType, 'image/')}">
                  <img src="${pageContext.request.contextPath}/controller?command=download_file&fileId=${file.id}"
                       width="50" height="50" style="object-fit: cover;" alt="preview">
                </c:when>
                <c:otherwise>
                  📄
                </c:otherwise>
              </c:choose>
            </td>
            <td>${fn:escapeXml(file.originalFilename)}</td>
            <td>${file.fileSize / 1024} KB</td>
            <td>
              <c:choose>
                <c:when test="${not empty file.uploadDate}">
                  ${file.uploadDate.toLocalDate()}
                </c:when>
                <c:otherwise>-</c:otherwise>
              </c:choose>
            </td>
            <td>
              <a href="${pageContext.request.contextPath}/controller?command=download_file&fileId=${file.id}">Download</a> |
              <a href="${pageContext.request.contextPath}/controller?command=delete_file&fileId=${file.id}"
                 onclick="return confirm('Delete this file?');">Delete</a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
  <p><a href="${pageContext.request.contextPath}/controller?command=edit_profile">Back to Profile</a></p>
</div>
</body>
</html>