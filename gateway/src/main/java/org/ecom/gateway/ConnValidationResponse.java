package org.ecom.gateway;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
public class ConnValidationResponse {
    private HttpStatus status;
    private boolean isAuthenticated;
    private String methodType;
    private String username;
    private String token;
    List<Map<String, Object>> authorities;
}