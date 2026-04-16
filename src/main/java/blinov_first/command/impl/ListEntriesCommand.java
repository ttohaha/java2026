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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ListEntriesCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(ListEntriesCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        // Simple session check: just verify USER_ID exists
        Long userId = SessionUtil.getUserId(request);
        if (userId == null || userId <= 0) {
            LOGGER.warn("Unauthorized access to phone book: userId={}", userId);
            return PagePath.INDEX;
        }

        PhoneEntryService service = PhoneEntryServiceFactory.getPhoneEntryService();
        try {
            List<PhoneEntry> entries = service.getUserEntries(userId);
            request.setAttribute(AttributeName.ENTRY_LIST, entries);
            LOGGER.debug("Loaded {} entries for user {}", entries.size(), userId);
            return PagePath.PHONE_BOOK;
        } catch (ServiceException e) {
            LOGGER.error("Failed to list entries for user: {}", userId, e);
            throw new CommandException("Entry listing failed", e);
        }
    }
}