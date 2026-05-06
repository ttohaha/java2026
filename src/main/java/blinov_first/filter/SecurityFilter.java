package blinov_first.filter;

import blinov_first.util.AttributeName;
import blinov_first.util.CookieUtil;
import blinov_first.util.PagePath;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Set;

public class SecurityFilter implements Filter {

    private static final Logger LOGGER = LogManager.getLogger(SecurityFilter.class);

    private static final Set<String> PUBLIC_COMMANDS = Set.of(
            "login",
            "add_user",
            "confirm_email",
            "change_locale"
    );

    private static final Set<String> PUBLIC_JSP_SUFFIXES = Set.of(
            "registration.jsp",
            "confirm_success.jsp",
            "confirm_error.jsp"
    );

    @Override
    public void init(FilterConfig config) {
        LOGGER.info("SecurityFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String contextPath = httpRequest.getContextPath();
        String requestURI  = httpRequest.getRequestURI();
        String path        = requestURI.substring(contextPath.length());

        restoreLangFromCookie(httpRequest);

        if (isPublicJsp(path)) {
            chain.doFilter(request, response);
            return;
        }

        if ("/controller".equals(path)) {
            String command = httpRequest.getParameter(AttributeName.COMMAND);
            if (isPublicCommand(command)) {
                chain.doFilter(request, response);
                return;
            }
        }

        if (!isLoggedIn(httpRequest)) {
            saveLastPage(httpRequest, httpResponse, path);
            LOGGER.debug("Unauthenticated access to '{}' — redirecting to login", path);
            httpResponse.sendRedirect(contextPath + PagePath.INDEX);
            return;
        }

        chain.doFilter(request, response);
    }

    private void restoreLangFromCookie(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return;

        if (session.getAttribute("lang") != null) return;

        CookieUtil.read(request, CookieUtil.COOKIE_LANG).ifPresent(lang ->
                session.setAttribute("lang", lang)
        );
    }

    private void saveLastPage(HttpServletRequest request, HttpServletResponse response, String path) {
        if (path == null || path.equals(PagePath.INDEX) || path.startsWith("/pages/error")) {
            return;
        }
        String query = request.getQueryString();
        String fullPath = (query != null) ? path + "?" + query : path;
        CookieUtil.writeLastPage(response, fullPath);
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(AttributeName.USER_ID) != null;
    }

    private boolean isPublicCommand(String command) {
        return command != null && PUBLIC_COMMANDS.contains(command.toLowerCase());
    }

    private boolean isPublicJsp(String path) {
        if (path == null) return false;
        return PUBLIC_JSP_SUFFIXES.stream().anyMatch(path::endsWith);
    }

    @Override
    public void destroy() {
        LOGGER.info("SecurityFilter destroyed");
    }
}
