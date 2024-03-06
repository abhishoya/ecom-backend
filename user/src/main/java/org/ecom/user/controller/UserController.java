package org.ecom.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.ecom.common.model.user.User;
import org.ecom.common.model.user.UserRole;
import org.ecom.common.model.user.permission.IsAdmin;
import org.ecom.common.model.user.permission.IsUser;
import org.ecom.user.model.UserDto;
import org.ecom.user.model.UserSignupDto;
import org.ecom.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/api/user")
@EnableMethodSecurity
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder encoder;

    public UserController()
    {
    }

    @PostMapping("/signup")
    public UserDto createNewUser(@RequestBody UserSignupDto userSignupDto)
    {
        User user = modelMapper.map(userSignupDto, User.class);
        user.setTypeOfUser(UserRole.USER);
        user.setCreatedOn(new Date());
        user.setPassword(encoder.encode(userSignupDto.getPassword()));
        user.setIsAccountExpired(false);
        user.setIsAccountLocked(false);
        user.setIsAccountDeleted(false);
        User createdUser = service.createNewUser(user);
        return modelMapper.map(createdUser, UserDto.class);
    }

    @GetMapping(value = "/getAll/{pageNumber}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @IsAdmin
    public Stream<UserDto> getUsers(@PathVariable int pageNumber)
    {
        return service.getAllUsers(pageNumber).stream().map(u -> modelMapper.map(u, UserDto.class));
    }

    @GetMapping("/get")
    @IsUser
    public UserDto getUser() throws IOException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return modelMapper.map(service.getUserByUsername(username), UserDto.class);
    }

    @PutMapping("/update")
    @IsUser
    public UserDto updateUser(@RequestBody UserDto newUser) throws IllegalAccessException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (newUser.getUsername().equals(username))
        {
            User user = modelMapper.map(newUser, User.class);
            User updatedUser = service.updateUser(user);
            return modelMapper.map(updatedUser, UserDto.class);
        }
        else {
            throw new IllegalAccessException("Cannot update user");
        }
    }

    @DeleteMapping("/delete")
    @IsUser
    public void deleteUser() throws IOException {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        service.deleteUser(username);
    }
}
