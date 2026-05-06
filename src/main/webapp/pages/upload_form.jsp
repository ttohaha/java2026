<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><fmt:message key="upload.title"/> — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="app-page">
<%@ include file="fragments/navbar.jsp" %>
<div class="page-wrapper" style="max-width:600px">
    <div class="page-header"><h2><fmt:message key="upload.heading"/></h2></div>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger"><span class="alert-icon">&#9888;</span><span>${errorMsg}</span></div>
    </c:if>
    <c:if test="${not empty successMsg}">
        <div class="alert alert-success"><span class="alert-icon">&#10003;</span><span>${successMsg}</span></div>
    </c:if>

    <div class="card">
        <form id="uploadForm" action="${pageContext.request.contextPath}/controller"
              method="POST" enctype="multipart/form-data" novalidate>
            <input type="hidden" name="command" value="upload_file">

            <div class="form-group">
                <label class="form-label"><fmt:message key="upload.field.file"/></label>
                <div class="upload-area-wrapper">
                    <div class="upload-area" id="uploadArea">
                        <input type="file" id="file" name="file" required
                               accept=".jpg,.jpeg,.png,.gif,.pdf,.txt,.doc,.docx">
                        <div class="upload-icon">&#128229;</div>
                        <p id="uploadLabel"><fmt:message key="upload.field.file"/></p>
                        <p style="font-size:.8rem"><fmt:message key="upload.hint"/></p>
                    </div>
                </div>
                <span class="field-error" id="fileError"><fmt:message key="upload.field.file"/></span>
            </div>

            <div style="display:flex;gap:12px">
                <button type="submit" class="btn btn-primary" style="flex:1"><fmt:message key="upload.submit"/></button>
                <a href="${pageContext.request.contextPath}/controller?command=list_files"
                   class="btn btn-outline" style="flex:0 0 auto"><fmt:message key="upload.cancel"/></a>
            </div>
        </form>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
