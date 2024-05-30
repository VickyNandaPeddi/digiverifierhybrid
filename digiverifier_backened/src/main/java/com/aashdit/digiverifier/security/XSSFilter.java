package com.aashdit.digiverifier.security;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class XSSFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  @Override
  public void doFilter(
        ServletRequest request, 
        ServletResponse response, 
        FilterChain chain) throws IOException, ServletException {

     // chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
	  chain.doFilter(request, response);
   }

} 