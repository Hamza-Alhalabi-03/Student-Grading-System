package webapp.controller;

import webapp.data.GradingSystemDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private GradingSystemDAO gradingDAO;

    public void init() {
        gradingDAO = new GradingSystemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (gradingDAO.validateUser(username, password)) {
            HttpSession session = request.getSession(true);

            // 30 minutes session timeout
            session.setMaxInactiveInterval(30 * 60);

            // Get the User object
            webapp.model.User user = gradingDAO.getUser(username, password);
            
            // Store user information in session
            session.setAttribute("username", username);
            session.setAttribute("userRole", user.getRole().toString());

            // Redirect to dashboard servlet
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}