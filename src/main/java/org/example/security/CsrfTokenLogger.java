package org.example.security;

import static org.example.Main.NAMESPACE;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;

public class CsrfTokenLogger implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf"); // Header is X-CSRF-TOKEN

//        LOGGER.info("CSRF token: [{}]", token.getToken());

        filterChain.doFilter(request, response);
    }
}
