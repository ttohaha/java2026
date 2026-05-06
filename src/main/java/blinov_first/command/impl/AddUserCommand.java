package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.service.impl.UserServiceImpl;
import blinov_first.telegram.TelegramBotConfig;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddUserCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(AddUserCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String login    = request.getParameter(AttributeName.LOGIN);
        String password = request.getParameter(AttributeName.PASSWORD);
        String email    = request.getParameter(AttributeName.EMAIL);

        try {
            String token = UserServiceImpl.getInstance()
                    .registerAndGetToken(login, password, email);

            if (token != null) {
                TelegramBotConfig tgConfig = TelegramBotConfig.getInstance();
                request.setAttribute(AttributeName.REGISTERED_LOGIN,  login);
                request.setAttribute(AttributeName.TG_BOT_CONFIGURED, tgConfig.isConfigured());
                request.setAttribute(AttributeName.TG_BOT_USERNAME,   tgConfig.getBotUsername());
                request.setAttribute("confirmationToken",              token);
                return PagePath.PENDING_CONFIRMATION;
            }

            request.setAttribute(AttributeName.ERROR_MSG,
                    "Registration failed: login exists or invalid data");
            return PagePath.REGISTRATION;

        } catch (ServiceException e) {
            LOGGER.error("Registration error for user: {}", login, e);
            request.setAttribute(AttributeName.ERROR_MSG,
                    "Registration error: " + e.getMessage());
            return PagePath.REGISTRATION;
        }
    }
}
