package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;

@WebServlet("/invalidateOTP")
public class InvalidateOTPServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("otp");
            session.removeAttribute("otpEmail");
            session.removeAttribute("otpExpiry");
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}