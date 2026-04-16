package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.util.PagePath;
import blinov_first.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;

public class DefaultCommand implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        return PagePath.INDEX;
    }
}