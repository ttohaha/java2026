import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Standalone SMTP test for Gmail.
 * Run via: mvn exec:java -Dexec.mainClass="EmailTest" -Dexec.classpathScope="test"
 * Or run directly from IDE.
 */
@SuppressWarnings({"SpellCheckingInspection"})
public class EmailTest {

    private static final String TEST_RECIPIENT = "ttohahamail@gmail.com";
    private static final String CONFIG_FILE = "mail.properties";

    public static void main(String[] args) {
        System.out.println("=== Starting SMTP Test ===");

        try {
            Properties config = loadConfig();
            Properties sessionProps = createSessionProperties(config);

            String username = config.getProperty("mail.username");
            String password = resolvePassword(config);
            String fromAddress = config.getProperty("mail.from.address");
            String fromName = config.getProperty("mail.from.name");

            System.out.println("Connecting to: " + config.getProperty("mail.host") + ":" + config.getProperty("mail.port"));
            System.out.println("From: " + fromAddress);
            System.out.println("To: " + TEST_RECIPIENT);

            Session session = Session.getInstance(sessionProps);
            session.setDebug(true); // Выводит детальный SMTP-лог в консоль

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress, fromName, StandardCharsets.UTF_8.name()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TEST_RECIPIENT));
            message.setSubject("SMTP Test - " + System.currentTimeMillis(), StandardCharsets.UTF_8.name());
            message.setText("This is a test email from your Java app.\n\nIf you see this, SMTP is working!", "UTF-8");

            try (Transport transport = session.getTransport("smtp")) {
                transport.connect(username, password);
                transport.sendMessage(message, message.getAllRecipients());
                System.out.println("✅ SUCCESS: Email sent to " + TEST_RECIPIENT);
            }

        } catch (javax.net.ssl.SSLHandshakeException e) {
            System.err.println("❌ SSL Error: Certificate validation failed");
            System.err.println("   Fix: Add 'mail.smtp.ssl.trust=smtp.gmail.com' to mail.properties");
            e.printStackTrace();
        } catch (java.net.UnknownHostException e) {
            System.err.println("❌ Network Error: Host not resolvable");
            System.err.println("   Fix: Check DNS, proxy, firewall. Try: ping smtp.gmail.com");
            e.printStackTrace();
        } catch (jakarta.mail.AuthenticationFailedException e) {
            System.err.println("❌ Auth Error: Invalid credentials");
            System.err.println("   Fix: Use 16-digit App Password, not your main Gmail password");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Unexpected error:");
            e.printStackTrace();
        }

        System.out.println("=== Test Finished ===");
    }

    private static Properties loadConfig() throws Exception {
        Properties props = new Properties();
        try (InputStream input = EmailTest.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new Exception("Config file '" + CONFIG_FILE + "' not found in classpath");
            }
            props.load(input);
        }
        return props;
    }

    private static Properties createSessionProperties(Properties config) {
        Properties props = new Properties();
        props.put("mail.smtp.host", config.getProperty("mail.host"));
        props.put("mail.smtp.port", config.getProperty("mail.port"));
        props.put("mail.smtp.auth", config.getProperty("mail.auth"));
        props.put("mail.smtp.starttls.enable", config.getProperty("mail.starttls.enable"));
        props.put("mail.transport.protocol", config.getProperty("mail.protocol"));
        // Ключевая настройка для разработки: доверять сертификату Gmail
        props.put("mail.smtp.ssl.trust", config.getProperty("mail.host"));
        return props;
    }

    private static String resolvePassword(Properties config) {
        String envPassword = System.getenv("SMTP_PASSWORD");
        return (envPassword != null) ? envPassword : config.getProperty("mail.password");
    }
}