package webapp.controller;

import webapp.GradingSystemDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginTestServlet extends HttpServlet {
    private GradingSystemDAO gradingDAO;

    public void init() {
        gradingDAO = new GradingSystemDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

//        if (gradingDAO.validateUser(username, password)) {
//            // Create a new session
//            HttpSession session = request.getSession();
//
//            // Set session timeout (30 minutes of inactivity)
//            session.setMaxInactiveInterval(30 * 60);
//
//            // Store user info in session (but not password!)
//            session.setAttribute("username", username);
//
//            // Prevent session fixation by creating a new session
//            request.getSession().invalidate();
//            session = request.getSession(true);
//
//            // Redirect to welcome page
//            response.sendRedirect("welcome.jsp");
//        } else {
//            // Set error message
//            request.setAttribute("errorMessage", "Invalid username or password");
//            request.getRequestDispatcher("login.jsp").forward(request, response);
//        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to login page
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}