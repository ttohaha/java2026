package blinov_first.service.impl;

import blinov_first.config.MailProperties;
import blinov_first.exception.ServiceException;
import blinov_first.service.MailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends emails using Spring's {@link JavaMailSender}.
 * Replaces the manual {@code javax.mail} / {@code jakarta.mail} setup
 * and {@code MailServiceFactory}.
 *
 * PATTERN — Singleton: @Service — one instance per context.
 * PATTERN — Factory Method: bean is created by AppConfig / Spring Boot
 *            auto-configuration via {@code spring.mail.*} properties.
 *
 * When {@code app.mail.dev-mode=true} the email body is only logged
 * (useful for development without an SMTP server).
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger LOGGER = LogManager.getLogger(MailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final MailProperties props;

    public MailServiceImpl(JavaMailSender mailSender, MailProperties props) {
        this.mailSender = mailSender;
        this.props      = props;
    }

    @Override
    public void sendConfirmationEmail(String toAddress, String token) throws ServiceException {
        String link = props.getConfirmationBaseUrl() + token;

        if (props.isDevMode()) {
            LOGGER.info("[DEV] Confirmation email for '{}': {}", toAddress, link);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(props.getFromName() + " <" + props.getFromAddress() + ">");
            message.setTo(toAddress);
            message.setSubject(props.getConfirmationSubject());
            message.setText(
                    "Thank you for registering!\n\n" +
                    "Please click the link below to confirm your email address:\n\n" +
                    link + "\n\n" +
                    "If you did not create an account, please ignore this email.");

            mailSender.send(message);
            LOGGER.info("Confirmation email sent to '{}'", toAddress);
        } catch (MailException e) {
            LOGGER.error("Failed to send confirmation email to '{}'", toAddress, e);
            throw new ServiceException("Failed to send confirmation email", e);
        }
    }
}
