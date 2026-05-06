<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<nav class="navbar">
    <div class="navbar-inner">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/controller?command=list_entries">
            &#128222; <fmt:message key="app.title"/>
        </a>
        <div class="navbar-nav">
            <a href="${pageContext.request.contextPath}/controller?command=list_entries"><fmt:message key="nav.phonebook"/></a>
            <a href="${pageContext.request.contextPath}/controller?command=list_files"><fmt:message key="nav.files"/></a>
        </div>
        <div class="navbar-user">
            <span class="username">&#128100; ${sessionScope.login}</span>
            <a href="${pageContext.request.contextPath}/controller?command=edit_profile"><fmt:message key="nav.profile"/></a>
            <div class="locale-switcher">
                <a href="${pageContext.request.contextPath}/controller?command=change_locale&lang=en"
                   class="locale-btn ${sessionScope.lang == 'en' || empty sessionScope.lang ? 'active' : ''}">EN</a>
                <a href="${pageContext.request.contextPath}/controller?command=change_locale&lang=ru"
                   class="locale-btn ${sessionScope.lang == 'ru' ? 'active' : ''}">RU</a>
            </div>
            <a href="${pageContext.request.contextPath}/controller?command=logout" class="btn-logout">
                <fmt:message key="nav.logout"/>
            </a>
        </div>
    </div>
</nav>
