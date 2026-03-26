<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="blinov_first.entity.User" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Main Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<h1>Hello, ${sessionScope.user}!</h1>

<h3>List of all users:</h3>
<table>
    <thead>
    <tr>
        <th>Lastname</th>
        <th>Email</th>
        <th>Phone</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<User> userList = (List<User>) request.getAttribute("userList");
        if (userList != null) {
            for (User u : userList) {
    %>
    <tr>
        <td><%= (u.getLastname() != null) ? u.getLastname() : "N/A" %></td>
        <td><%= u.getEmail() %></td>
        <td><%= (u.getPhone() != null) ? u.getPhone() : "N/A" %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="3" style="text-align: center;">Данные не загружены</td>
    </tr>
    <% } %>
    </tbody>
</table>

<br/>
<a href="${pageContext.request.contextPath}/controller?command=logout" class="logout-link">Logout (Sign Out)</a>

</body>
</html>