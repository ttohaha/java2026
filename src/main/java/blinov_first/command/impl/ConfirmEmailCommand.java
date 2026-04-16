package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.service.impl.UserServiceImpl;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfirmEmailCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(ConfirmEmailCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String token = request.getParameter(AttributeName.TOKEN);

        if (token == null || token.isEmpty()) {
            LOGGER.warn("Confirmation attempt with empty token");
            request.setAttribute(AttributeName.CONFIRMATION_STATUS, "invalid_token");
            return PagePath.CONFIRM_ERROR;
        }

        try {
            boolean confirmed = UserServiceImpl.getInstance().confirmRegistration(token);

            if (confirmed) {
                LOGGER.info("User successfully confirmed with token: {}",
                        token.substring(0, Math.min(8, token.length())) + "...");
                request.setAttribute(AttributeName.CONFIRMATION_STATUS, "success");
                return PagePath.CONFIRM_SUCCESS;
            } else {
                LOGGER.warn("Confirmation failed for token: {}",
                        token.substring(0, Math.min(8, token.length())) + "...");
                request.setAttribute(AttributeName.CONFIRMATION_STATUS, "already_confirmed");
                return PagePath.CONFIRM_ERROR;
            }

        } catch (ServiceException e) {
            LOGGER.error("Confirmation processing error for token: {}",
                    token.substring(0, Math.min(8, token.length())) + "...", e);
            request.setAttribute(AttributeName.CONFIRMATION_STATUS, "invalid_token");
            return PagePath.CONFIRM_ERROR;
        }
    }
}