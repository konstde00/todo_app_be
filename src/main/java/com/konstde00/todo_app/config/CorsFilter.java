package com.konstde00.todo_app.config;

import java.io.IOException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      jakarta.servlet.http.HttpServletRequest request,
      jakarta.servlet.http.HttpServletResponse response,
      jakarta.servlet.FilterChain filterChain)
      throws jakarta.servlet.ServletException, IOException {

    response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, HEAD");
    response.addHeader(
        "Access-Control-Allow-Headers",
        "Origin, Accept, X-Requested-With, Content-Type, Authorization, authorization, Access-Control-Request-Method, Access-Control-Request-Headers");
    response.addHeader(
        "Access-Control-Expose-Headers",
        "Access-Control-Allow-Origin, Access-Control-Allow-Credentials");
    response.addHeader("Access-Control-Allow-Credentials", "true");
    response.addIntHeader("Access-Control-Max-Age", 10);
    filterChain.doFilter(request, response);
  }
}
