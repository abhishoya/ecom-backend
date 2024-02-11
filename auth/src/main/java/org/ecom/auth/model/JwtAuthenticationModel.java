package org.ecom.auth.model;

import lombok.*;

@Getter
@Setter
public class JwtAuthenticationModel
{
    private String username;
    private String password;
}
