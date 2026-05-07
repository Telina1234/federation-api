package edu.hei.school.agricultural.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyAuthenticationFilter implements Filter {
    private static final String API_KEY_HEADER = "x-api-key";
    private static final String BAD_CREDENTIALS = "Bad credentials";

    private final String expectedApiKey;

    public ApiKeyAuthenticationFilter(@Value("${api.security.key}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String providedApiKey = httpRequest.getHeader(API_KEY_HEADER);
        if (!expectedApiKey.equals(providedApiKey)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("text/plain");
            httpResponse.getWriter().write(BAD_CREDENTIALS);
            return;
        }

        chain.doFilter(request, response);
    }
}
