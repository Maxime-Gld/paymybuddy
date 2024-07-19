package com.maxgld.paymybuddy.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.maxgld.paymybuddy.model.User;
import com.maxgld.paymybuddy.repository.UsersRepository;

@Service
public class UsersService {

    private UsersRepository usersRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User saveUser(User user) {

        if (usersRepository.findByEmail(user.getEmail()) != null) {
            return null;
        }

        User newUser = createUser(user);

        return usersRepository.save(newUser);
    }

    private User createUser(User user) {

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        newUser.setBalance(100.0);

        return newUser;
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    // send and receive connections
    public void addConnection(User user, User connection) {
        if (!user.getConnections().contains(connection)) {
            user.getConnections().add(connection);
        }
    }

    public void removeConnection(User user, User connection) {
        user.getConnections().remove(connection);
    }

}
