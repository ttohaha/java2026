package blinov_first.service.impl;

import blinov_first.entity.User;
import blinov_first.exception.ServiceException;
import blinov_first.service.MailService;
import com.sun.mail.util.MailConnectException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@SuppressWarnings({"SpellCheckingInspection"})
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LogManager.getLogger(MailServiceImpl.class);

    private final Properties sessionProperties;
    private final String smtpUsername;
    private final String smtpPassword;
    private final String fromAddress;
    private final String fromName;
    private final String confirmationBaseUrl;
    private final String confirmationSubject;
    private final boolean devMode;

    public MailServiceImpl(Properties config) {
        this.sessionProperties = createSessionProperties(config);
        this.smtpUsername = config.getProperty("mail.username");
        this.smtpPassword = resolvePassword(config);
        this.fromAddress = config.getProperty("mail.from.address");
        this.fromName = config.getProperty("mail.from.name");
        this.confirmationBaseUrl = config.getProperty("mail.confirmation.base.url");
        this.confirmationSubject = config.getProperty("mail.confirmation.subject");
        this.devMode = "true".equalsIgnoreCase(config.getProperty("mail.dev.mode"));
    }

    private String resolvePassword(Properties config) {
        String envPassword = System.getenv("SMTP_PASSWORD");
        return (envPassword != null) ? envPassword : config.getProperty("mail.password");
    }

    private Properties createSessionProperties(Properties config) {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getProperty("mail.host"));
        props.put("mail.smtp.port", config.getProperty("mail.port"));
        props.put("mail.smtp.auth", config.getProperty("mail.auth"));
        props.put("mail.smtp.starttls.enable", config.getProperty("mail.starttls.enable"));
        props.put("mail.transport.protocol", config.getProperty("mail.protocol"));
        // === FIX: Добавляем доверие к сертификату ===
        String sslTrust = config.getProperty("mail.smtp.ssl.trust");
        if (sslTrust != null && !sslTrust.isEmpty()) {
            props.put("mail.smtp.ssl.trust", sslTrust);
        }
        // ===========================================
        return props;
    }

    @Override
    public void sendConfirmationEmail(User user, String token) throws ServiceException {
        validateParameters(user, token);

        if (devMode) {
            logDevModeInfo(user, token);
            return;
        }

        validateCredentials();

        String confirmationUrl = confirmationBaseUrl + token;
        String content = buildEmailContent(user.getLogin(), confirmationUrl);

        try {
            Session session = Session.getInstance(sessionProperties);
            MimeMessage message = new MimeMessage(session);

            try {
                message.setFrom(new InternetAddress(fromAddress, fromName, StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Failed to encode from address: {} ({})", fromAddress, fromName, e);
                throw new ServiceException("Invalid email sender configuration", e);
            }

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject(confirmationSubject, StandardCharsets.UTF_8.name());
            message.setContent(content, "text/html; charset=UTF-8");

            try (Transport transport = session.getTransport("smtp")) {
                transport.connect(smtpUsername, smtpPassword);
                transport.sendMessage(message, message.getAllRecipients());
                LOGGER.info("Confirmation email sent to {}", user.getEmail());
            }
        } catch (MailConnectException e) {
            handleConnectionError(e, user, token);
        } catch (MessagingException e) {
            throw new ServiceException("Email delivery failed", e);
        }
    }

    private void validateParameters(User user, String token) throws ServiceException {
        if (user == null || token == null || user.getEmail() == null) {
            LOGGER.error("Invalid parameters for sending confirmation email");
            throw new ServiceException("Cannot send email: invalid parameters");
        }
    }

    private void validateCredentials() throws ServiceException {
        if (smtpUsername == null || smtpUsername.isEmpty() || smtpPassword == null || smtpPassword.isEmpty()) {
            LOGGER.error("SMTP credentials are not configured");
            throw new ServiceException("SMTP credentials missing");
        }
    }

    private void logDevModeInfo(User user, String token) {
        LOGGER.info("[DEV MODE] Confirmation email would be sent to: {}", user.getEmail());
        LOGGER.info("[DEV MODE] Confirmation token for user '{}': {}", user.getLogin(), token);
    }

    private void handleConnectionError(MailConnectException e, User user, String token) throws ServiceException {
        if (e.getCause() instanceof UnknownHostException) {
            LOGGER.error("SMTP host '{}' is unreachable. Check DNS/proxy/firewall settings.",
                    sessionProperties.getProperty("mail.smtp.host"));
            LOGGER.info("Confirmation token for user '{}': {}", user.getLogin(), token);
        }
        throw new ServiceException("Failed to connect to mail server", e);
    }

    private String buildEmailContent(String login, String confirmationUrl) {
        return "<html><body>" +
                "<h2>Welcome, " + escapeHtml(login) + "!</h2>" +
                "<p>Please confirm your registration by clicking the link below:</p>" +
                "<p><a href=\"" + escapeHtml(confirmationUrl) + "\">Confirm Registration</a></p>" +
                "<p>If you did not register, please ignore this email.</p>" +
                "</body></html>";
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}