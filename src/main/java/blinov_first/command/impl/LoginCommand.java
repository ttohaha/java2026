package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.User;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.service.impl.UserServiceImpl;
import blinov_first.util.AttributeName;
import blinov_first.util.CookieUtil;
import blinov_first.util.PagePath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class LoginCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(LoginCommand.class);

    private static final String PARAM_REMEMBER_ME = "rememberMe";

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String login      = request.getParameter(AttributeName.LOGIN);
        String password   = request.getParameter(AttributeName.PASSWORD);
        String rememberMe = request.getParameter(PARAM_REMEMBER_ME);

        HttpServletResponse response =
                (HttpServletResponse) request.getAttribute(AttributeName.HTTP_RESPONSE);

        try {
            if (UserServiceImpl.getInstance().authenticate(login, password)) {
                HttpSession session = request.getSession(true);
                session.setAttribute(AttributeName.LOGIN, login);

                Long userId = fetchUserId(login);
                if (userId != null) {
                    session.setAttribute(AttributeName.USER_ID, userId);
                    LOGGER.info("Session created for user: {} (id={})", login, userId);
                }

                if (response != null) {
                    if ("on".equals(rememberMe)) {
                        CookieUtil.writeRememberLogin(response, login);
                    } else {
                        CookieUtil.delete(response, CookieUtil.COOKIE_REMEMBER_LOGIN);
                    }
                }

                String lastPage = CookieUtil.read(request, CookieUtil.COOKIE_LAST_PAGE)
                        .filter(p -> !p.isBlank())
                        .orElse(null);

                List<User> userList = UserServiceImpl.getInstance().findAllUsers();
                request.setAttribute(AttributeName.USER_LIST, userList);

                if (lastPage != null && response != null) {
                    CookieUtil.delete(response, CookieUtil.COOKIE_LAST_PAGE);
                    return "redirect:" + lastPage;
                }

                return PagePath.MAIN;
            }

            request.setAttribute(AttributeName.ERROR_MSG, "Invalid credentials");
            return PagePath.INDEX;

        } catch (ServiceException e) {
            LOGGER.error("Authentication error for user: {}", login, e);
            throw new CommandException("Login failed", e);
        }
    }

    private Long fetchUserId(String login) {
        try {
            Optional<User> userOpt = blinov_first.dao.impl.UserDaoImpl.getInstance().findByLogin(login);
            return userOpt.map(User::getId).orElse(null);
        } catch (blinov_first.exception.DaoException e) {
            LOGGER.warn("Failed to fetch user id for login: {}", login, e);
            return null;
        }
    }
}
