package blinov_first.telegram;

import org.json.JSONObject;

/**
 * Immutable value object parsed from a single Telegram update JSON payload.
 */
public class TelegramUpdate {

    private final int    updateId;
    private final String chatId;
    private final String text;
    private final String username;

    private TelegramUpdate(int updateId, String chatId, String text, String username) {
        this.updateId = updateId;
        this.chatId   = chatId;
        this.text     = text;
        this.username = username;
    }

    /**
     * Parses a single update object returned by Telegram's getUpdates API.
     *
     * @param json one element from the {@code result} array
     * @return parsed update, with null fields when data is absent
     */
    public static TelegramUpdate from(JSONObject json) {
        int    updateId = json.optInt("update_id", 0);
        String chatId   = null;
        String text     = null;
        String username = null;

        if (json.has("message")) {
            JSONObject message = json.getJSONObject("message");
            text = message.optString("text", null);

            if (message.has("chat")) {
                chatId = String.valueOf(message.getJSONObject("chat").optLong("id", 0));
            }
            if (message.has("from")) {
                username = message.getJSONObject("from").optString("username", null);
            }
        }

        return new TelegramUpdate(updateId, chatId, text, username);
    }

    public int    getUpdateId() { return updateId; }
    public String getChatId()   { return chatId; }
    public String getText()     { return text; }
    public String getUsername() { return username; }
}
