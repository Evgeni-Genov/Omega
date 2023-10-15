package com.example.omega.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter for logging incoming and outgoing HTTP requests.
 */
@Slf4j
@Component
public class LogRequestFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "request-id";
    public static final String TRACKING_ID = "request.tracking.id";

    /**
     * This method intercepts incoming and outgoing HTTP requests, logs relevant information,
     * and adds tracking information to the Mapped Diagnostic Context (MDC).
     *
     * @param request     The incoming HTTP request.
     * @param response    The outgoing HTTP response.
     * @param filterChain The filter chain for processing the request.
     * @throws ServletException If a servlet error occurs.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final var start = System.currentTimeMillis();

        var headerId = request.getHeader(REQUEST_ID);
        var trackingId = headerId != null ? headerId : UUID.randomUUID().toString();

        MDC.put(TRACKING_ID, trackingId);

        var sb = new StringBuilder();
        sb.append("- request params: ");

        request.getParameterMap().entrySet().forEach((entry) -> {
            sb.append("[")
                    .append(entry.getKey())
                    .append("=")
                    .append(entry.getValue()[0])
                    .append("] ");
        });

        if (shouldLog(request)) {
            log.info("Processing {} '{}' request {}", request.getMethod(), request.getRequestURI(), sb.length() > 19 ? sb.toString().trim() : "");
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            long end = System.currentTimeMillis();

            if (shouldLog(request)) {
                log.info("Processing {} '{}' response - status: [{}], content-type: [{}], process time: ({}ms)", request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        response.getContentType(),
                        end - start);
            }

            MDC.remove(TRACKING_ID);
        }
    }

    /**
     * Determines whether a request should be logged based on the request URI.
     *
     * @param request The HTTP request to be checked.
     * @return True if the request should be logged, false otherwise.
     */
    private boolean shouldLog(final HttpServletRequest request) {
        return !request.getRequestURI().contains("actuator");
    }
}
