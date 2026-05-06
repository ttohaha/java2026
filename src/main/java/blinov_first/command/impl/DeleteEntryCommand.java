package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.PhoneEntryServiceFactory;
import blinov_first.util.AjaxUtil;
import blinov_first.util.AttributeName;
import blinov_first.util.FlashMessage;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class DeleteEntryCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(DeleteEntryCommand.class);

    private static final String MSG_DELETED      = "Contact deleted successfully";
    private static final String MSG_NOT_FOUND    = "Contact not found or access denied";
    private static final String MSG_INVALID_ID   = "Invalid contact identifier";
    private static final String MSG_DELETE_ERROR = "Failed to delete contact";

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) {
            return respondUnauthorized(request);
        }

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) {
            return respondUnauthorized(request);
        }

        String entryIdStr = request.getParameter(AttributeName.ENTRY_ID);
        if (entryIdStr == null || entryIdStr.isBlank()) {
            return respondBadRequest(request, MSG_INVALID_ID);
        }

        try {
            long entryId = Long.parseLong(entryIdStr);
            boolean deleted = PhoneEntryServiceFactory.getPhoneEntryService()
                    .deleteEntry(entryId, userId);

            if (AjaxUtil.isAjax(request)) {
                return respondAjax(request, deleted);
            }

            if (deleted) {
                FlashMessage.success(request, MSG_DELETED);
            } else {
                FlashMessage.error(request, MSG_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid entryId parameter: '{}'", entryIdStr);
            if (AjaxUtil.isAjax(request)) {
                return respondAjaxError(request, HttpServletResponse.SC_BAD_REQUEST, MSG_INVALID_ID);
            }
            FlashMessage.error(request, MSG_INVALID_ID);
        } catch (ServiceException e) {
            LOGGER.error("Failed to delete entry for user={}", userId, e);
            if (AjaxUtil.isAjax(request)) {
                return respondAjaxError(request, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        MSG_DELETE_ERROR);
            }
            FlashMessage.error(request, MSG_DELETE_ERROR);
        }

        return "redirect:/controller?command=list_entries";
    }

    private String respondUnauthorized(HttpServletRequest request) {
        if (!AjaxUtil.isAjax(request)) {
            return PagePath.INDEX;
        }
        return respondAjaxError(request, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    private String respondBadRequest(HttpServletRequest request, String message) {
        if (!AjaxUtil.isAjax(request)) {
            return "redirect:/controller?command=list_entries";
        }
        return respondAjaxError(request, HttpServletResponse.SC_BAD_REQUEST, message);
    }

    private String respondAjax(HttpServletRequest request, boolean deleted) {
        HttpServletResponse response =
                (HttpServletResponse) request.getAttribute(AttributeName.HTTP_RESPONSE);
        if (response == null) {
            return PagePath.AJAX_HANDLED;
        }
        try {
            if (deleted) {
                AjaxUtil.writeSuccess(response, MSG_DELETED);
            } else {
                AjaxUtil.writeError(response, HttpServletResponse.SC_NOT_FOUND, MSG_NOT_FOUND);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write AJAX delete response", e);
        }
        return PagePath.AJAX_HANDLED;
    }

    private String respondAjaxError(HttpServletRequest request, int status, String message) {
        HttpServletResponse response =
                (HttpServletResponse) request.getAttribute(AttributeName.HTTP_RESPONSE);
        if (response != null) {
            try {
                AjaxUtil.writeError(response, status, message);
            } catch (IOException e) {
                LOGGER.error("Failed to write AJAX error response", e);
            }
        }
        return PagePath.AJAX_HANDLED;
    }
}
