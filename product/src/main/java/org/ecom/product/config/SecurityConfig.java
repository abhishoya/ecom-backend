package org.ecom.product.config;

import com.fasterxml.jackson.databind.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.*;
import org.ecom.common.model.response.*;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.web.filter.*;

import java.io.*;
import java.sql.*;
import java.time.*;
import java.util.*;

@Slf4j
@Configuration
public class SecurityConfig
{
    private ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new AuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers("/api/product/swagger/**","/api/product/get/**").permitAll()
                .anyRequest().authenticated().and().build();
    }

    public class AuthFilter extends OncePerRequestFilter
    {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String username = request.getHeader("username");
            String token = request.getHeader("auth-token");
            String authoritiesString = request.getHeader("authorities");
            if (username != null && token != null && authoritiesString != null)
            {
                List<? extends GrantedAuthority> authorities = Arrays.stream(authoritiesString.split(",")).map(SimpleGrantedAuthority::new).toList();
                log.info(String.format("Username: %s, Authorities: %s", username, authorities));
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, token, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
            else
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(ExceptionResponse.builder().status(HttpStatus.BAD_REQUEST).message("username, token or authorities missing from headers").timestamp(new Timestamp(Instant.now().toEpochMilli())).build()));
            }
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
            return request.getRequestURI().matches("/api/product/swagger/(.*)") || request.getRequestURI().matches("/api/product/get/(.*)");
        }
    }
}
