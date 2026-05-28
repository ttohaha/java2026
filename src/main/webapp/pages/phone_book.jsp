<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Phone Book</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="fragments/navbar.jsp" %>

<div class="container">
    <h2>Phone Book</h2>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-error">${errorMessage}</div>
    </c:if>

    <%-- Search bar --%>
    <div class="search-bar">
        <input type="text" id="searchInput" placeholder="Search by name, phone or email..."
               oninput="searchEntries(this.value)">
    </div>

    <%-- Sort controls --%>
    <div class="sort-bar">
        Sort by:
        <a href="${pageContext.request.contextPath}/phonebook?sort=alphabetical
                &page=${currentPage}" class="${sort == 'alphabetical' ? 'active' : ''}">Name</a>
        |
        <a href="${pageContext.request.contextPath}/phonebook?sort=phoneNumber
                &page=${currentPage}" class="${sort == 'phoneNumber' ? 'active' : ''}">Phone</a>
        |
        <a href="${pageContext.request.contextPath}/phonebook?sort=dateAdded
                &page=${currentPage}" class="${sort == 'dateAdded' ? 'active' : ''}">Date added</a>
    </div>

    <%-- Entry table --%>
    <table id="entriesTable" class="data-table">
        <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody id="entriesBody">
        <c:forEach var="entry" items="${entries}" varStatus="st">
            <tr id="row-${entry.id}">
                <td>${(currentPage - 1) * 6 + st.index + 1}</td>
                <td>${entry.contactName}</td>
                <td>${entry.contactPhone}</td>
                <td>${entry.contactEmail}</td>
                <td class="actions">
                    <a href="${pageContext.request.contextPath}/phonebook/edit?entryId=${entry.id}"
                       class="btn btn-sm btn-edit">Edit</a>

                    <form method="post"
                          action="${pageContext.request.contextPath}/phonebook/delete"
                          style="display:inline"
                          onsubmit="return confirm('Delete this contact?');">
                        <input type="hidden" name="entryId" value="${entry.id}">
                        <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <%-- Pagination --%>
    <div class="pagination">
        <c:if test="${currentPage > 1}">
            <a href="${pageContext.request.contextPath}/phonebook?page=${currentPage - 1}&sort=${sort}">
                &laquo; Prev
            </a>
        </c:if>
        <c:forEach begin="1" end="${totalPages}" var="p">
            <a href="${pageContext.request.contextPath}/phonebook?page=${p}&sort=${sort}"
               class="${p == currentPage ? 'active' : ''}">${p}</a>
        </c:forEach>
        <c:if test="${currentPage < totalPages}">
            <a href="${pageContext.request.contextPath}/phonebook?page=${currentPage + 1}&sort=${sort}">
                Next &raquo;
            </a>
        </c:if>
    </div>

    <a href="${pageContext.request.contextPath}/phonebook/add"
       class="btn btn-primary" style="margin-top:16px;">+ Add Contact</a>
</div>

<script>
    const ctx = '${pageContext.request.contextPath}';

    function searchEntries(query) {
        if (query.length < 1) {
            document.getElementById('entriesBody').innerHTML = '';
            location.reload();
            return;
        }
        fetch(ctx + '/phonebook/search?q=' + encodeURIComponent(query))
            .then(r => r.json())
            .then(data => {
                const tbody = document.getElementById('entriesBody');
                tbody.innerHTML = '';
                data.forEach((e, i) => {
                    tbody.innerHTML +=
                        '<tr>' +
                        '<td>' + (i + 1) + '</td>' +
                        '<td>' + (e.contactName  || '') + '</td>' +
                        '<td>' + (e.contactPhone || '') + '</td>' +
                        '<td>' + (e.contactEmail || '') + '</td>' +
                        '<td>' +
                          '<a href="' + ctx + '/phonebook/edit?entryId=' + e.id +
                             '" class="btn btn-sm btn-edit">Edit</a>' +
                        '</td>' +
                        '</tr>';
                });
            });
    }
</script>
</body>
</html>
