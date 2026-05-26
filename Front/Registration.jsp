<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/otp.css">
        <title>Registration</title>
    </head>
    <body>
        <div class="login form">
        <h2>Registration page</h2>

        <%-- Error / success messages --%>
        <% if(request.getParameter("error") != null) {
            String error = request.getParameter("error"); %>
            <% if("exists".equals(error)) { %>
                <p style="color:red;">Email already registered. <a href="Login.jsp">Login here</a></p>
            <% } else if("mismatch".equals(error)) { %>
                <p style="color:red;">Passwords do not match. Try again.</p>
            <% } else if("invalidotp".equals(error)) { %>
                <p style="color:red;">Invalid OTP. Try again.</p>
            <% } else if("expired".equals(error)) { %>
                <p style="color:red;">OTP expired. Please register again.</p>
            <% } else { %>
                <p style="color:red;">Registration failed. Please try again.</p>
            <% } %>
        <% } %>
        <% if(request.getParameter("success") != null) { %>
            <p style="color:green;">OTP sent to your email.</p>
        <% } %>

        <%-- STEP 1: Registration form --%>
        <% if(request.getParameter("step") == null || request.getParameter("step").equals("1")) { %>
            <form action="register" method="post">
                <input type="hidden" name="step" value="1" />
                <p>Please fill in your details to create an account</p>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" placeholder="Email" required>
                <br>

                <label for="password">Password:</label>
                <input type="password" id="password" name="password" placeholder="Password" required>
                <br>

                <label for="confirm_password">Confirm Password:</label>
                <input type="password" id="confirm_password" name="confirm_password" placeholder="Confirm Password" required>
                <br>

                <button type="submit">Register</button>
            </form>

        <%-- STEP 2: OTP verification --%>
        <% } else if(request.getParameter("step").equals("2")) { %>
            <form action="register" method="post" id="otpForm">
                <input type="hidden" name="step" value="2" />
                <p>Enter the OTP sent to your email to verify your account</p>

                <label for="otp">OTP:</label>
                <input type="text" id="otp" name="otp" placeholder="Enter OTP" required maxlength="6">
                <br>

                <!-- Countdown Timer -->
                <div class="otp-timer-container">
                    <span id="timerLabel">OTP expires in: </span>
                    <span id="countdown" class="otp-countdown">2:00</span>
                </div>

                <!-- Resend button -->
                <form action="register" method="post" id="resendForm">
                    <input type="hidden" name="step" value="1" />
                    <input type="hidden" name="email"            value="<%= request.getParameter("email") %>" />
                    <input type="hidden" name="password"         value="<%= request.getParameter("password") %>" />
                    <input type="hidden" name="confirm_password" value="<%= request.getParameter("password") %>" />
                    <button type="submit" id="resendBtn" class="otp-resend" disabled
                            onclick="invalidateOTP()">
                        Resend OTP (<span id="resendCountdown">50</span>s)
                    </button>
                </form>

                <button type="submit">Verify & Create Account</button>
            </form>

            <script>
                var expirySeconds = 120;
                var resendSeconds = 50;

                var expiryDisplay = document.getElementById("countdown");
                var resendDisplay = document.getElementById("resendCountdown");
                var timerLabel    = document.getElementById("timerLabel");
                var resendBtn     = document.getElementById("resendBtn");

                function invalidateOTP() {
                    fetch("invalidateOTP");
                }

                var expiryTimer = setInterval(function () {
                    expirySeconds--;
                    var mins = Math.floor(expirySeconds / 60);
                    var secs = expirySeconds % 60;
                    expiryDisplay.textContent = mins + ":" + (secs < 10 ? "0" : "") + secs;

                    if (expirySeconds <= 30) {
                        expiryDisplay.style.color = "#e65100";
                    }
                    if (expirySeconds <= 0) {
                        clearInterval(expiryTimer);
                        timerLabel.textContent    = "OTP expired. ";
                        expiryDisplay.textContent = "0:00";
                        expiryDisplay.style.color = "red";
                    }
                }, 1000);

                var resendTimer = setInterval(function () {
                    resendSeconds--;
                    resendDisplay.textContent = resendSeconds;

                    if (resendSeconds <= 0) {
                        clearInterval(resendTimer);
                        resendBtn.disabled  = false;
                        resendBtn.innerHTML = "Resend OTP";
                    }
                }, 1000);
            </script>
        <% } %>

        <p class="form-footer">Already have an account? <a href="Login.jsp">Login here</a></p>
        </div>
    </body>
</html>