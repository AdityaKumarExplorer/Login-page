package servlet;

import database.DataConnector;
import database.EmailService;
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

        request.getSession().removeAttribute("captcha");
        // ─────────────────────────────────────────────────────────

        // ── Track login attempts + timed lockout ──────────────────
        HttpSession session = request.getSession();
        Integer attempts    = (Integer) session.getAttribute("loginAttempts");
        Long    lockoutTime = (Long)    session.getAttribute("lockoutTime");

        if (attempts == null) attempts = 0;

        // Check if currently locked out
        if (lockoutTime != null) {
            if (System.currentTimeMillis() < lockoutTime) {
                long minutesLeft = ((lockoutTime - System.currentTimeMillis()) / 60000) + 1;
                response.sendRedirect("Login.jsp?error=locked&mins=" + minutesLeft);
                return;
            } else {
                // Lockout expired — reset
                session.removeAttribute("loginAttempts");
                session.removeAttribute("lockoutTime");
                attempts = 0;
            }
        }
        // ─────────────────────────────────────────────────────────

        // ── Check login credentials ───────────────────────────────
        DataConnector db = new DataConnector();
        boolean isValid = db.checkLogin(email, password);
        db.closeConnection();

        if (isValid) {
            session.removeAttribute("loginAttempts");
            session.removeAttribute("lockoutTime");
            session.setAttribute("user", email);
            EmailService.sendLoginConfirmation(email);
            response.sendRedirect("Main.jsp");

        } else {
            attempts++;
            session.setAttribute("loginAttempts", attempts);

            // Alert on 3rd failed attempt and beyond
            if (attempts >= 3) {
                EmailService.sendLoginAttemptAlert(email);
            }

            // Lock after 5 failed attempts for 15 minutes
            if (attempts >= 5) {
                session.setAttribute("lockoutTime",
                    System.currentTimeMillis() + (15 * 60 * 1000));
                response.sendRedirect("Login.jsp?error=locked&mins=15");
            } else {
                response.sendRedirect("Login.jsp?error=1");
            }
        }
    }
}