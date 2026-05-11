package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import database.DatabaseConnector;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;

@WebServlet("/register")       // matches action="register"
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email           = request.getParameter("email");
        String password        = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        // 1. Check passwords match before touching the DB
        if (!password.equals(confirmPassword)) {
            response.sendRedirect("Front/Registration.jsp?error=mismatch");
            return;
        }

        // 2. Try to register
        DatabaseConnector db = new DatabaseConnector();
        String result = db.registerUser(email, password);
        db.closeConnection();

        switch (result) {
            case "success":
                response.sendRedirect("Front/Login.jsp");          // go login
                break;
            case "exists":
                response.sendRedirect("Front/Registration.jsp?error=exists");
                break;
            default:
                response.sendRedirect("Front/Registration.jsp?error=failed");
        }
    }
}