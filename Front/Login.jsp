<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Random" %>
<%
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789@#$%&";
    Random rand = new Random();
    StringBuilder captchaBuilder = new StringBuilder();
    for (int i = 0; i < 6; i++) {
        captchaBuilder.append(chars.charAt(rand.nextInt(chars.length())));
    }
    String captchaText = captchaBuilder.toString();
    session.setAttribute("captcha", captchaText);
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/captcha.css">
        <title>Login</title>
    </head>
    <body>
        <div class="login form">
            <h2>Login page</h2>

            <% if(request.getParameter("error") != null) { 
                String error = request.getParameter("error"); %>
                <% if("captcha".equals(error)) { %>
                    <p style="color:red;">Incorrect CAPTCHA. Please try again.</p>
                <% } else { %>
                    <p style="color:red;">Login failed. Please check your credentials.</p>
                <% } %>
            <% } %>

            <form action="login" method="post" onsubmit="return validateCaptcha()">
                <p>Please use your credentials to login</p>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" placeholder="Email" required>
                <br>

                <label for="password">Password:</label>
                <input type="password" id="password" name="password" placeholder="Password" required minlength="6">
                <br>

                <input type="checkbox" id="remember" name="remember">
                <label for="remember">Remember me</label>
                <br><br>

                <label>Enter the characters shown:</label>
                <div class="captcha-container">
                    <canvas id="captchaCanvas" width="160" height="50"></canvas>
                    <button type="button" class="captcha-refresh" onclick="refreshCaptcha()" title="Refresh CAPTCHA">&#8635;</button>
                </div>
                <input type="text" id="captchaInput" name="captchaInput"
                       placeholder="Type characters here" required
                       autocomplete="off" maxlength="6">
                <p class="captcha-error" id="captchaError" style="display:none;">
                    Characters don't match. Try again.
                </p>
                <br>

                <input type="hidden" name="step" value="1">
                <br>
                <button type="submit">Login</button>
            </form>

            <p class="form-footer">Forget Password? <a href="ForgotPassword.jsp">Reset it here</a></p>
            <p class="form-footer">Don't have an account? <a href="Registration.jsp">Create one here</a></p>
        </div>

        <script>
            var captchaText = "<%= captchaText %>";

            function drawCaptcha(text) {
                var canvas = document.getElementById("captchaCanvas");
                var ctx = canvas.getContext("2d");

                ctx.clearRect(0, 0, canvas.width, canvas.height);

                var gradient = ctx.createLinearGradient(0, 0, canvas.width, canvas.height);
                gradient.addColorStop(0, "#eef2ff");
                gradient.addColorStop(1, "#e8f5e9");
                ctx.fillStyle = gradient;
                ctx.fillRect(0, 0, canvas.width, canvas.height);

                for (var i = 0; i < 6; i++) {
                    ctx.beginPath();
                    ctx.moveTo(Math.random() * canvas.width, Math.random() * canvas.height);
                    ctx.lineTo(Math.random() * canvas.width, Math.random() * canvas.height);
                    ctx.strokeStyle = "rgba(" +
                        Math.floor(Math.random() * 150) + "," +
                        Math.floor(Math.random() * 150) + "," +
                        Math.floor(Math.random() * 150) + ", 0.4)";
                    ctx.lineWidth = 1;
                    ctx.stroke();
                }

                for (var j = 0; j < 40; j++) {
                    ctx.beginPath();
                    ctx.arc(Math.random() * canvas.width, Math.random() * canvas.height, 1, 0, Math.PI * 2);
                    ctx.fillStyle = "rgba(100,100,100,0.3)";
                    ctx.fill();
                }

                var colors = ["#1a237e", "#b71c1c", "#1b5e20", "#4a148c", "#e65100", "#006064"];
                var fonts  = ["bold 24px Georgia", "bold 26px Courier New", "bold 24px Arial", "bold 25px Times New Roman"];

                for (var k = 0; k < text.length; k++) {
                    ctx.save();
                    ctx.translate(18 + k * 24, 32 + (Math.random() * 6 - 3));
                    ctx.rotate((Math.random() - 0.5) * 0.5);
                    ctx.font = fonts[Math.floor(Math.random() * fonts.length)];
                    ctx.fillStyle = colors[Math.floor(Math.random() * colors.length)];
                    ctx.fillText(text[k], 0, 0);
                    ctx.restore();
                }
            }

            function refreshCaptcha() {
                fetch("refreshCaptcha")
                    .then(function(res) { return res.text(); })
                    .then(function(newText) {
                        captchaText = newText.trim();
                        drawCaptcha(captchaText);
                        document.getElementById("captchaInput").value = "";
                        document.getElementById("captchaError").style.display = "none";
                    })
                    .catch(function() {
                        location.reload();
                    });
            }

            function validateCaptcha() {
                var input = document.getElementById("captchaInput").value.trim();
                if (input !== captchaText) {
                    document.getElementById("captchaError").style.display = "block";
                    document.getElementById("captchaInput").value = "";
                    refreshCaptcha();  // fetch new one from server, no reload
                    return false;
                }
                // Pre-fetch new CAPTCHA in background before page redirects back
                fetch("refreshCaptcha")
                    .then(function(res) { return res.text(); })
                    .then(function(newText) { captchaText = newText.trim(); });
                return true;
            }

            drawCaptcha(captchaText);
        </script>
    </body>
</html>