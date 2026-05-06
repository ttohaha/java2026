package blinov_first.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class FlashMessage {

    private static final String FLASH_SUCCESS = "flashSuccess";
    private static final String FLASH_ERROR   = "flashError";

    private FlashMessage() {}

    public static void success(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(FLASH_SUCCESS, message);
    }

    public static void error(HttpServletRequest request, String message) {
        request.getSession(true).setAttribute(FLASH_ERROR, message);
    }

    public static void transfer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return;

        String success = (String) session.getAttribute(FLASH_SUCCESS);
        String error   = (String) session.getAttribute(FLASH_ERROR);

        if (success != null) {
            request.setAttribute(AttributeName.SUCCESS_MSG, success);
            session.removeAttribute(FLASH_SUCCESS);
        }
        if (error != null) {
            request.setAttribute(AttributeName.ERROR_MSG, error);
            session.removeAttribute(FLASH_ERROR);
        }
    }
}
