package blinov_first.telegram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TelegramApiClient {

    private static final Logger LOGGER = LogManager.getLogger(TelegramApiClient.class);

    private static final String API_BASE        = "https://api.telegram.org/bot";
    private static final String METHOD_UPDATES  = "/getUpdates";
    private static final String METHOD_SEND     = "/sendMessage";

    private static final int HTTP_OK            = 200;
    private static final int CONNECT_TIMEOUT_S  = 10;

    private final String      token;
    private final HttpClient  httpClient;

    public TelegramApiClient(String token) {
        this.token      = token;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_S))
                .build();
    }

    public List<TelegramUpdate> getUpdates(long offset, int timeout) {
        String url = API_BASE + token + METHOD_UPDATES
                + "?offset=" + offset
                + "&timeout=" + timeout
                + "&allowed_updates=message";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(timeout + CONNECT_TIMEOUT_S))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HTTP_OK) {
                LOGGER.warn("getUpdates returned status {}", response.statusCode());
                return List.of();
            }

            return parseUpdates(response.body());

        } catch (IOException | InterruptedException e) {
            LOGGER.debug("getUpdates interrupted or failed: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return List.of();
        }
    }

    public void sendMessage(long chatId, String text) {
        String url  = API_BASE + token + METHOD_SEND;
        String body = buildSendMessageBody(chatId, text);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(CONNECT_TIMEOUT_S))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != HTTP_OK) {
                LOGGER.warn("sendMessage returned status {}", response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            LOGGER.error("sendMessage failed for chatId={}", chatId, e);
            Thread.currentThread().interrupt();
        }
    }

    private List<TelegramUpdate> parseUpdates(String json) {
        List<TelegramUpdate> updates = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            if (!root.optBoolean("ok", false)) {
                return updates;
            }
            JSONArray result = root.optJSONArray("result");
            if (result == null) {
                return updates;
            }
            for (int i = 0; i < result.length(); i++) {
                JSONObject item    = result.getJSONObject(i);
                long       updateId = item.getLong("update_id");
                JSONObject message = item.optJSONObject("message");
                if (message == null) {
                    updates.add(new TelegramUpdate(updateId, 0L, null));
                    continue;
                }
                JSONObject from   = message.optJSONObject("chat");
                long       chatId = (from != null) ? from.optLong("id", 0L) : 0L;
                String     text   = message.optString("text", null);
                updates.add(new TelegramUpdate(updateId, chatId, text));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to parse Telegram updates JSON", e);
        }
        return updates;
    }

    private String buildSendMessageBody(long chatId, String text) {
        return "{\"chat_id\":" + chatId
                + ",\"text\":\"" + escapeJson(text) + "\""
                + ",\"parse_mode\":\"HTML\"}";
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
