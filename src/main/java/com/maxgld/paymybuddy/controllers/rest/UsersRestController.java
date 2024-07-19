package com.maxgld.paymybuddy.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maxgld.paymybuddy.model.User;

import com.maxgld.paymybuddy.repository.UsersRepository;
import com.maxgld.paymybuddy.services.UsersService;

@RestController
@RequestMapping("/users")
public class UsersRestController {
    @Autowired
    private UsersService usersService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = usersService.saveUser(user);

        if (newUser == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = usersService.findByEmail(email);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
