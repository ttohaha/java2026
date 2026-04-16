package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.util.PagePath;
import blinov_first.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LogoutCommand implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return PagePath.INDEX;
    }
}