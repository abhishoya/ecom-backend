package org.ecom.common.config.security;

import com.fasterxml.jackson.databind.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;

@Slf4j
@Configuration
public abstract class CommonSecurityConfig
{
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${security.config.excludedUrls}")
    private String[] excludedUrls;

    @Autowired
    private CommonAuthFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(authFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers(excludedUrls).permitAll()
                .anyRequest().authenticated().and().build();
    }
}
