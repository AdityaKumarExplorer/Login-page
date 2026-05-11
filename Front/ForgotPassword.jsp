<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="css/style.css">
        <title>Forgot Password</title>
    </head>
    <body>
        <div class="login form">
        <h2>Forgot Password</h2>

        <%-- Error / success messages --%>
        <% if(request.getParameter("error") != null) { %>
            <% if(request.getParameter("error").equals("notfound")) { %>
                <p style="color:red;">No account found with that email.</p>
            <% } else if(request.getParameter("error").equals("invalidotp")) { %>
                <p style="color:red;">Invalid or expired OTP. Try again.</p>
            <% } else { %>
                <p style="color:red;">Something went wrong. Please try again.</p>
            <% } %>
        <% } %>
        <% if(request.getParameter("success") != null) { %>
            <p style="color:green;">OTP sent to your email.</p>
        <% } %>

        <%-- STEP 1: Show email form if OTP not sent yet --%>
        <% if(request.getParameter("step") == null || request.getParameter("step").equals("1")) { %>
            <form action="forgotpassword" method="post">
                <input type="hidden" name="step" value="1" />
                <p>Enter your registered email to receive an OTP</p>
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" placeholder="Email" required>
                <br>
                <button type="submit">Send OTP</button>
            </form>

        <%-- STEP 2: Show OTP + new password form after OTP is sent --%>
        <% } else if(request.getParameter("step").equals("2")) { %>
            <form action="forgotpassword" method="post">
                <input type="hidden" name="step" value="2" />
                <%-- Pass email forward so servlet knows who to reset --%>
                <input type="hidden" name="email" value="<%= request.getParameter("email") %>" />
                <p>Enter the OTP sent to your email and your new password</p>
                <label for="otp">OTP:</label>
                <input type="text" id="otp" name="otp" placeholder="Enter OTP" required>
                <br>
                <label for="new_password">New Password:</label>
                <input type="password" id="new_password" name="new_password" placeholder="New Password" required>
                <br>
                <button type="submit">Reset Password</button>
            </form>
        <% } %>

        <p class="form-footer">Remembered your password? <a href="Login.jsp">Login here</a></p>
        </div>
    </body>
</html>