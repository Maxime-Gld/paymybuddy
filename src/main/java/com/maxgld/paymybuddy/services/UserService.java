package com.maxgld.paymybuddy.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
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

    public UserEntity findById(int id) {
        return usersRepository.findById(id).orElse(null);
    }

    public void addConnection(UserDetails user, String connection) {

        UserEntity userEntity = findByEmail(user.getUsername());
        UserEntity connectionEntity = findByEmail(connection);

        if (connectionEntity == null) {
            throw new IllegalArgumentException("Adresse mail introuvable");
        }

        if (userEntity.getConnections().contains(connectionEntity)) {
            throw new IllegalArgumentException("Cet utilisateur est déjà dans votre liste d'amis");
        }

        userEntity.getConnections().add(connectionEntity);
        usersRepository.save(userEntity);
    }

    public List<UserDto> getConnections(UserDetails currentUser) {
        UserEntity user = findByEmail(currentUser.getUsername());
        return user.getConnections().stream().map(
                connection -> new UserDto(connection.getId(), connection.getUsername(), connection.getEmail()))
                .collect(Collectors.toList());
    }

    public void transfer(UserDetails sender, int receiverId, Double amount) {

        UserEntity userEntity = findByEmail(sender.getUsername());
        UserEntity connectionEntity = findById(receiverId);

        if (connectionEntity == null) {
            throw new IllegalArgumentException("Adresse mail introuvable");
        }

        if (userEntity.getId() == connectionEntity.getId()) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous envoyer de fonds");
        }

        if (userEntity.getBalance() < amount) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        userEntity.setBalance(userEntity.getBalance() - amount);
        connectionEntity.setBalance(connectionEntity.getBalance() + amount);
        usersRepository.save(userEntity);
        usersRepository.save(connectionEntity);
    }

    public Double getBalance(UserDetails user) {

        UserEntity userEntity = findByEmail(user.getUsername());

        return userEntity.getBalance();
    }

    public String getUsername(UserDetails user) {

        UserEntity userEntity = findByEmail(user.getUsername());
        UserDto userDto = new UserDto(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail());
        return userDto.getUsername();
    }
}
