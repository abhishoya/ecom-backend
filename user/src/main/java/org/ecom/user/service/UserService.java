package org.ecom.user.service;

import org.ecom.common.model.user.User;
import org.ecom.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService
{
    @Autowired
    private UserRepository repository;

    public UserService()
    {
    }

    public List<User> getAllUsers(int pageNumber)
    {
        return repository.findAll(PageRequest.of(pageNumber, 100)).stream().toList();
    }

    public User createNewUser(User user)
    {
        return repository.save(user);
    }

    public User getUserByUsername(String username)
    {
        return repository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));
    }

    public User updateUser(User newUser)
    {
        return repository.findByUsername(newUser.getUsername()).map(user -> {
                    user.setFirstName(newUser.getFirstName());
                    user.setLastName(newUser.getLastName());
                    user.setPhoneNo(newUser.getPhoneNo());
                    return repository.save(user);
                })
                .orElseThrow(() -> new RuntimeException(String.format("User with email %s doesn't exist", newUser.getUsername())));
    }

    public void deleteUser(String username) {
        repository.deleteByUsername(username);
    }
}
