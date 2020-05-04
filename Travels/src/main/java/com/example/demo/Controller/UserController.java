package com.example.demo.Controller;

import com.example.demo.Entity.User;
import com.example.demo.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "user")
    public User addUser(@RequestParam(value = "username", required = false) String username,
                        @RequestParam(value = "password", required = false) String password) throws JsonProcessingException {
        return userService.addUser(username,password);
    }

    @GetMapping(value = "user")
    public List<User> getUserAll() throws JsonProcessingException{
        return userService.getUserAll();
    }

    @GetMapping(value = "user/query")
    public User getUser(@RequestParam(value = "username", required = false) String username,
                          @RequestParam(value = "id", required = false) Long id) throws JsonProcessingException{
        return userService.getUser(username,id);
    }

    @PostMapping(value = "user/modifyUser")
    public void modifyUser(@RequestBody User user) throws JsonProcessingException {
        userService.modifyUser(user);
    }

    @PostMapping(value = "user/deleteUser")
    public void deleteUser(@RequestBody User user) throws JsonProcessingException{
        userService.deleteUser(user);
    }
}
