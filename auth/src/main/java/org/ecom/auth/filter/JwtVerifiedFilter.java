package org.ecom.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.lang.NonNull;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ecom.auth.service.UserService;
import org.ecom.auth.utils.JwtHelper;
import org.ecom.common.model.response.ExceptionResponse;
import org.ecom.common.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class JwtVerifiedFilter extends OncePerRequestFilter {
    @Autowired
    Tracer tracer;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);

            try {
                username = this.jwtHelper.getUserNameFromToken(token);
            } catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username !!");
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired !!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(
                        objectMapper.writeValueAsString(ExceptionResponse
                                .builder()
                                .status(HttpStatus.UNAUTHORIZED)
                                .message("Expired Token")
                                .requestId(Objects.requireNonNull(tracer.currentTraceContext().context()).traceId())
                                .build()
                        )
                );
                return;
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token !! Invalid Token");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            logger.info("Invalid Header Value !! ");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                List<? extends GrantedAuthority> authorities = jwtHelper.getAuthoritiesFromToken(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, token, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("username", username);
                request.setAttribute("authorities", authorities);
                request.setAttribute("jwt", token);
            } else {
                logger.info("Validation fails !!");
            }
        }
        filterChain.doFilter(request, response);
    }
}
