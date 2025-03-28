<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .menu {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .menu ul {
            list-style-type: none;
            padding: 0;
        }
        .menu li {
            margin-bottom: 10px;
        }
        .menu a {
            text-decoration: none;
            color: #007bff;
        }
        .content {
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 8px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
    <h2>Welcome, <%= session.getAttribute("username") %></h2>
    
    <div class="menu">
        <h3>Menu</h3>
        <ul>
            <li><a href="dashboard?operation=viewCourses">1. View Courses</a></li>
            <li><a href="dashboard?operation=viewGrades">2. View Grades</a></li>
            <li><a href="logout">3. Logout</a></li>
        </ul>
    </div>
    
    <div class="content">
        <h3>Student Dashboard</h3>
        <p>Welcome to your student dashboard. Use the menu to select an option.</p>
        
        <!-- Display message/error if exists -->
        <c:if test="${not empty message}">
            <div style="color: green; margin: 10px 0;">${message}</div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div style="color: red; margin: 10px 0;">${errorMessage}</div>
        </c:if>
        
        <!-- 1. View Courses -->
        <c:if test="${operation eq 'viewCourses' && not empty courses}">
            <h4>My Courses</h4>
            <table>
                <tr>
                    <th>Course Name</th>
                    <th>Instructor</th>
                </tr>
                <c:forEach var="course" items="${courses}">
                    <tr>
                        <td>${course.key}</td>
                        <td>${course.value}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
        
        <!-- 2. View Grades -->
        <c:if test="${operation eq 'viewGrades' && not empty grades}">
            <h4>My Grades</h4>
            <table>
                <tr>
                    <th>Course</th>
                    <th>Grade</th>
                </tr>
                <c:forEach var="grade" items="${grades}">
                    <tr>
                        <td>${grade.key}</td>
                        <td>${grade.value}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>
    </div>
</body>
</html>
