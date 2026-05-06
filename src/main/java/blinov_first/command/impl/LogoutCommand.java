package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.util.AttributeName;
import blinov_first.util.CookieUtil;
import blinov_first.util.PagePath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutCommand implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        HttpServletResponse response =
                (HttpServletResponse) request.getAttribute(AttributeName.HTTP_RESPONSE);

        if (response != null) {
            CookieUtil.delete(response, CookieUtil.COOKIE_LAST_PAGE);
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:" + PagePath.INDEX;
    }
}
