package blinov_first.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EncodingFilter implements Filter {

    private static final String INIT_PARAM_ENCODING = "encoding";

    private String encoding;

    @Override
    public void init(FilterConfig config) {
        String configured = config.getInitParameter(INIT_PARAM_ENCODING);
        encoding = (configured != null && !configured.isBlank())
                ? configured
                : StandardCharsets.UTF_8.name();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(encoding);
        }
        response.setCharacterEncoding(encoding);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
