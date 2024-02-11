package org.ecom.gateway;

import lombok.*;
import org.springframework.http.*;
import java.util.*;

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