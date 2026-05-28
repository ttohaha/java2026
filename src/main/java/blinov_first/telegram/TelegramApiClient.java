package blinov_first.telegram;

import blinov_first.config.TelegramProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Low-level HTTP client for the Telegram Bot API.
 *
 * Replaces the old static utility class; now managed as a Spring bean
 * so {@link TelegramProperties} can be injected rather than read from
 * a separate singleton.
 *
 * PATTERN — Singleton: @Component — one instance per Spring context.
 */
@Component
public class TelegramApiClient {

    private static final Logger LOGGER = LogManager.getLogger(TelegramApiClient.class);

    private static final String BASE_URL = "https://api.telegram.org/bot";

    private final TelegramProperties props;

    public TelegramApiClient(TelegramProperties props) {
        this.props = props;
    }

    // ----------------------------------------------------------------
    // API methods
    // ----------------------------------------------------------------

    public JSONObject getUpdates(int offset, int timeout) {
        String url = BASE_URL + props.getBotToken()
                + "/getUpdates?offset=" + offset
                + "&timeout=" + timeout;
        return get(url);
    }

    public JSONObject sendMessage(String chatId, String text) {
        String url  = BASE_URL + props.getBotToken() + "/sendMessage";
        String body = new JSONObject()
                .put("chat_id", chatId)
                .put("text", text)
                .toString();
        return post(url, body);
    }

    // ----------------------------------------------------------------
    // HTTP helpers
    // ----------------------------------------------------------------

    private JSONObject get(String urlStr) {
        try {
            HttpURLConnection conn = openConnection(urlStr, "GET");
            return readResponse(conn);
        } catch (IOException e) {
            LOGGER.error("Telegram GET failed: {}", urlStr, e);
            return null;
        }
    }

    private JSONObject post(String urlStr, String jsonBody) {
        try {
            HttpURLConnection conn = openConnection(urlStr, "POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
            return readResponse(conn);
        } catch (IOException e) {
            LOGGER.error("Telegram POST failed: {}", urlStr, e);
            return null;
        }
    }

    private HttpURLConnection openConnection(String urlStr, String method) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(10_000);
        conn.setReadTimeout((props.getPollingTimeout() + 5) * 1_000);
        return conn;
    }

    private JSONObject readResponse(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        var stream = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        }
    }
}
