package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.CommandException;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.PhoneEntryServiceFactory;
import blinov_first.service.PhoneEntryService;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditEntryCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(EditEntryCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) {
            LOGGER.warn("Unauthorized access to edit entry");
            return PagePath.INDEX;
        }

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) {
            return PagePath.INDEX;
        }

        // GET: load entry data into form
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            String entryIdStr = request.getParameter(AttributeName.ENTRY_ID);
            if (entryIdStr == null) {
                return PagePath.PHONE_BOOK;
            }
            try {
                int entryId = Integer.parseInt(entryIdStr);
                PhoneEntry entry = blinov_first.dao.impl.PhoneEntryDaoImpl.getInstance()
                        .findById(entryId)
                        .orElseThrow(() -> new CommandException("Entry not found"));

                if (!entry.getUserId().equals(userId)) {
                    LOGGER.warn("Unauthorized edit: user {} tried to edit entry {} owned by user {}",
                            userId, entryId, entry.getUserId());
                    return PagePath.PHONE_BOOK;
                }

                request.setAttribute("entry", entry);
                return PagePath.ENTRY_FORM;
            } catch (DaoException | NumberFormatException e) {
                LOGGER.error("Failed to load entry for editing", e);
                throw new CommandException("Entry loading failed", e);
            }
        }

        // POST: update the entry
        String entryIdStr = request.getParameter(AttributeName.ENTRY_ID);
        String name = request.getParameter("contactName");
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

            boolean success = PhoneEntryServiceFactory.getPhoneEntryService().updateEntry(entry);
            if (success) {
                request.setAttribute(AttributeName.SUCCESS_MSG, "Contact updated successfully");
            } else {
                request.setAttribute(AttributeName.ERROR_MSG, "Failed to update contact");
            }
        } catch (ServiceException | NumberFormatException e) {
            LOGGER.error("Failed to update entry", e);
            throw new CommandException("Contact update failed", e);
        }

        // FIX: Redirect to reload fresh data
        return "redirect:/controller?command=list_entries";
    }
}