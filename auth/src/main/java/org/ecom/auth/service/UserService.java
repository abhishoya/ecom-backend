package org.ecom.auth.service;

import org.ecom.auth.repository.UserRepository;
import org.ecom.common.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
