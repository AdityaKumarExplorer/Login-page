<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/otp.css">
        <title>Forgot Password</title>
    </head>
    <body>
        <div class="login form">
        <h2>Forgot Password</h2>

        <%-- Error / success messages --%>
        <% if(request.getParameter("error") != null) { 
            String error = request.getParameter("error"); %>
            <% if("notfound".equals(error)) { %>
                <p style="color:red;">No account found with that email.</p>
            <% } else if("invalidotp".equals(error)) { %>
                <p style="color:red;">Invalid OTP. Try again.</p>
            <% } else if("expired".equals(error)) { %>
                <p style="color:red;">OTP expired. Please request a new one.</p>
            <% } else { %>
                <p style="color:red;">Something went wrong. Please try again.</p>
            <% } %>
        <% } %>
        <% if(request.getParameter("success") != null) { %>
            <p style="color:green;">OTP sent to your email.</p>
        <% } %>

        <%-- STEP 1: Email form --%>
        <% if(request.getParameter("step") == null || request.getParameter("step").equals("1")) { %>
            <form action="forgotpassword" method="post">
                <input type="hidden" name="step" value="1" />
                <p>Enter your registered email to receive an OTP</p>
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" placeholder="Email" required>
                <br>
                <button type="submit">Send OTP</button>
            </form>

        <%-- STEP 2: OTP + new password form --%>
        <% } else if(request.getParameter("step").equals("2")) { %>
            <form action="forgotpassword" method="post" id="otpForm">
                <input type="hidden" name="step" value="2" />
                <input type="hidden" name="email" value="<%= request.getParameter("email") %>" />
                <p>Enter the OTP sent to your email and your new password</p>

                <label for="otp">OTP:</label>
                <input type="text" id="otp" name="otp" placeholder="Enter OTP" required maxlength="6">
                <br>

                <!-- Countdown Timer -->
                <div class="otp-timer-container">
                    <span id="timerLabel">OTP expires in: </span>
                    <span id="countdown" class="otp-countdown">2:00</span>
                </div>

                <!-- Resend button -->
                <form action="forgotpassword" method="post" id="resendForm">
                    <input type="hidden" name="step" value="1" />
                    <input type="hidden" name="email" value="<%= request.getParameter("email") %>" />
                    <button type="submit" id="resendBtn" class="otp-resend" disabled
                            onclick="invalidateOTP()">
                        Resend OTP (<span id="resendCountdown">50</span>s)
                    </button>
                </form>

                <label for="new_password">New Password:</label>
                <input type="password" id="new_password" name="new_password" placeholder="New Password" required>
                <br>
                <button type="submit">Reset Password</button>
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

        <p class="form-footer">Remembered your password? <a href="Login.jsp">Login here</a></p>
        </div>
    </body>
</html>