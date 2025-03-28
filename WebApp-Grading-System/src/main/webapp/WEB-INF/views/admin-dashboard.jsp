<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container">
        <h2>Welcome, <%= session.getAttribute("username") %> (Administrator)</h2>

        <div class="clearfix">
            <div class="menu">
                <h3>Menu</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/addStudent">1. Add Student</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/deleteStudent">2. Delete Student</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/addInstructor">3. Add Instructor</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/deleteInstructor">4. Delete Instructor</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/addCourse">5. Add Course</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users">6. View All Users</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/courses">7. View All Courses</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">8. Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <h3>Admin Dashboard</h3>
                <p>Welcome to the admin dashboard. Select an option from the menu to perform administrative tasks.</p>

                <!-- Display message/error if exists -->
                <c:if test="${not empty message}">
                    <div class="success-message">${message}</div>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">${errorMessage}</div>
                </c:if>

                <!-- Add Student Form -->
                <c:if test="${operation eq 'addStudent'}">
                    <h4>Add Student</h4>
                    <form action="${pageContext.request.contextPath}/admin/addStudent" method="post">
                        <label for="username">Username:</label>
                        <input type="text" id="username" name="username" required>

                        <label for="password">Password:</label>
                        <input type="password" id="password" name="password" required>

                        <input type="submit" value="Add Student">
                    </form>
                </c:if>

                <!-- Delete Student Selection -->
                <c:if test="${operation eq 'deleteStudent'}">
                    <h4>Delete Student</h4>
                    <c:if test="${empty students}">
                        <p>No students found.</p>
                    </c:if>
                    <c:if test="${not empty students}">
                        <table>
                            <tr>
                                <th>Username</th>
                                <th>Action</th>
                            </tr>
                            <c:forEach var="student" items="${students}">
                                <tr>
                                    <td>${student.key}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/deleteStudent?username=${student.key}" class="action-button"
                                           onclick="return confirm('Are you sure you want to delete this student?')">Delete</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </c:if>
                </c:if>

                <!-- Add Instructor Form -->
                <c:if test="${operation eq 'addInstructor'}">
                    <h4>Add Instructor</h4>
                    <form action="${pageContext.request.contextPath}/admin/addInstructor" method="post">
                        <label for="username">Username:</label>
                        <input type="text" id="username" name="username" required>

                        <label for="password">Password:</label>
                        <input type="password" id="password" name="password" required>

                        <input type="submit" value="Add Instructor">
                    </form>
                </c:if>

                <!-- Delete Instructor Selection -->
                <c:if test="${operation eq 'deleteInstructor'}">
                    <h4>Delete Instructor</h4>
                    <c:if test="${empty instructors}">
                        <p>No instructors found.</p>
                    </c:if>
                    <c:if test="${not empty instructors}">
                        <table>
                            <tr>
                                <th>Username</th>
                                <th>Action</th>
                            </tr>
                            <c:forEach var="instructor" items="${instructors}">
                                <tr>
                                    <td>${instructor.key}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/deleteInstructor?username=${instructor.key}" class="action-button"
                                           onclick="return confirm('Are you sure you want to delete this instructor?')">Delete</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </c:if>
                </c:if>

                <!-- Add Course Form -->
                <c:if test="${operation eq 'addCourse' || param.section eq 'courses'}">
                    <h4>Add Course</h4>
                    <form action="${pageContext.request.contextPath}/admin/addCourse" method="post">
                        <label for="courseName">Course Name:</label>
                        <input type="text" id="courseName" name="courseName" required>

                        <label for="instructorName">Instructor:</label>
                        <select id="instructorName" name="instructorName" required>
                            <option value="">Select an instructor</option>
                            <c:forEach var="instructor" items="${instructors}">
                                <option value="${instructor.key}">${instructor.key}</option>
                            </c:forEach>
                        </select>

                        <input type="submit" value="Add Course">
                    </form>
                </c:if>

                <!-- View All Users -->
                <c:if test="${operation eq 'viewAllUsers' && not empty users}">
                    <h4>All Users</h4>
                    <table>
                        <tr>
                            <th>Username</th>
                            <th>Role</th>
                        </tr>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.key}</td>
                                <td>${user.value}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>

                <!-- View All Courses -->
                <c:if test="${operation eq 'viewAllCourses' && not empty courses}">
                    <h4>All Courses</h4>
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
            </div>
        </div>
    </div>
</body>
</html>