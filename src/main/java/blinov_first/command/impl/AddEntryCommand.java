package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.PhoneEntryServiceFactory;
import blinov_first.service.PhoneEntryService;
import blinov_first.util.AttributeName;
import blinov_first.util.FlashMessage;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddEntryCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(AddEntryCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) return PagePath.INDEX;

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) return PagePath.INDEX;

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return PagePath.ENTRY_FORM;
        }

        String name  = request.getParameter("contactName");
        String phone = request.getParameter("contactPhone");
        String email = request.getParameter("contactEmail");

        if (name == null || name.trim().isEmpty()) {
            request.setAttribute(AttributeName.ERROR_MSG, "Name is required");
            return PagePath.ENTRY_FORM;
        }

        PhoneEntry entry = new PhoneEntry(name, phone, email, userId);
        PhoneEntryService service = PhoneEntryServiceFactory.getPhoneEntryService();

        try {
            if (service.addEntry(entry)) {
                FlashMessage.success(request, "Contact added successfully");
            } else {
                FlashMessage.error(request, "Failed to add contact");
            }
        } catch (ServiceException e) {
            LOGGER.error("Failed to add entry for user: {}", userId, e);
            FlashMessage.error(request, "Error: " + e.getMessage());
        }

        return "redirect:/controller?command=list_entries";
    }
}
