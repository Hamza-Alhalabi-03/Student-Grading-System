<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container">
        <h2>Welcome, <%= session.getAttribute("username") %></h2>

        <div class="menu">
            <h3>Menu:</h3>
            <ul>
                <li><a href="${pageContext.request.contextPath}/student/courses">View Courses</a></li>
                <li><a href="${pageContext.request.contextPath}/student/grades">View Grades</a></li>
                <li><a href="${pageContext.request.contextPath}/student/statistics">View Course Statistics</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
            </ul>
        </div>

        <div class="content">
            <h3>Student Dashboard</h3>
            <p>Welcome to your student dashboard. Use the menu to select an option.</p>

            <!-- Display message/error if exists -->
            <c:if test="${not empty message}">
                <div class="success-message">${message}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="error-message">${errorMessage}</div>
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

            <!-- 3. Course Statistics - Course Selection -->
            <c:if test="${operation eq 'selectCourseForStatistics' && not empty courses}">
                <h4>Select Course to View Statistics</h4>
                <table>
                    <tr>
                        <th>Course Name</th>
                        <th>Instructor</th>
                        <th>Action</th>
                    </tr>
                    <c:forEach var="course" items="${courses}">
                        <tr>
                            <td>${course.key}</td>
                            <td>${course.value}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/student/statistics?course=${course.key}">View Statistics</a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>

            <!-- 3. Course Statistics - Display Statistics -->
            <c:if test="${operation eq 'viewCourseStatistics' && not empty statistics}">
                <h4>Statistics for ${selectedCourse}</h4>
                <table>
                    <tr>
                        <th>Metric</th>
                        <th>Value</th>
                    </tr>
                    <tr>
                        <td>Total Students</td>
                        <td>${statistics.totalStudents}</td>
                    </tr>
                    <tr>
                        <td>Average Grade</td>
                        <td>${statistics.averageGrade}</td>
                    </tr>
                    <tr>
                        <td>Highest Grade</td>
                        <td>${statistics.highestGrade}</td>
                    </tr>
                    <tr>
                        <td>Lowest Grade</td>
                        <td>${statistics.lowestGrade}</td>
                    </tr>
                    <tr>
                        <td>Median Grade</td>
                        <td>${statistics.medianGrade}</td>
                    </tr>
                </table>
            </c:if>
        </div>
    </div>
</body>
</html>