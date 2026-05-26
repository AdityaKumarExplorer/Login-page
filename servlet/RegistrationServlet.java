package servlet;

import database.DataConnector;
import database.EmailService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.util.Random;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");

        // ── STEP 1: Validate inputs, send OTP ────────────────────
        if (step == null || step.equals("1")) {
            String email           = request.getParameter("email");
            String password        = request.getParameter("password");
            String confirmPassword = request.getParameter("confirm_password");

            // Check passwords match
            if (!password.equals(confirmPassword)) {
                response.sendRedirect("Registration.jsp?error=mismatch");
                return;
            }

            // Check email not already registered
            DataConnector db = new DataConnector();
            boolean exists = db.emailExists(email);
            db.closeConnection();

            if (exists) {
                response.sendRedirect("Registration.jsp?error=exists");
                return;
            }

            // Store registration details in session temporarily
            HttpSession session = request.getSession();
            session.setAttribute("regEmail",    email);
            session.setAttribute("regPassword", password);

            // Generate OTP
            String otp = String.format("%06d", new Random().nextInt(999999));
            session.setAttribute("regOtp",      otp);
            session.setAttribute("regOtpExpiry", System.currentTimeMillis() + (2 * 60 * 1000)); // 2 minutes

            // Send OTP
            EmailService.sendOTP(email, otp);

            response.sendRedirect("Registration.jsp?step=2&email="
                + java.net.URLEncoder.encode(email, "UTF-8")
                + "&password=" + java.net.URLEncoder.encode(password, "UTF-8")
                + "&success=1");

        // ── STEP 2: Verify OTP, create account ───────────────────
        } else if (step.equals("2")) {
            String enteredOtp = request.getParameter("otp");

            HttpSession session = request.getSession();
            String savedOtp     = (String) session.getAttribute("regOtp");
            Long   otpExpiry    = (Long)   session.getAttribute("regOtpExpiry");
            String email        = (String) session.getAttribute("regEmail");
            String password     = (String) session.getAttribute("regPassword");

            // Check expiry
            if (otpExpiry == null || System.currentTimeMillis() > otpExpiry) {
                session.removeAttribute("regOtp");
                session.removeAttribute("regOtpExpiry");
                session.removeAttribute("regEmail");
                session.removeAttribute("regPassword");
                response.sendRedirect("Registration.jsp?error=expired");
                return;
            }

            // Check OTP
            if (savedOtp == null || !savedOtp.equals(enteredOtp)) {
                response.sendRedirect("Registration.jsp?step=2&email="
                    + java.net.URLEncoder.encode(email, "UTF-8")
                    + "&password=" + java.net.URLEncoder.encode(password, "UTF-8")
                    + "&error=invalidotp");
                return;
            }

            // OTP valid — create account
            DataConnector db = new DataConnector();
            String result = db.registerUser(email, password);
            db.closeConnection();

            // Clear session
            session.removeAttribute("regOtp");
            session.removeAttribute("regOtpExpiry");
            session.removeAttribute("regEmail");
            session.removeAttribute("regPassword");

            switch (result) {
                case "success":
                    EmailService.sendWelcomeEmail(email);
                    response.sendRedirect("Login.jsp");
                    break;
                case "exists":
                    response.sendRedirect("Registration.jsp?error=exists");
                    break;
                default:
                    response.sendRedirect("Registration.jsp?error=failed");
            }
        }
    }
}