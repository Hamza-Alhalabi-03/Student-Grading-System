<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
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
            float: left;
            width: 20%;
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
            float: right;
            width: 75%;
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
        .clearfix::after {
            content: "";
            clear: both;
            display: table;
        }
        form {
            margin-top: 20px;
        }
        input[type=text], input[type=password], select {
            width: 100%;
            padding: 8px;
            margin: 5px 0 15px 0;
            display: inline-block;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        input[type=submit] {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .action-button {
            background-color: #dc3545;
            color: white;
            padding: 5px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            font-size: 0.8em;
        }
    </style>
</head>
<body>
    <h2>Welcome, <%= session.getAttribute("username") %> (Administrator)</h2>
    
    <div class="clearfix">
        <div class="menu">
            <h3>Menu</h3>
            <ul>
                <li><a href="dashboard?operation=addStudent">1. Add Student</a></li>
                <li><a href="dashboard?operation=deleteStudent">2. Delete Student</a></li>
                <li><a href="dashboard?operation=addInstructor">3. Add Instructor</a></li>
                <li><a href="dashboard?operation=deleteInstructor">4. Delete Instructor</a></li>
                <li><a href="dashboard?operation=addCourse">5. Add Course</a></li>
                <li><a href="dashboard?operation=viewAllUsers">6. View All Users</a></li>
                <li><a href="dashboard?operation=viewAllCourses">7. View All Courses</a></li>
                <li><a href="logout">8. Logout</a></li>
            </ul>
        </div>
        
        <div class="content">
            <h3>Admin Dashboard</h3>
            <p>Welcome to the admin dashboard. Select an option from the menu to perform administrative tasks.</p>
            
            <!-- Display message/error if exists -->
            <c:if test="${not empty message}">
                <div style="color: green; margin: 10px 0;">${message}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div style="color: red; margin: 10px 0;">${errorMessage}</div>
            </c:if>
            
            <!-- Add Student Form -->
            <c:if test="${operation eq 'addStudent'}">
                <h4>Add Student</h4>
                <form action="dashboard" method="post">
                    <input type="hidden" name="operation" value="addStudent">
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
                                    <a href="dashboard?operation=deleteStudent&username=${student.key}" class="action-button"
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
                <form action="dashboard" method="post">
                    <input type="hidden" name="operation" value="addInstructor">
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
                                    <a href="dashboard?operation=deleteInstructor&username=${instructor.key}" class="action-button"
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
                <form action="dashboard" method="post">
                    <input type="hidden" name="operation" value="addCourse">
                    <label for="courseName">Course Name:</label>
                    <input type="text" id="courseName" name="courseName" required>
                    
                    <label for="instructorName">Instructor:</label>
                    <select id="instructorName" name="instructorName" required>
                        <c:forEach var="instructor" items="${instructors}">
                            <option value="${instructor.key}">${instructor.key}</option>
                        </c:forEach>
                    </select>
                    
                    <input type="submit" value="Add Course">
                </form>
            </c:if>
            
            <!-- View All Users -->
            <c:if test="${operation eq 'viewAllUsers' || not empty users}">
                <h4>All Users</h4>
                <c:if test="${empty users}">
                    <p>No users found.</p>
                </c:if>
                <c:if test="${not empty users}">
                    <table>
                        <tr>
                            <th>Username</th>
                            <th>Role</th>
                            <th>Actions</th>
                        </tr>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.key}</td>
                                <td>${user.value}</td>
                                <td>
                                    <c:if test="${user.value eq 'student'}">
                                        <a href="dashboard?operation=deleteStudent&username=${user.key}" class="action-button"
                                           onclick="return confirm('Are you sure you want to delete this student?')">Delete</a>
                                    </c:if>
                                    <c:if test="${user.value eq 'instructor'}">
                                        <a href="dashboard?operation=deleteInstructor&username=${user.key}" class="action-button"
                                           onclick="return confirm('Are you sure you want to delete this instructor?')">Delete</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>
            </c:if>
            
            <!-- View All Courses -->
            <c:if test="${operation eq 'viewAllCourses' || not empty courses}">
                <h4>All Courses</h4>
                <c:if test="${empty courses}">
                    <p>No courses found.</p>
                </c:if>
                <c:if test="${not empty courses}">
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
            </c:if>
        </div>
    </div>
    
    <script>
        function toggleForm(formId) {
            var form = document.getElementById(formId);
            if (form.style.display === "none") {
                form.style.display = "block";
            } else {
                form.style.display = "none";
            }
        }
    </script>
</body>
</html>
