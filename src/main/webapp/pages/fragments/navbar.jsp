<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar">
    <div class="nav-brand">
        <a href="${pageContext.request.contextPath}/main">PhoneBook</a>
    </div>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/main">Users</a></li>
        <li><a href="${pageContext.request.contextPath}/phonebook">My Contacts</a></li>
        <li><a href="${pageContext.request.contextPath}/files">My Files</a></li>
        <li><a href="${pageContext.request.contextPath}/profile">Profile</a></li>
        <li class="nav-user">
            <span>${login}</span>
        </li>
        <li>
            <form method="post"
                  action="${pageContext.request.contextPath}/auth/logout"
                  style="display:inline;">
                <button type="submit" class="btn btn-link">Logout</button>
            </form>
        </li>
    </ul>
    <div class="locale-switcher">
        <a href="${pageContext.request.contextPath}/locale?lang=en">EN</a> |
        <a href="${pageContext.request.contextPath}/locale?lang=ru">RU</a>
    </div>
</nav>
