package org.ecom.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.ecom.common.model.response.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CommonAuthFilter extends OncePerRequestFilter
{
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${security.config.excludedUrls}")
    private String[] excludedUrls;

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
        return Arrays
                .stream(excludedUrls)
                .map(url -> url.replace("**", "(.*)"))
                .anyMatch(url -> request.getRequestURI().matches(url));
    }
}