package com.aashdit.digiverifier.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.aashdit.digiverifier.login.service.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
@EnableScheduling
@EnableMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = { "com.aashdit.*" })
public class SecurityConfig {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private JwtFilter jwtFilter;
	
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
//	@Bean
//	public SessionRegistry SessionRegistry() {
//		SessionRegistry sessionRegistry = new SessionRegistryImpl();
//		return sessionRegistry;
//	}

//	@Bean
//	public HttpSessionEventPublisher httpSessionEventPublisher() {
//		return new HttpSessionEventPublisher();
//	}

	 @Bean
	    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration builder) throws Exception {
	        return builder.getAuthenticationManager();
	    }
	 
	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// @formatter:off
//		
//		 http.headers()
//         .contentTypeOptions()
//         .and()
//         .xssProtection()
//         .and()
//         .cacheControl()
//         .and()
//         .httpStrictTransportSecurity()
//         .and()
//         .frameOptions()
//         .and()
//         .contentSecurityPolicy("script-src 'self' 'unsafe-eval' 'unsafe-inline'")
////         .and()
////         .referrerPolicy(ReferrerPolicy.ORIGIN_WHEN_CROSS_ORIGIN)
//         ;
		
//		 http.csrf().disable().authorizeRequests()
//         .antMatchers(HttpMethod.TRACE, "/**").denyAll()
//         .antMatchers(HttpMethod.PATCH, "/**").denyAll()
//         .antMatchers(HttpMethod.DELETE, "/**").denyAll().antMatchers(HttpMethod.HEAD, "/**").denyAll()
//         .antMatchers("/swagger-resources/**").permitAll()
//         .antMatchers("/swagger**").permitAll()
//         .antMatchers("/webjars/**").permitAll()
//         .antMatchers("/configuration/ui").permitAll()
//         .antMatchers("/v2/api-docs").permitAll()
//         .antMatchers("/api/**").permitAll()
//         .anyRequest().authenticated();
//		http.headers()
//				.frameOptions().sameOrigin()
//				.contentSecurityPolicy("default-src 'self'");
//		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
//	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(request->
        request.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.TRACE, "/**")).denyAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.PATCH, "/**")).denyAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/**")).denyAll()
        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.HEAD, "/**")).denyAll()
//              request.requestMatchers(HttpMethod.TRACE, "/**").denyAll()
//              .requestMatchers(HttpMethod.PATCH, "/**").denyAll()
//              .requestMatchers(HttpMethod.DELETE, "/**").denyAll().requestMatchers(HttpMethod.HEAD, "/**").denyAll()
              .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
              .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
              .requestMatchers(new AntPathRequestMatcher("/swagger**")).permitAll()
              .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
              .requestMatchers(new AntPathRequestMatcher("/configuration/ui")).permitAll()
              .requestMatchers(new AntPathRequestMatcher("/v3/api-docs")).permitAll()
              .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
              .requestMatchers(new AntPathRequestMatcher("/api/**")).permitAll()
              .anyRequest().authenticated()
        		)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}
