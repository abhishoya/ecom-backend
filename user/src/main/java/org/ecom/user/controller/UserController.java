package org.ecom.user.controller;

import lombok.extern.slf4j.*;
import org.ecom.common.model.user.*;
import org.ecom.common.model.user.permission.*;
import org.ecom.user.model.*;
import org.ecom.user.service.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.core.context.*;
import org.springframework.security.crypto.password.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;
import java.util.stream.*;

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
    UserDto createNewUser(@RequestBody UserSignupDto userSignupDto)
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
    Stream<UserDto> getUsers(@PathVariable int pageNumber)
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
    @PreAuthorize("#newUser.username == authentication.principal")
    UserDto updateUser(@RequestBody UserDto newUser) throws IOException {
        User user = modelMapper.map(newUser, User.class);
        User updatedUser = service.updateUser(user);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @DeleteMapping("/delete/{username}")
    @IsUser
    @PreAuthorize("#username == authentication.principal.username")
    void deleteUser(@PathVariable String username) throws IOException {
        service.deleteUser(username);
    }
}
