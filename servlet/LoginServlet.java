package servlet;

import database.DataConnector;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        // ── Verify CAPTCHA first ──────────────────────────────────
        String enteredCaptcha = request.getParameter("captchaInput");
        String savedCaptcha   = (String) request.getSession().getAttribute("captcha");

        if (savedCaptcha == null || !savedCaptcha.equals(enteredCaptcha)) {
            response.sendRedirect("Login.jsp?error=captcha");
            return;
        }

        // Clear used CAPTCHA from session
        request.getSession().removeAttribute("captcha");
        // ─────────────────────────────────────────────────────────

        // ── Check login credentials ───────────────────────────────
        DataConnector db = new DataConnector();
        boolean isValid = db.checkLogin(email, password);
        db.closeConnection();

        if (isValid) {
            HttpSession session = request.getSession();
            session.setAttribute("user", email);
            response.sendRedirect("Main.jsp");
        } else {
            response.sendRedirect("Login.jsp?error=1");
        }
    }
}