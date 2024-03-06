package org.ecom.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.ecom.auth.model.ConnValidationResponse;
import org.ecom.auth.model.JwtAuthenticationModel;
import org.ecom.auth.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuthController
{
    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping(value = "/auth/validateToken", headers = { HttpHeaders.AUTHORIZATION })
    public ConnValidationResponse validateToken(HttpServletRequest request)
    {
        String username = (String) request.getAttribute("username");
        String token = (String) request.getAttribute("jwt");
        return ConnValidationResponse.builder().status(HttpStatus.OK).methodType(HttpMethod.GET.name())
                .username(username).token(token).authorities(jwtHelper.getAuthoritiesFromToken(token))
                .isAuthenticated(true).build();
    }

    @PostMapping(value = "/auth/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public void login(@RequestBody JwtAuthenticationModel jwtAuthenticationModel)
    {
    }
}
