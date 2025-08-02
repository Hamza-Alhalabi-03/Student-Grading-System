# Student Grading System
A Java-based grading management system demonstrating the evolution from command-line to modern web applications through three distinct implementations using different architectural approaches.

## 🎯 Project Goal
Showcase software architecture evolution by implementing the same grading system functionality across three different paradigms: Socket-based CLI, Traditional Web MVC, and Modern Spring Framework.

---

## ▶️ Demo
Watch the project in action:  
[🎬 Student Grading System Video](https://youtu.be/kKFd0WMQYVs)

---

## 🔧 Three Implementation Approaches

### 1. Command-Line + Sockets (CLI Version)
- **Architecture**: Client-Server with Java Sockets
- **Backend**: JDBC with DAO pattern
- **Concurrency**: Multi-threaded server using ExecutorService
- **Interface**: Console-based text menus
- **Authentication**: Basic username/password validation

### 2. Traditional Web App (Servlet/JSP Version)
- **Architecture**: MVC with Servlets and JSPs
- **Backend**: Enhanced JDBC with connection pooling
- **Frontend**: JSP pages with HTML/CSS
- **Authentication**: HTTP session-based
- **Run**: `mvn tomcat7:run`

### 3. Modern Spring Web App (Spring MVC Version)
- **Architecture**: Spring MVC with REST APIs
- **Backend**: Spring JDBC Template
- **Frontend**: Thymeleaf templates with responsive CSS
- **Features**: Dependency injection, declarative transactions

  
---

## Progressive Complexity
- **Version 1**: Basic CRUD operations with console interface
- **Version 2**: Web-based interface with session management
- **Version 3**: Modern framework with REST APIs and enhanced UI


## 📄 License
This project is provided for educational and demonstration purposes only.  
Created by Hamza Alhalabi as part of Java enterprise development learning.
