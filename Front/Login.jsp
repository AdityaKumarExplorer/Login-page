<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="css/style.css">
        <title>Login</title>
    </head>
    <body>
        <div class="login form">
        <h2>Login page</h2>

        <%-- Display error message if redirected back from Servlet with an error --%>
        <% if(request.getParameter("error") != null) { %>
            <p style="color:red;">Login failed. Please check your credentials.</p>
        <% } %>

        <form action="login" method="post"> <%-- Action points to Servlet --%>
            <p>Please use your credentials to login</p>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" placeholder="Email" required>
            <br>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" placeholder="Password" required minlength="6">
            <br>
            <input type="checkbox" id="remember" name="remember">
            <label for="remember">Remember me:</label>
            <br>
            <button type="submit">Login</button>
        </form>
            <p class="form-footer">Forget Password? <a href="ForgotPassword.jsp">Reset it here</a></p>
            <p class="form-footer">Don't have an account? <a href="Registration.jsp">Create one here</a></p>
        </div>
    </body>
</html>
