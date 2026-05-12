package database;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    // ── Gmail credentials ──────────────────────────────────────────
    private static final String SENDER_EMAIL    = "youremail@gmail.com";
    private static final String SENDER_PASSWORD = "your_app_password";  
    // NOTE: Use a Gmail App Password, not your real Gmail password.
    // Generate one at: Google Account → Security → 2-Step Verification → App Passwords
    // ──────────────────────────────────────────────────────────────

    // Creates authenticated Gmail SMTP session
    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            "smtp.gmail.com");
        props.put("mail.smtp.port",            "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
    }

    // Core send method — all other methods call this
    private static void sendEmail(String toEmail, String subject, String body) {
        try {
            Session session = createSession();
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");  // HTML emails

            Transport.send(message);
            System.out.println("Email sent to: " + toEmail);

        } catch (MessagingException e) {
            System.out.println("Email send failed: " + e.getMessage());
        }
    }

    // ── 1. OTP for Forgot Password ─────────────────────────────────
    public static void sendOTP(String toEmail, String otp) {
        String subject = "Your Password Reset OTP";
        String body = 
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;'>" +
            "<h2 style='color:#3a86ff;'>Password Reset Request</h2>" +
            "<p>You requested to reset your password. Use the OTP below:</p>" +
            "<div style='font-size:36px;font-weight:bold;letter-spacing:10px;" +
                 "background:#f0f4ff;padding:20px;text-align:center;border-radius:8px;" +
                 "color:#3a86ff;margin:20px 0;'>" + otp + "</div>" +
            "<p>This OTP is valid for <b>10 minutes</b>. Do not share it with anyone.</p>" +
            "<p style='color:#999;font-size:12px;'>If you didn't request this, ignore this email.</p>" +
            "</div>";
        sendEmail(toEmail, subject, body);
    }

    // ── 2. Alert: Someone tried to log into your account ──────────
    public static void sendLoginAttemptAlert(String toEmail) {
        String subject = "⚠️ New Login Attempt Detected";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;'>" +
            "<h2 style='color:#ff6b35;'>Login Attempt Detected</h2>" +
            "<p>Someone just tried to log into your account.</p>" +
            "<p>If this was <b>you</b>, no action is needed.</p>" +
            "<p>If this was <b>not you</b>, your account may be at risk. " +
            "<a href='#' style='color:#ff6b35;'>Reset your password immediately.</a></p>" +
            "<p style='color:#999;font-size:12px;'>Time: " + new java.util.Date() + "</p>" +
            "</div>";
        sendEmail(toEmail, subject, body);
    }

    // ── 3. Alert: Confirm it's really you trying to log in ────────
    public static void sendLoginConfirmation(String toEmail) {
        String subject = "✅ Are You Trying to Log In?";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;'>" +
            "<h2 style='color:#2ec4b6;'>Login Confirmation</h2>" +
            "<p>A login to your account was just made.</p>" +
            "<p>If this was you, you can safely ignore this email.</p>" +
            "<p>If you did <b>not</b> log in, " +
            "<a href='#' style='color:#e63946;'>click here to secure your account.</a></p>" +
            "<p style='color:#999;font-size:12px;'>Time: " + new java.util.Date() + "</p>" +
            "</div>";
        sendEmail(toEmail, subject, body);
    }

    // ── 4. Alert: Password was changed ────────────────────────────
    public static void sendPasswordChangedAlert(String toEmail) {
        String subject = "🔐 Your Password Was Changed";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;'>" +
            "<h2 style='color:#e63946;'>Password Changed</h2>" +
            "<p>Your account password was just changed successfully.</p>" +
            "<p>If you made this change, no action is needed.</p>" +
            "<p>If you did <b>not</b> change your password, someone may have access " +
            "to your account. <a href='#' style='color:#e63946;'>Contact support immediately.</a></p>" +
            "<p style='color:#999;font-size:12px;'>Time: " + new java.util.Date() + "</p>" +
            "</div>";
        sendEmail(toEmail, subject, body);
    }

    // ── 5. Alert: Account successfully registered ─────────────────
    public static void sendWelcomeEmail(String toEmail) {
        String subject = "🎉 Welcome! Your Account is Ready";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;'>" +
            "<h2 style='color:#3a86ff;'>Account Created Successfully</h2>" +
            "<p>Your account has been registered with this email address.</p>" +
            "<p>If you did <b>not</b> create this account, " +
            "<a href='#' style='color:#e63946;'>report it here.</a></p>" +
            "<p style='color:#999;font-size:12px;'>Time: " + new java.util.Date() + "</p>" +
            "</div>";
        sendEmail(toEmail, subject, body);
    }
}