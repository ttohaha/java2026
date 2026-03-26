package blinov_first.command;

import blinov_first.command.impl.AddUserCommand;
import blinov_first.command.impl.DefaultCommand;
import blinov_first.command.impl.LoginCommand;
import blinov_first.command.impl.LogoutCommand;

public enum CommandType {
    ADD_USER(new AddUserCommand()),
    LOGIN(new LoginCommand()),
    LOGOUT(new LogoutCommand()),
    DEFAULT(new DefaultCommand());

    private final Command command;

    CommandType(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public static Command define(String commandStr) {
        if (commandStr == null || commandStr.isEmpty()) {
            return DEFAULT.command;
        }

        try {
            return CommandType.valueOf(commandStr.toUpperCase()).command;
        } catch (IllegalArgumentException e) {
            return DEFAULT.command;
        }
    }
}