package org.ecom.auth.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Builder
@ToString
public class ConnValidationResponse {
    private HttpStatus status;
    private boolean isAuthenticated;
    private String methodType;
    private String username;
    private String token;
    private List<? extends GrantedAuthority> authorities;
}