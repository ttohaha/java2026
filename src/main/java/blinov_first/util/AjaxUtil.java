package blinov_first.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class AjaxUtil {

    private static final String AJAX_HEADER_NAME  = "X-Requested-With";
    private static final String AJAX_HEADER_VALUE = "XMLHttpRequest";
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    private AjaxUtil() {}

    public static boolean isAjax(HttpServletRequest request) {
        return AJAX_HEADER_VALUE.equalsIgnoreCase(request.getHeader(AJAX_HEADER_NAME));
    }

    public static void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType(CONTENT_TYPE_JSON);
        response.getWriter().write(json);
    }

    public static void writeSuccess(HttpServletResponse response, String message) throws IOException {
        writeJson(response, buildResult(true, message));
    }

    public static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        writeJson(response, buildResult(false, message));
    }

    private static String buildResult(boolean ok, String message) {
        return "{\"ok\":" + ok + ",\"message\":\"" + escapeJson(message) + "\"}";
    }

    public static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
