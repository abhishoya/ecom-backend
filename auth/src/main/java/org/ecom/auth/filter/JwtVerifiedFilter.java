package org.ecom.auth.filter;

import com.fasterxml.jackson.databind.*;
import com.mongodb.lang.*;
import io.jsonwebtoken.*;
import io.micrometer.tracing.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.ecom.auth.service.*;
import org.ecom.auth.utils.*;
import org.ecom.common.model.response.*;
import org.ecom.common.model.user.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

import java.io.*;
import java.util.*;

@Component
public class JwtVerifiedFilter extends OncePerRequestFilter {
    @Autowired
    Tracer tracer;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

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
                                .errorId(HttpStatus.UNAUTHORIZED.toString())
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
