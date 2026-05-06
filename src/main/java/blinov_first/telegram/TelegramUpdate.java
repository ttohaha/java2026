package blinov_first.telegram;

public class TelegramUpdate {

    private final long   updateId;
    private final long   chatId;
    private final String text;

    public TelegramUpdate(long updateId, long chatId, String text) {
        this.updateId = updateId;
        this.chatId   = chatId;
        this.text     = text;
    }

    public long getUpdateId() {
        return updateId;
    }

    public long getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }
}
