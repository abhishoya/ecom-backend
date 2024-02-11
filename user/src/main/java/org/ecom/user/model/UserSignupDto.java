package org.ecom.user.model;

import lombok.*;

@Data
public class UserSignupDto
{
    private String firstName;
    private String lastName;
    private String username;
    private String password;
}
