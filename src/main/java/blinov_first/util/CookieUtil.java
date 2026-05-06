package blinov_first.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

public final class CookieUtil {

    public static final String COOKIE_REMEMBER_LOGIN = "rememberedLogin";
    public static final String COOKIE_LANG           = "lang";
    public static final String COOKIE_LAST_PAGE      = "lastPage";

    private static final int MAX_AGE_30_DAYS  = 30 * 24 * 60 * 60;
    private static final int MAX_AGE_1_YEAR   = 365 * 24 * 60 * 60;
    private static final int MAX_AGE_DELETE   = 0;

    private CookieUtil() {}

    public static Optional<String> read(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static void writeRememberLogin(HttpServletResponse response, String login) {
        write(response, COOKIE_REMEMBER_LOGIN, login, MAX_AGE_30_DAYS);
    }

    public static void writeLang(HttpServletResponse response, String lang) {
        write(response, COOKIE_LANG, lang, MAX_AGE_1_YEAR);
    }

    public static void writeLastPage(HttpServletResponse response, String path) {
        write(response, COOKIE_LAST_PAGE, path, MAX_AGE_30_DAYS);
    }

    public static void delete(HttpServletResponse response, String name) {
        write(response, name, "", MAX_AGE_DELETE);
    }

    private static void write(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
