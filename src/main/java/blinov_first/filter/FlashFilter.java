package blinov_first.filter;

import blinov_first.util.FlashMessage;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class FlashFilter implements Filter {

    @Override
    public void init(FilterConfig config) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if ("GET".equalsIgnoreCase(httpRequest.getMethod())) {
            FlashMessage.transfer(httpRequest);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
