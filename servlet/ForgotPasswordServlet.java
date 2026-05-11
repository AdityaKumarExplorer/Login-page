package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.util.Random;
import database.DatabaseConnector;

@WebServlet("/forgotpassword")      // matches action="forgotpassword"
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");

        // ── STEP 1: Verify email exists, generate OTP ──
        if (step.equals("1")) {
            String email = request.getParameter("email");

            DatabaseConnector db = new DatabaseConnector();
            boolean emailExists = db.emailExists(email);   // new method needed (see below)
            db.closeConnection();

            if (!emailExists) {
                response.sendRedirect("Front/ForgotPassword.jsp?error=notfound");
                return;
            }

            // Generate a 6-digit OTP and store in session
            String otp = String.format("%06d", new Random().nextInt(999999));
            HttpSession session = request.getSession();
            session.setAttribute("otp", otp);
            session.setAttribute("otpEmail", email);

            // In production: email the OTP using JavaMail
            // For now: print to console for testing
            System.out.println("OTP for " + email + ": " + otp);

            // Go to step 2
            response.sendRedirect("Front/ForgotPassword.jsp?step=2&email="
                + java.net.URLEncoder.encode(email, "UTF-8") + "&success=1");

        // ── STEP 2: Verify OTP and reset password ──
        } else if (step.equals("2")) {
            String email       = request.getParameter("email");
            String enteredOtp  = request.getParameter("otp");
            String newPassword = request.getParameter("new_password");

            HttpSession session = request.getSession();
            String savedOtp     = (String) session.getAttribute("otp");
            String savedEmail   = (String) session.getAttribute("otpEmail");

            // Check OTP matches and is for the right email
            if (savedOtp == null || !savedOtp.equals(enteredOtp)|| !savedEmail.equals(email)) {
                response.sendRedirect("Front/ForgotPassword.jsp?step=2&email="
                    + java.net.URLEncoder.encode(email, "UTF-8") + "&error=invalidotp");
                return;
            }

            // OTP valid — reset the password
            DatabaseConnector db = new DatabaseConnector();
            boolean success = db.resetPassword(email, newPassword);
            db.closeConnection();

            // Clear OTP from session
            session.removeAttribute("otp");
            session.removeAttribute("otpEmail");

            if (success) {
                response.sendRedirect("Front/Login.jsp");
            } else {
                response.sendRedirect("Front/ForgotPassword.jsp?error=failed");
            }
        }
    }
}