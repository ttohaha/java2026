package blinov_first.filter;

import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {"/pages/*"})
public class SecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();

        boolean isRegistrationPage = requestURI.endsWith("registration.jsp");
        boolean isLoggedIn = (session != null && session.getAttribute(AttributeName.USER) != null);

        if (isLoggedIn || isRegistrationPage) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + PagePath.INDEX);
        }
    }

    @Override
    public void destroy() {
    }
}