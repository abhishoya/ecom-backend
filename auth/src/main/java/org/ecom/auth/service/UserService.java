package org.ecom.auth.service;

import org.ecom.auth.repository.*;
import org.ecom.common.model.user.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class UserService
{
    @Autowired
    private UserRepository repository;

    public User getUserByUsername(String username)
    {
        return repository.findByusername(username).orElseThrow(()->new RuntimeException("User not found"));
    }
}
