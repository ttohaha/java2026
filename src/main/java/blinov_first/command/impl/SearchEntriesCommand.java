package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.dao.impl.PhoneEntryDaoImpl;
import blinov_first.entity.PhoneEntry;
import blinov_first.exception.CommandException;
import blinov_first.exception.DaoException;
import blinov_first.util.AjaxUtil;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class SearchEntriesCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(SearchEntriesCommand.class);

    private static final String PARAM_QUERY        = "q";
    private static final int    MAX_QUERY_LENGTH   = 100;
    private static final int    MAX_RESULTS        = 50;

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        Long userId = SessionUtil.getUserId(request);
        if (userId == null || userId <= 0) {
            return handleUnauthorized(request);
        }

        String query = sanitizeQuery(request.getParameter(PARAM_QUERY));

        if (!AjaxUtil.isAjax(request)) {
            request.setAttribute(AttributeName.ENTRY_LIST, List.of());
            return PagePath.PHONE_BOOK;
        }

        HttpServletResponse response =
                (HttpServletResponse) request.getAttribute(AttributeName.HTTP_RESPONSE);

        try {
            List<PhoneEntry> results = PhoneEntryDaoImpl.getInstance()
                    .searchByUserIdAndQuery(userId, query, MAX_RESULTS);
            LOGGER.debug("AJAX search query='{}' for user={} returned {} result(s)",
                    query, userId, results.size());
            AjaxUtil.writeJson(response, buildJson(results));
        } catch (DaoException | IOException e) {
            LOGGER.error("Search failed for user={} query='{}'", userId, query, e);
            tryWriteError(response);
        }

        return PagePath.AJAX_HANDLED;
    }

    private String handleUnauthorized(HttpServletRequest request) {
        if (!AjaxUtil.isAjax(request)) {
            return PagePath.INDEX;
        }
        HttpServletResponse response =
                (HttpServletResponse) request.getAttribute(AttributeName.HTTP_RESPONSE);
        tryWriteError(response);
        return PagePath.AJAX_HANDLED;
    }

    private String sanitizeQuery(String raw) {
        if (raw == null) {
            return "";
        }
        String trimmed = raw.trim();
        return trimmed.length() > MAX_QUERY_LENGTH
                ? trimmed.substring(0, MAX_QUERY_LENGTH)
                : trimmed;
    }

    private String buildJson(List<PhoneEntry> entries) {
        if (entries.isEmpty()) {
            return "{\"items\":[]}";
        }
        StringBuilder sb = new StringBuilder("{\"items\":[");
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            appendEntry(sb, entries.get(i));
        }
        sb.append("]}");
        return sb.toString();
    }

    private void appendEntry(StringBuilder sb, PhoneEntry e) {
        sb.append("{\"id\":")         .append(e.getId())
          .append(",\"name\":\"")     .append(AjaxUtil.escapeJson(e.getContactName())).append('"')
          .append(",\"phone\":\"")    .append(AjaxUtil.escapeJson(e.getContactPhone())).append('"')
          .append(",\"email\":\"")    .append(AjaxUtil.escapeJson(e.getContactEmail())).append('"')
          .append('}');
    }

    private void tryWriteError(HttpServletResponse response) {
        if (response == null) {
            return;
        }
        try {
            AjaxUtil.writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Search failed");
        } catch (IOException ex) {
            LOGGER.error("Failed to write error response", ex);
        }
    }
}
