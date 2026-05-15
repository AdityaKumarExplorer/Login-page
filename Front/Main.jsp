<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String user = (String) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("Login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome</title>
    <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600&family=Cormorant+Garamond:ital,wght@0,300;1,300&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            min-height: 100vh;
            font-family: 'Cormorant Garamond', serif;
            overflow: hidden;
        }

        .bg {
            position: fixed;
            inset: 0;
            background-image: url('css/bg.png');
            background-size: cover;
            background-position: center;
            animation: slowZoom 30s ease-in-out infinite alternate;
            z-index: 0;
        }

        @keyframes slowZoom {
            from { transform: scale(1); }
            to   { transform: scale(1.06); }
        }

        .overlay {
            position: fixed;
            inset: 0;
            background: linear-gradient(
                to bottom,
                rgba(10,15,30,0.1) 0%,
                rgba(10,15,30,0.05) 40%,
                rgba(10,15,30,0.6) 100%
            );
            z-index: 1;
        }

        .content {
            position: relative;
            z-index: 2;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 2.5rem 3rem;
        }

        /* Top bar */
        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            animation: fadeDown 1s ease both;
        }

        @keyframes fadeDown {
            from { opacity: 0; transform: translateY(-20px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        .site-name {
            font-family: 'Cinzel', serif;
            font-size: 0.85rem;
            letter-spacing: 0.3em;
            color: rgba(255,255,255,0.6);
        }

        .logout-btn {
            font-family: 'Cinzel', serif;
            font-size: 0.75rem;
            letter-spacing: 0.2em;
            color: rgba(255,255,255,0.6);
            text-decoration: none;
            border: 1px solid rgba(255,255,255,0.25);
            padding: 0.5rem 1.2rem;
            border-radius: 2px;
            backdrop-filter: blur(6px);
            background: rgba(255,255,255,0.05);
            transition: all 0.3s ease;
        }

        .logout-btn:hover {
            color: white;
            border-color: rgba(255,255,255,0.6);
            background: rgba(255,255,255,0.12);
        }

        /* Hero */
        .hero {
            text-align: center;
            animation: fadeUp 1.2s ease 0.3s both;
        }

        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(30px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        .greeting {
            font-style: italic;
            font-size: 1.2rem;
            color: rgba(255,240,200,0.75);
            letter-spacing: 0.15em;
            margin-bottom: 0.5rem;
        }

        .welcome-text {
            font-family: 'Cinzel', serif;
            font-size: clamp(2.5rem, 6vw, 5rem);
            color: white;
            letter-spacing: 0.08em;
            text-shadow: 0 2px 40px rgba(120,180,255,0.4);
        }

        /* Email badge */
        .email-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.6rem;
            margin-top: 1.8rem;
            padding: 0.6rem 1.6rem;
            background: rgba(255,255,255,0.08);
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 100px;
            backdrop-filter: blur(12px);
            animation: fadeUp 1.2s ease 0.6s both;
        }

        .dot {
            width: 7px;
            height: 7px;
            border-radius: 50%;
            background: #7dd4a8;
            box-shadow: 0 0 8px #7dd4a8;
            animation: pulse 2s ease-in-out infinite;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; transform: scale(1); }
            50%       { opacity: 0.5; transform: scale(0.8); }
        }

        .email-text {
            font-size: 1.05rem;
            font-weight: 300;
            color: rgba(255,255,255,0.9);
            letter-spacing: 0.05em;
        }

        /* Bottom */
        .bottom {
            text-align: center;
            font-style: italic;
            font-size: 0.9rem;
            color: rgba(255,255,255,0.3);
            letter-spacing: 0.1em;
            animation: fadeUp 1.2s ease 0.9s both;
        }
    </style>
</head>
<body>
    <div class="bg"></div>
    <div class="overlay"></div>

    <div class="content">

        <div class="topbar">
            <span class="site-name">MY APP</span>
            <a href="LogoutServlet" class="logout-btn">Logout</a>
        </div>

        <div class="hero">
            <p class="greeting">you have arrived,</p>
            <h1 class="welcome-text">Welcome Back</h1>
            <div class="email-badge">
                <span class="dot"></span>
                <span class="email-text"><%= user %></span>
            </div>
        </div>

        <div class="bottom">
            <%= new java.util.Date() %>
        </div>

    </div>
</body>
</html>