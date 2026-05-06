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
    <title><fmt:message key="phonebook.title"/> — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body class="app-page">
<%@ include file="fragments/navbar.jsp" %>
<div class="page-wrapper">
    <div class="page-header">
        <h2><fmt:message key="phonebook.heading"/></h2>
        <a href="${pageContext.request.contextPath}/controller?command=add_entry" class="btn btn-outline">
            &#43; <fmt:message key="phonebook.add"/>
        </a>
    </div>

    <pb:alert type="danger"  message="${errorMsg}"/>
    <pb:alert type="success" message="${successMsg}"/>

    <%-- AJAX live search bar --%>
    <div class="search-bar">
        <input type="search"
               id="ajaxSearch"
               class="form-control"
               placeholder="Search by name or phone…"
               autocomplete="off"
               data-search-url="${pageContext.request.contextPath}/controller?command=search_entries"/>
        <span id="ajaxSearchSpinner" class="search-spinner" hidden>&#8987;</span>
    </div>

    <div class="table-wrapper">
        <%-- Static table (shown on page load / no JS) --%>
        <div id="staticTable">
            <c:choose>
                <c:when test="${empty entryList}">
                    <div class="empty-state">
                        <div class="empty-icon">&#128222;</div>
                        <p><fmt:message key="phonebook.empty"/></p>
                        <a href="${pageContext.request.contextPath}/controller?command=add_entry"
                           class="btn btn-outline"><fmt:message key="phonebook.add"/></a>
                    </div>
                </c:when>
                <c:otherwise>
                    <fmt:message key="phonebook.delete.confirm" var="deleteConfirm"/>
                    <fmt:message key="phonebook.edit"           var="editLabel"/>
                    <fmt:message key="phonebook.delete"         var="deleteLabel"/>
                    <table class="data-table">
                        <thead><tr>
                            <th><fmt:message key="phonebook.col.name"/></th>
                            <th><fmt:message key="phonebook.col.phone"/></th>
                            <th><fmt:message key="phonebook.col.email"/></th>
                            <th><fmt:message key="phonebook.col.actions"/></th>
                        </tr></thead>
                        <tbody id="entriesTableBody">
                        <c:forEach var="entry" items="${entryList}">
                            <tr id="entry-row-${entry.id}">
                                <td><strong>${entry.contactName}</strong></td>
                                <td>${entry.contactPhone}</td>
                                <td>${entry.contactEmail}</td>
                                <td>
                                    <div class="table-actions">
                                        <pb:actionLink
                                                href="${pageContext.request.contextPath}/controller?command=edit_entry&entryId=${entry.id}"
                                                label="${editLabel}"
                                                style="outline"/>
                                        <button type="button"
                                                class="btn btn-danger btn-ajax-delete"
                                                data-entry-id="${entry.id}"
                                                data-confirm="${deleteConfirm}"
                                                data-delete-url="${pageContext.request.contextPath}/controller?command=delete_entry">
                                                ${deleteLabel}
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${entriesPage.totalPages > 1}">
                        <c:set var="totalPages"  value="${entriesPage.totalPages}"/>
                        <c:set var="currentPage" value="${entriesPage.currentPage}"/>
                        <div class="pagination" id="paginationBlock">
                            <c:if test="${entriesPage.previousAvailable}">
                                <a href="${pageContext.request.contextPath}/controller?command=list_entries&page=${entriesPage.previousPage}"
                                   class="page-btn">&#8592;</a>
                            </c:if>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <a href="${pageContext.request.contextPath}/controller?command=list_entries&page=${i}"
                                   class="page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
                            </c:forEach>

                            <c:if test="${entriesPage.nextAvailable}">
                                <a href="${pageContext.request.contextPath}/controller?command=list_entries&page=${entriesPage.nextPage}"
                                   class="page-btn">&#8594;</a>
                            </c:if>

                            <span class="page-info">
                            ${currentPage} / ${totalPages}
                            (<fmt:message key="phonebook.total"/>: ${entriesPage.totalItems})
                        </span>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- AJAX search results table (hidden until search is active) --%>
        <div id="ajaxResultsBlock" hidden>
            <table class="data-table">
                <thead><tr>
                    <th><fmt:message key="phonebook.col.name"/></th>
                    <th><fmt:message key="phonebook.col.phone"/></th>
                    <th><fmt:message key="phonebook.col.email"/></th>
                    <th><fmt:message key="phonebook.col.actions"/></th>
                </tr></thead>
                <tbody id="ajaxResultsBody"></tbody>
            </table>
            <p id="ajaxNoResults" hidden class="empty-state-text">No contacts found.</p>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
<script src="${pageContext.request.contextPath}/js/phonebook-ajax.js"></script>
</body>
</html>
