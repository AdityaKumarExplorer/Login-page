package database;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EmailService - Handles all email notifications for the application.
 * Uses Gmail SMTP with App Password authentication.
 * 
 * SETUP INSTRUCTIONS:
 * 1. Enable 2-Step Verification on your Gmail account
 * 2. Generate an App Password at: Google Account → Security → App Passwords
 * 3. Replace SENDER_EMAIL and SENDER_PASSWORD with your credentials
 * 4. Maven dependency: <artifactId>jakarta.mail</artifactId>
 */
public class EmailService {

    // ── Gmail credentials ──────────────────────────────────────────
    private static final String SENDER_EMAIL    = "unknownducktest@gmail.com";
    private static final String SENDER_PASSWORD = "qwcl ewem kevg irxn";  
    private static final String SENDER_NAME     = "Login System";
    // NOTE: Use a Gmail App Password, not your real Gmail password.
    // Generate one at: Google Account → Security → 2-Step Verification → App Passwords
    // ──────────────────────────────────────────────────────────────

    /**
     * Creates an authenticated Gmail SMTP session.
     * 
     * @return Session configured for Gmail SMTP
     */
    private static Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth",                "true");
        props.put("mail.smtp.starttls.enable",     "true");
        props.put("mail.smtp.starttls.required",   "true");
        props.put("mail.smtp.host",                "smtp.gmail.com");
        props.put("mail.smtp.port",                "587");
        props.put("mail.smtp.connectiontimeout",   "5000");
        props.put("mail.smtp.timeout",             "5000");
        props.put("mail.smtp.writetimeout",        "5000");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
    }

    /**
     * Core send method — all other methods call this.
     * Handles all SMTP communication and error handling.
     * 
     * @param toEmail   Recipient's email address
     * @param subject   Email subject line
     * @param body      HTML email body
     * @return          true if email sent successfully, false otherwise
     */
    private static boolean sendEmail(String toEmail, String subject, String body) {
        // Input validation
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.err.println("Error: Recipient email cannot be empty");
            return false;
        }
        
        if (!isValidEmail(toEmail)) {
            System.err.println("Error: Invalid email format: " + toEmail);
            return false;
        }

        try {
            Session session = createSession();
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("[✓] Email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("[✗] Email send failed for: " + toEmail);
            System.err.println("    Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("[✗] Unexpected error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validates email format using regex.
     * 
     * @param email Email address to validate
     * @return      true if valid email format
     */
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    /**
     * Returns current timestamp in readable format.
     * 
     * @return Formatted date-time string
     */
    private static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // ── 1. OTP for Forgot Password ─────────────────────────────────
    /**
     * Sends OTP email for password reset.
     * 
     * @param toEmail Recipient's email address
     * @param otp     6-digit OTP code
     * @return        true if email sent successfully
     */
    public static boolean sendOTP(String toEmail, String otp) {
        if (otp == null || otp.trim().isEmpty()) {
            System.err.println("Error: OTP cannot be empty");
            return false;
        }

        String subject = "Your Password Reset OTP";
        String body = 
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;background:#fff;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
            "<div style='text-align:center;margin-bottom:30px;'>" +
            "<h2 style='color:#3a86ff;margin:0;'>Password Reset Request</h2>" +
            "</div>" +
            "<p style='color:#333;line-height:1.6;'>You requested to reset your password. Use the OTP below:</p>" +
            "<div style='font-size:42px;font-weight:bold;letter-spacing:12px;" +
                 "background:#f0f4ff;padding:25px;text-align:center;border-radius:8px;" +
                 "color:#3a86ff;margin:25px 0;border:2px solid #3a86ff;'>" + otp + "</div>" +
            "<p style='color:#333;line-height:1.6;'><strong>Valid for: 10 minutes</strong></p>" +
            "<p style='color:#e63946;line-height:1.6;'><strong>⚠️ Do not share this OTP with anyone.</strong></p>" +
            "<hr style='border:none;border-top:1px solid #ddd;margin:30px 0;'>" +
            "<p style='color:#999;font-size:12px;margin:0;'>If you didn't request this, please ignore this email and your password will remain unchanged.</p>" +
            "<p style='color:#999;font-size:12px;margin:10px 0 0 0;'>Sent at: " + getCurrentTimestamp() + "</p>" +
            "</div>";
        
        return sendEmail(toEmail, subject, body);
    }

    // ── 2. Alert: Someone tried to log into your account ──────────
    /**
     * Sends login attempt alert.
     * 
     * @param toEmail Recipient's email address
     * @return        true if email sent successfully
     */
    public static boolean sendLoginAttemptAlert(String toEmail) {
        String subject = "⚠️  New Login Attempt Detected";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;background:#fff;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
            "<div style='text-align:center;margin-bottom:30px;'>" +
            "<h2 style='color:#ff6b35;margin:0;'>Login Attempt Detected</h2>" +
            "</div>" +
            "<p style='color:#333;line-height:1.6;'>Someone just tried to log into your account.</p>" +
            "<p style='color:#333;line-height:1.6;'><strong>If this was YOU:</strong> No action is needed. You can close this email.</p>" +
            "<p style='color:#e63946;line-height:1.6;'><strong>If this was NOT you:</strong> Your account may be at risk. " +
            "<a href='#' style='color:#e63946;font-weight:bold;text-decoration:none;'>Reset your password immediately →</a></p>" +
            "<hr style='border:none;border-top:1px solid #ddd;margin:30px 0;'>" +
            "<p style='color:#999;font-size:12px;margin:0;'>Timestamp: " + getCurrentTimestamp() + "</p>" +
            "</div>";
        
        return sendEmail(toEmail, subject, body);
    }

    // ── 3. Alert: Confirm it's really you trying to log in ────────
    /**
     * Sends login confirmation email.
     * 
     * @param toEmail Recipient's email address
     * @return        true if email sent successfully
     */
    public static boolean sendLoginConfirmation(String toEmail) {
        String subject = "✅ Confirm Your Login";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;background:#fff;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
            "<div style='text-align:center;margin-bottom:30px;'>" +
            "<h2 style='color:#2ec4b6;margin:0;'>Login Confirmation</h2>" +
            "</div>" +
            "<p style='color:#333;line-height:1.6;'>A login to your account was just detected.</p>" +
            "<p style='color:#333;line-height:1.6;'><strong>If this was YOU:</strong> You can safely ignore this email.</p>" +
            "<p style='color:#e63946;line-height:1.6;'><strong>If you did NOT log in:</strong> " +
            "<a href='#' style='color:#e63946;font-weight:bold;text-decoration:none;'>Click here to secure your account →</a></p>" +
            "<hr style='border:none;border-top:1px solid #ddd;margin:30px 0;'>" +
            "<p style='color:#999;font-size:12px;margin:0;'>Timestamp: " + getCurrentTimestamp() + "</p>" +
            "</div>";
        
        return sendEmail(toEmail, subject, body);
    }

    // ── 4. Alert: Password was changed ────────────────────────────
    /**
     * Sends password change confirmation email.
     * 
     * @param toEmail Recipient's email address
     * @return        true if email sent successfully
     */
    public static boolean sendPasswordChangedAlert(String toEmail) {
        String subject = "🔐 Your Password Was Changed";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;background:#fff;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
            "<div style='text-align:center;margin-bottom:30px;'>" +
            "<h2 style='color:#e63946;margin:0;'>Password Changed</h2>" +
            "</div>" +
            "<p style='color:#333;line-height:1.6;'>Your account password was just changed successfully.</p>" +
            "<p style='color:#333;line-height:1.6;'><strong>If YOU made this change:</strong> No action is needed.</p>" +
            "<p style='color:#e63946;line-height:1.6;'><strong>If YOU did NOT change it:</strong> Someone may have unauthorized access to your account. " +
            "<a href='#' style='color:#e63946;font-weight:bold;text-decoration:none;'>Contact support immediately →</a></p>" +
            "<hr style='border:none;border-top:1px solid #ddd;margin:30px 0;'>" +
            "<p style='color:#999;font-size:12px;margin:0;'>Timestamp: " + getCurrentTimestamp() + "</p>" +
            "</div>";
        
        return sendEmail(toEmail, subject, body);
    }

    // ── 5. Alert: Account successfully registered ─────────────────
    /**
     * Sends welcome/registration confirmation email.
     * 
     * @param toEmail Recipient's email address
     * @return        true if email sent successfully
     */
    public static boolean sendWelcomeEmail(String toEmail) {
        String subject = "🎉 Welcome! Your Account is Ready";
        String body =
            "<div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;background:#fff;padding:20px;border:1px solid #ddd;border-radius:8px;'>" +
            "<div style='text-align:center;margin-bottom:30px;'>" +
            "<h2 style='color:#3a86ff;margin:0;'>Account Created Successfully</h2>" +
            "</div>" +
            "<p style='color:#333;line-height:1.6;'>Welcome! Your account has been registered with this email address.</p>" +
            "<p style='color:#333;line-height:1.6;'>You can now log in and start using our platform.</p>" +
            "<p style='color:#999;line-height:1.6;'><strong>Did NOT create this account?</strong> " +
            "<a href='#' style='color:#e63946;font-weight:bold;text-decoration:none;'>Report it here →</a></p>" +
            "<hr style='border:none;border-top:1px solid #ddd;margin:30px 0;'>" +
            "<p style='color:#999;font-size:12px;margin:0;'>Timestamp: " + getCurrentTimestamp() + "</p>" +
            "</div>";
        
        return sendEmail(toEmail, subject, body);
    }

    /**
     * Main method for testing (optional).
     * Remove or comment out before production deployment.
     * 
     * @param args Command-line arguments (not used)

    public static void main(String[] args) {
        System.out.println("EmailService loaded successfully.");
        System.out.println("Use this class methods to send emails:");
        System.out.println("  - sendOTP(email, otp)");
        System.out.println("  - sendLoginAttemptAlert(email)");
        System.out.println("  - sendLoginConfirmation(email)");
        System.out.println("  - sendPasswordChangedAlert(email)");
        System.out.println("  - sendWelcomeEmail(email)");

    }
     */
}