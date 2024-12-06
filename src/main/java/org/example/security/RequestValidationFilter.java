package org.example.security;

import static org.example.Main.NAMESPACE;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Custom filter that validates the incoming request has a request id.
 * If no request id, return 400 Bad Request.
 */
@Component
public class RequestValidationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        final String requestId = request.getHeader("x-request-id");

        if (requestId == null || requestId.isBlank()) {
            LOGGER.error("x-request-id header is null or empty");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        MDC.put("request_id", requestId);
        filterChain.doFilter(request, response);
    }
}
