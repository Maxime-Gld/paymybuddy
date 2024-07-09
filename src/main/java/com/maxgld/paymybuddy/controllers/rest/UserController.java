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

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UsersRepository usersRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = usersRepository.save(user);
        if (newUser != null) {
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        User user = usersRepository.findByEmail(email);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
