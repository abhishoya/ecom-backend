package org.ecom.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse
{
    private String token;
    private String username;
}
