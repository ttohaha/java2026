package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.PhoneEntryServiceFactory;
import blinov_first.service.PhoneEntryService;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeleteEntryCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(DeleteEntryCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) {
            LOGGER.warn("Unauthorized access to delete entry");
            return PagePath.INDEX;
        }

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) {
            return PagePath.INDEX;
        }

        String entryIdStr = request.getParameter(AttributeName.ENTRY_ID);
        if (entryIdStr == null) {
            return PagePath.PHONE_BOOK;
        }

        Long entryId;
        try {
            entryId = Long.parseLong(entryIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute(AttributeName.ERROR_MSG, "Invalid entry identifier");
            return PagePath.PHONE_BOOK;
        }

        PhoneEntryService service = PhoneEntryServiceFactory.getPhoneEntryService();
        try {
            boolean success = service.deleteEntry(entryId, userId);
            if (success) {
                request.setAttribute(AttributeName.SUCCESS_MSG, "Contact deleted successfully");
            } else {
                request.setAttribute(AttributeName.ERROR_MSG, "Failed to delete contact or unauthorized access");
            }
        } catch (ServiceException e) {
            LOGGER.error("Failed to delete entry: {} for user: {}", entryId, userId, e);
            throw new CommandException("Contact deletion failed", e);
        }

        // FIX: Redirect to reload fresh data
        return "redirect:/controller?command=list_entries";
    }
}