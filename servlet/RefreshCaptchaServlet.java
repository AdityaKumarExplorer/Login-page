package servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.util.Random;

@WebServlet("/refreshCaptcha")
public class RefreshCaptchaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Generate new CAPTCHA text
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#$%&";
        Random rand = new Random();
        StringBuilder captchaBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            captchaBuilder.append(chars.charAt(rand.nextInt(chars.length())));
        }
        String captchaText = captchaBuilder.toString();

        // Store in session
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captchaText);

        // Return plain text to JavaScript
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(captchaText);
    }
}
