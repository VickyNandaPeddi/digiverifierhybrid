package com.aashdit.digiverifier.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aashdit.digiverifier.config.admin.model.Token;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.TokenRepository;
import com.aashdit.digiverifier.config.admin.service.UserService;
import com.aashdit.digiverifier.config.superadmin.service.SuperAdminDashboardServiceImpl;
import com.aashdit.digiverifier.login.model.LoggedInUser;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
	@Value("${CROSS.ORIGINS}")
	private String crossOrigins;
	
	@Value("${KPMG.CROSS.ORIGINS}")
	private String kpmgCrossOrigins;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserService userService;
	
	@Autowired
	private TokenRepository tokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {

		httpServletResponse.setHeader("Access-Control-Allow-Origin", crossOrigins);
		String requestingOrigin = httpServletRequest.getHeader("Origin"); 
		if (requestingOrigin != null) {
		    // Check if the requesting origin is allowed
		    if (requestingOrigin.equals(crossOrigins) ||
		        requestingOrigin.equals(kpmgCrossOrigins)) {
		        httpServletResponse.setHeader("Access-Control-Allow-Origin", requestingOrigin);
		    }
		}

		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,PUT,POST,OPTIONS");
		httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

		httpServletResponse.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With, No-Auth");

		httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");

		httpServletResponse.setHeader("Content-Security-Policy", "frame-ancestors 'self'");
		httpServletResponse.setHeader("X-Xss-Protection", "1; mode=block");
		httpServletResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
		
		Boolean skipThis = false;
		if (httpServletRequest.getRequestURI().contains("/api/login/authenticate")
				|| httpServletRequest.getRequestURI().contains("/configuration/ui")
				|| httpServletRequest.getRequestURI().contains("/swagger")
				|| httpServletRequest.getRequestURI().contains("/webjars")
				|| httpServletRequest.getRequestURI().contains("/v3")
				|| httpServletRequest.getRequestURI().contains("/api/allowAll")) {
			skipThis = true;
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} else {
			if (httpServletRequest.getRequestURI().contains("/api") && skipThis == false) {
				String authorizationHeader = httpServletRequest.getHeader("Authorization");
				String token = null;
				String userName = null;

				if (!httpServletRequest.getMethod().equals("OPTIONS")) {
					if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
						token = authorizationHeader.substring(7);
						if(StringUtils.isNotEmpty(token)){
							userName = jwtUtil.extractUsername(token);
						}
						
						if (userName != null) {
							User user = userService.findByUsername(userName).getData();
							if (user != null) {
								
								try {
									//check the validity of token from database side
									Optional<Token> dataBaseTokenEntry = tokenRepository.findByUserToken(token);
									boolean isTokenValidInDB = dataBaseTokenEntry.get()!=null && !dataBaseTokenEntry.get().getExpired() && !dataBaseTokenEntry.get().getRevoked();
									if (Boolean.TRUE.equals(jwtUtil.validateToken(token, user)) && isTokenValidInDB) {

										final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
										grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getRoleCode()));

										final LoggedInUser userDetails = new LoggedInUser(user.getUserName(),
												user.getPassword(), true, true, true, true, grantedAuthorities,
												user.getRole(), user);

										final Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
												null, grantedAuthorities);
										final SecurityContext sc = SecurityContextHolder.getContext();
										sc.setAuthentication(auth);
										final HttpSession session = httpServletRequest.getSession(true);
										session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

										filterChain.doFilter(httpServletRequest, httpServletResponse);

									} else {
										user.setIsLoggedIn(false);
										userService.saveUserLoginData(user);
										httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
									}
								} catch (Exception e) {
									log.error("Exception occured in jWT filter-->",e);
									user.setIsLoggedIn(false);
									userService.saveUserLoginData(user);
									httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
								}
							} else {
								httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
							}

						} else {
							httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
						}

					} else {
						httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
					}
				}

			} else {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
			}
		}

	}

}
