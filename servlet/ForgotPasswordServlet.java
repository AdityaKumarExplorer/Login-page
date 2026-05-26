package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.util.Random;

import database.DataConnector;
import database.EmailService;

@WebServlet("/forgotpassword")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");

        // ── STEP 1: Verify email exists, generate OTP ──
        if (step.equals("1")) {
            String email = request.getParameter("email");

            DataConnector db = new DataConnector();
            boolean emailExists = db.emailExists(email);
            db.closeConnection();

            if (!emailExists) {
                response.sendRedirect("ForgotPassword.jsp?error=notfound");
                return;
            }

            // Generate OTP and store in session with expiry
            String otp = String.format("%06d", new Random().nextInt(999999));
            HttpSession session = request.getSession();
            session.setAttribute("otp", otp);
            session.setAttribute("otpEmail", email);
            session.setAttribute("otpExpiry", System.currentTimeMillis() + (2 * 60 * 1000)); // 2 minutes

            // Send OTP email
            EmailService.sendOTP(email, otp);

            response.sendRedirect("ForgotPassword.jsp?step=2&email="
                + java.net.URLEncoder.encode(email, "UTF-8") + "&success=1");

        // ── STEP 2: Verify OTP and reset password ──
        } else if (step.equals("2")) {
            String email       = request.getParameter("email");
            String enteredOtp  = request.getParameter("otp");
            String newPassword = request.getParameter("new_password");

            HttpSession session = request.getSession();
            String savedOtp     = (String) session.getAttribute("otp");
            String savedEmail   = (String) session.getAttribute("otpEmail");
            Long   otpExpiry    = (Long)   session.getAttribute("otpExpiry");

            // Check OTP expiry
            if (otpExpiry == null || System.currentTimeMillis() > otpExpiry) {
                session.removeAttribute("otp");
                session.removeAttribute("otpEmail");
                session.removeAttribute("otpExpiry");
                response.sendRedirect("ForgotPassword.jsp?error=expired");
                return;
            }

            // Check OTP matches and is for the right email
            if (savedOtp == null || !savedOtp.equals(enteredOtp) || !savedEmail.equals(email)) {
                response.sendRedirect("ForgotPassword.jsp?step=2&email="
                    + java.net.URLEncoder.encode(email, "UTF-8") + "&error=invalidotp");
                return;
            }

            // OTP valid — reset the password
            DataConnector db = new DataConnector();
            boolean success = db.resetPassword(email, newPassword);
            db.closeConnection();

            // Clear OTP from session
            session.removeAttribute("otp");
            session.removeAttribute("otpEmail");
            session.removeAttribute("otpExpiry");

            if (success) {
                EmailService.sendPasswordChangedAlert(email);
                response.sendRedirect("Login.jsp");
            } else {
                response.sendRedirect("ForgotPassword.jsp?error=failed");
            }
        }
    }
}