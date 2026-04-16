package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.PhoneEntryServiceFactory;
import blinov_first.service.PhoneEntryService;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class AddEntryCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(AddEntryCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) {
            LOGGER.warn("Unauthorized access to add entry");
            return PagePath.INDEX;
        }

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) {
            return PagePath.INDEX;
        }

        // GET: show empty form
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return PagePath.ENTRY_FORM;
        }

        // POST: process form and save
        String name = request.getParameter("contactName");
        String phone = request.getParameter("contactPhone");
        String email = request.getParameter("contactEmail");

        if (name == null || name.trim().isEmpty()) {
            request.setAttribute(AttributeName.ERROR_MSG, "Name is required");
            return PagePath.ENTRY_FORM;
        }

        PhoneEntry entry = new PhoneEntry(name, phone, email, userId);
        PhoneEntryService service = PhoneEntryServiceFactory.getPhoneEntryService();

        try {
            boolean success = service.addEntry(entry);
            if (success) {
                request.setAttribute(AttributeName.SUCCESS_MSG, "Contact added successfully");
            } else {
                request.setAttribute(AttributeName.ERROR_MSG, "Failed to add contact (maybe duplicate phone?)");
                return PagePath.ENTRY_FORM;
            }
        } catch (ServiceException e) {
            LOGGER.error("Failed to add entry for user: {}", userId, e);
            request.setAttribute(AttributeName.ERROR_MSG, "Error: " + e.getMessage());
            return PagePath.ENTRY_FORM;
        }

        // FIX: Redirect to list_entries command to reload fresh data (PRG pattern)
        return "redirect:/controller?command=list_entries";
    }
}