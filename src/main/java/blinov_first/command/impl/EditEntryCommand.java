package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.CommandException;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.PhoneEntryServiceFactory;
import blinov_first.util.AttributeName;
import blinov_first.util.FlashMessage;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditEntryCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(EditEntryCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) return PagePath.INDEX;

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) return PagePath.INDEX;

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String entryIdStr = request.getParameter(AttributeName.ENTRY_ID);
            if (entryIdStr == null) return PagePath.PHONE_BOOK;
            try {
                int entryId = Integer.parseInt(entryIdStr);
                PhoneEntry entry = blinov_first.dao.impl.PhoneEntryDaoImpl.getInstance()
                        .findById(entryId)
                        .orElseThrow(() -> new CommandException("Entry not found"));

                if (!entry.getUserId().equals(userId)) {
                    LOGGER.warn("Unauthorized edit attempt: user={}, entryId={}", userId, entryId);
                    return PagePath.PHONE_BOOK;
                }

                request.setAttribute("entry", entry);
                return PagePath.ENTRY_FORM;
            } catch (DaoException | NumberFormatException e) {
                LOGGER.error("Failed to load entry for editing", e);
                throw new CommandException("Entry loading failed", e);
            }
        }

        String entryIdStr = request.getParameter(AttributeName.ENTRY_ID);
        String name  = request.getParameter("contactName");
        String phone = request.getParameter("contactPhone");
        String email = request.getParameter("contactEmail");

        if (entryIdStr == null || name == null || name.trim().isEmpty()) {
            request.setAttribute(AttributeName.ERROR_MSG, "Name is required");
            return PagePath.ENTRY_FORM;
        }

        try {
            int entryId = Integer.parseInt(entryIdStr);
            PhoneEntry entry = new PhoneEntry(name, phone, email, userId);
            entry.setId(entryId);

            if (PhoneEntryServiceFactory.getPhoneEntryService().updateEntry(entry)) {
                FlashMessage.success(request, "Contact updated successfully");
            } else {
                FlashMessage.error(request, "Failed to update contact");
            }
        } catch (ServiceException | NumberFormatException e) {
            LOGGER.error("Failed to update entry", e);
            FlashMessage.error(request, "Update error: " + e.getMessage());
        }

        return "redirect:/controller?command=list_entries";
    }
}
