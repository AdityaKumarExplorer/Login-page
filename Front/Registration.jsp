<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <link rel="stylesheet" href="css/style.css">
        <title>Registration</title>
    </head>
    <body>
        <div class="login form">
        <h2>Registration page</h2>

        <%-- Show error/success messages --%>
        <% if(request.getParameter("error") != null) { %>
            <% if(request.getParameter("error").equals("exists")) { %>
                <p style="color:red;">Email already registered. <a href="Login.jsp">Login here</a></p>
            <% } else if(request.getParameter("error").equals("mismatch")) { %>
                <p style="color:red;">Passwords do not match. Try again.</p>
            <% } else { %>
                <p style="color:red;">Registration failed. Please try again.</p>
            <% } %>
        <% } %>

        <form action="register" method="post"> <%-- Fixed: points to servlet, POST method --%>
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
        <p class="form-footer">Already have an account? <a href="Login.jsp">Login here</a></p>
        </div>
    </body>
</html>