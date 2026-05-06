package blinov_first.command;

import blinov_first.command.impl.*;

public enum CommandType {
    ADD_USER(new AddUserCommand()),
    LOGIN(new LoginCommand()),
    LOGOUT(new LogoutCommand()),
    CONFIRM_EMAIL(new ConfirmEmailCommand()),
    EDIT_PROFILE(new EditProfileCommand()),
    LIST_ENTRIES(new ListEntriesCommand()),
    ADD_ENTRY(new AddEntryCommand()),
    EDIT_ENTRY(new EditEntryCommand()),
    DELETE_ENTRY(new DeleteEntryCommand()),
    SEARCH_ENTRIES(new SearchEntriesCommand()),
    UPLOAD_FILE(new UploadFileCommand()),
    LIST_FILES(new ListFilesCommand()),
    DOWNLOAD_FILE(new DownloadFileCommand()),
    DELETE_FILE(new DeleteFileCommand()),
    CHANGE_LOCALE(new ChangeLocaleCommand()),
    DEFAULT(new DefaultCommand());

    private final Command command;

    CommandType(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public static Command define(String commandStr) {
        if (commandStr == null || commandStr.isBlank()) {
            return DEFAULT.command;
        }
        try {
            return CommandType.valueOf(commandStr.toUpperCase()).command;
        } catch (IllegalArgumentException e) {
            return DEFAULT.command;
        }
    }
}
