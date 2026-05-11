package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import database.DatabaseConnector;

@WebServlet("/login")          // matches action="login" in Login.jsp
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        DatabaseConnector db = new DatabaseConnector();
        boolean isValid = db.checkLogin(email, password);
        db.closeConnection();

        if (isValid) {
            HttpSession session = request.getSession();
            session.setAttribute("user", email);
            response.sendRedirect("Front/Main.jsp");
        } else {
            // getParameter("error") in JSP means we pass it as a URL param
            response.sendRedirect("Front/Login.jsp?error=1");
        }
    }
}