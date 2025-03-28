<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Instructor Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
    <div class="container">
        <h2>Welcome, <%= session.getAttribute("username") %></h2>

        <div class="clearfix">
            <div class="menu">
                <h3>Menu:</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/instructor/courses">View Courses</a></li>
                    <li><a href="${pageContext.request.contextPath}/instructor/courseGrades">View Course Grades</a></li>
                    <li><a href="${pageContext.request.contextPath}/instructor/editGrade">Edit Student Grade</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                </ul>
            </div>

            <div class="content">
                <h3>Instructor Dashboard</h3>
                <p>Welcome to your instructor dashboard. Use the menu to select an option.</p>

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
                        </tr>
                        <c:forEach var="course" items="${courses}">
                            <tr>
                                <td>${course}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>

                <!-- 2. View Course Grades - Course Selection -->
                <c:if test="${operation eq 'selectCourseForGrades' && not empty courses}">
                    <h4>Select Course to View Grades</h4>
                    <table>
                        <tr>
                            <th>Course Name</th>
                            <th>Action</th>
                        </tr>
                        <c:forEach var="course" items="${courses}">
                            <tr>
                                <td>${course}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/instructor/courseGrades?course=${course}">View Grades</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>

                <!-- 2. View Course Grades - Display Grades -->
                <c:if test="${operation eq 'viewCourseGrades' && not empty courseGrades}">
                    <h4>Student Grades for ${selectedCourse}</h4>
                    <table>
                        <tr>
                            <th>Student</th>
                            <th>Grade</th>
                        </tr>
                        <c:forEach var="grade" items="${courseGrades}">
                            <tr>
                                <td>${grade.key}</td>
                                <td>${grade.value}</td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>

                <!-- 3. Edit Student Grade - Course Selection -->
                <c:if test="${operation eq 'selectCourseForGradeEdit' && not empty courses}">
                    <h4>Select Course to Edit Grades</h4>
                    <table>
                        <tr>
                            <th>Course Name</th>
                            <th>Action</th>
                        </tr>
                        <c:forEach var="course" items="${courses}">
                            <tr>
                                <td>${course}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/instructor/editGrade?course=${course}">Edit Grades</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>

                <!-- 3. Edit Student Grade - Student Selection -->
                <c:if test="${operation eq 'selectStudentForGradeEdit' && not empty courseGrades}">
                    <h4>Select Student to Edit Grade for ${selectedCourse}</h4>
                    <table>
                        <tr>
                            <th>Student</th>
                            <th>Current Grade</th>
                            <th>Action</th>
                        </tr>
                        <c:forEach var="grade" items="${courseGrades}">
                            <tr>
                                <td>${grade.key}</td>
                                <td>${grade.value}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/instructor/editGrade?course=${selectedCourse}&student=${grade.key}">Edit</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </c:if>

                <!-- 3. Edit Student Grade - Edit Form -->
                <c:if test="${operation eq 'editGradeForm' && not empty selectedCourse && not empty selectedStudent}">
                    <h4>Edit Grade for ${selectedStudent} in ${selectedCourse}</h4>
                    <form action="${pageContext.request.contextPath}/instructor/updateGrade" method="post">
                        <input type="hidden" name="course" value="${selectedCourse}">
                        <input type="hidden" name="student" value="${selectedStudent}">
                        <label for="newGrade">New Grade:</label>
                        <input type="text" id="newGrade" name="newGrade" required>
                        <input type="submit" value="Update Grade">
                    </form>
                </c:if>

                <c:if test="${not empty statistics}">
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
    </div>
</body>
</html>