package com.aashdit.digiverifier.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
//vunerablility fix for acessible by ipaddress
@Component
public class DomainCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the 'Host' header from the request, which indicates the domain or IP used for access
        String host = httpRequest.getHeader("Host");

        // Allow requests only if the host contains "digiverifier.com"
        if (host != null && (host.contains("digiverifier.com") || host.contains("localhost") || host.contains("kpmg.in"))) {
        	// If the host is valid, continue with the chain and process the request
            chain.doFilter(request, response);
        } else {
            // If the host is anything other than "digiverifier.com" or "localhost", return a 404 error
            httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
        }

    }
}

