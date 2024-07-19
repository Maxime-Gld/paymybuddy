package com.maxgld.paymybuddy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.maxgld.paymybuddy.model.User;
import com.maxgld.paymybuddy.repository.UsersRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = usersRepository.findByEmail(email);
        if (user != null) {
            return (UserDetails) new User(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getBalance(),
                    user.getConnections());
        }
        return null;
    }
}
