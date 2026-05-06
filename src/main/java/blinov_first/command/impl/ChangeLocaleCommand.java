package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.util.AttributeName;
import blinov_first.util.CookieUtil;
import blinov_first.util.PagePath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Set;

public class ChangeLocaleCommand implements Command {

    private static final Set<String> SUPPORTED_LOCALES = Set.of("en", "ru");
    private static final String DEFAULT_LOCALE         = "en";
    private static final String PARAM_LANG             = "lang";
    private static final String SESSION_LANG           = "lang";

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String lang = request.getParameter(PARAM_LANG);

        if (lang == null || !SUPPORTED_LOCALES.contains(lang.toLowerCase())) {
            lang = DEFAULT_LOCALE;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_LANG, lang.toLowerCase());

        HttpServletResponse response =
                (HttpServletResponse) request.getAttribute(AttributeName.HTTP_RESPONSE);
        if (response != null) {
            CookieUtil.writeLang(response, lang.toLowerCase());
        }

        boolean isLoggedIn = session.getAttribute(AttributeName.USER_ID) != null;
        return "redirect:" + (isLoggedIn ? PagePath.PHONE_BOOK : PagePath.INDEX);
    }
   }