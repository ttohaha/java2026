<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Phone Book — Upload File</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="fragments/navbar.jsp" %>

<div class="container">
    <h2>Upload File</h2>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <form method="post"
          action="${pageContext.request.contextPath}/files/upload"
          enctype="multipart/form-data">

        <div class="form-group">
            <label for="file">Choose file</label>
            <input type="file" id="file" name="file" required>
        </div>
        <button type="submit" class="btn btn-primary">Upload</button>
        <a href="${pageContext.request.contextPath}/files" class="btn btn-secondary">Cancel</a>
    </form>
</div>
</body>
</html>
