<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Upload File</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/forms.css">
</head>
<body>
<div class="login-container">
    <h2>Upload File</h2>
    <c:if test="${not empty errorMsg}"><div class="error-msg">${errorMsg}</div></c:if>
    <c:if test="${not empty successMsg}"><div class="success-msg">${successMsg}</div></c:if>

    <form action="${pageContext.request.contextPath}/controller" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="command" value="upload_file">
        <label for="file">Select File:</label>
        <input type="file" id="file" name="file" required>
        <p class="hint">Allowed: JPG, PNG, GIF, PDF, TXT, DOC, DOCX. Max size: 10 MB.</p>
        <input type="submit" value="Upload">
    </form>
    <p><a href="${pageContext.request.contextPath}/controller?command=list_files">Back to Files</a></p>
</div>
</body>
</html>