<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome Page</title>
</head>
<body>
    <%
    // Check if user is logged in
    if (session.getAttribute("username") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    %>

    <h2>Welcome, <%= session.getAttribute("username") %>!</h2>

    <p>You have successfully logged in.</p>

    <a href="logout">Logout</a>
</body>
</html>