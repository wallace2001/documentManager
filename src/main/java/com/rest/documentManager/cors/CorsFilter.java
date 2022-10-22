package com.rest.documentManager.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Value("${url}")
    private String URL;

    @Value("${other.url}")
    private String OTHER_URL;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (URL.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", URL);
        } else if (OTHER_URL.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", OTHER_URL);
        }

        response.setHeader("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(request.getMethod()) && (URL.equals(request.getHeader("Origin")) || OTHER_URL.equals(request.getHeader("Origin")))) {
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
            response.setHeader("Access-Control-Max-Age", "3600");

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }

    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}
