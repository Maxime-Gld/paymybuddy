package com.maxgld.paymybuddy.services;

import java.util.HashSet;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.maxgld.paymybuddy.dto.UserCreateDto;
import com.maxgld.paymybuddy.dto.UserDto;
import com.maxgld.paymybuddy.entity.UserEntity;
import com.maxgld.paymybuddy.repository.UsersRepository;

@Service
public class UserService {

    private UsersRepository usersRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Boolean saveUser(UserCreateDto user) {
        Optional<UserEntity> existing = usersRepository.findByEmail(user.getEmail());

        if (existing.isPresent()) {
            return false;
        }

        usersRepository.save(createUser(user));

        return true;
    }

    private UserDto convertToUserDto(UserEntity newUser) {

        UserDto user = new UserDto();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        return user;
    }

    private UserEntity createUser(UserCreateDto user) {

        UserEntity newUser = new UserEntity();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        newUser.setBalance(100.0);
        newUser.setConnections(new HashSet<>());
        return newUser;
    }

    public UserEntity findByEmail(String email) {
        return usersRepository.findByEmail(email).orElse(null);
    }

    // send and receive connections
    /*
     * public void addConnection(UserCreateDto user, UserCreateDto connection) {
     * if (!user.getConnections().contains(connection)) {
     * user.getConnections().add(connection);
     * }
     * }
     * 
     * public void removeConnection(UserCreateDto user, UserCreateDto connection) {
     * user.getConnections().remove(connection);
     * }
     */

}
