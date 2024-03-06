package org.ecom.auth.controller;

import io.micrometer.tracing.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.*;
import org.ecom.auth.model.*;
import org.ecom.auth.service.*;
import org.ecom.auth.utils.*;
import org.ecom.common.model.log.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.core.*;
import org.springframework.security.crypto.password.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
