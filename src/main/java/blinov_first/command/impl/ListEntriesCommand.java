package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.dao.impl.PhoneEntryDaoImpl;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.CommandException;
import blinov_first.exception.DaoException;
import blinov_first.util.AttributeName;
import blinov_first.util.Page;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ListEntriesCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(ListEntriesCommand.class);

    private static final int DEFAULT_PAGE      = 1;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String PARAM_PAGE     = "page";

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Long userId = SessionUtil.getUserId(request);
        if (userId == null || userId <= 0) {
            LOGGER.warn("Unauthorized access to phone book: userId={}", userId);
            return PagePath.INDEX;
        }

        int page = parsePage(request.getParameter(PARAM_PAGE));

        try {
            PhoneEntryDaoImpl dao = PhoneEntryDaoImpl.getInstance();

            int totalItems = dao.countByUserId(userId);
            int offset     = (page - 1) * DEFAULT_PAGE_SIZE;

            List<PhoneEntry> items = dao.findByUserIdPaged(userId, offset, DEFAULT_PAGE_SIZE);

            Page<PhoneEntry> pageResult = new Page<>(items, page, DEFAULT_PAGE_SIZE, totalItems);

            request.setAttribute(AttributeName.ENTRY_LIST, pageResult.getItems());
            request.setAttribute("entriesPage", pageResult);

            LOGGER.debug("Loaded page {}/{} for user {} ({} entries)",
                    page, pageResult.getTotalPages(), userId, items.size());

            return PagePath.PHONE_BOOK;
        } catch (DaoException e) {
            LOGGER.error("Failed to list entries for user: {}", userId, e);
            throw new CommandException("Entry listing failed", e);
        }
    }

    private int parsePage(String pageParam) {
        if (pageParam == null || pageParam.isBlank()) {
            return DEFAULT_PAGE;
        }
        try {
            int page = Integer.parseInt(pageParam);
            return page > 0 ? page : DEFAULT_PAGE;
        } catch (NumberFormatException e) {
            return DEFAULT_PAGE;
        }
    }
}
