package com.maxgld.paymybuddy.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String username;
    private String password;
    private String email;
    private Double balance;
    private Set<User> connections;
}
