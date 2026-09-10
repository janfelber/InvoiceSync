package com.invoicesync.core.common;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

  public static final String REQUEST_ID_HEADER = "X-Request-ID";

  public static final String REQUEST_ID_MDC_KEY = "requestId";

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
      final FilterChain filterChain) throws ServletException, IOException {
    final String incoming = request.getHeader(REQUEST_ID_HEADER);
    final String requestId = (incoming != null && !incoming.isBlank()) ? incoming : UUID.randomUUID().toString();

    MDC.put(REQUEST_ID_MDC_KEY, requestId);
    response.setHeader(REQUEST_ID_HEADER, requestId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(REQUEST_ID_MDC_KEY);
    }
  }

}
