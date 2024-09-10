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

    /**
     * Sauvegarde un utilisateur
     *
     * @param user l'utilisateur a creer
     *
     * @return true si l'utilisateur a ete cree, sinon false
     */
    public Boolean saveUser(UserCreateDto user) {
        Optional<UserEntity> existing = usersRepository.findByEmail(user.getEmail());

        if (existing.isPresent()) {
            return false;
        }

        usersRepository.save(createUser(user));

        return true;
    }

    /**
     * Cree un utilisateur a partir d'un UserCreateDto
     * Le mot de passe est chiffre par bcrypt
     *
     * @param user l'utilisateur a creer
     *
     * @return l'utilisateur cree
     */
    private UserEntity createUser(UserCreateDto user) {

        UserEntity newUser = new UserEntity();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        newUser.setBalance(100.0);
        newUser.setConnections(new HashSet<>());
        return newUser;
    }

    /**
     * Recherche un utilisateur par son adresse e-mail
     *
     * @param email l'adresse e-mail a rechercher
     *
     * @return l'utilisateur trouve, ou null si l'adresse e-mail n'est pas trouvee
     */
    public UserEntity findByEmail(String email) {
        return usersRepository.findByEmail(email).orElse(null);
    }

    /**
     * Recherche un utilisateur par son id
     *
     * @param id l'id a rechercher
     *
     * @return l'utilisateur trouve, ou null si l'id n'est pas trouve
     */
    public UserEntity findById(int id) {
        return usersRepository.findById(id).orElse(null);
    }

    /**
     * Ajoute une connection a un utilisateur
     *
     * @param user       l'utilisateur qui ajoute une connection
     * @param connection l'adresse mail de l'utilisateur a ajouter en tant que
     *                   connection
     *
     * @throws IllegalArgumentException si l'adresse mail n'est pas trouvee, ou si
     *                                  l'utilisateur est deja dans la liste d'amis
     */
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

    /**
     * Retourne la liste des relations d'un utilisateur
     *
     * @param currentUser l'utilisateur dont on veut connaitre les relations
     *
     * @return la liste des relationss
     */
    public List<UserDto> getConnections(UserDetails currentUser) {
        UserEntity user = findByEmail(currentUser.getUsername());
        return user.getConnections().stream().map(
                connection -> new UserDto(connection.getId(), connection.getUsername(), connection.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Envoie de l'argent entre deux utilisateurs
     *
     * @param sender     l'utilisateur qui envoie de l'argent
     * @param receiverId l'ID de l'utilisateur qui recoit de l'argent
     * @param amount     le montant a envoyer
     *
     * @throws IllegalArgumentException si l'ID de l'utilisateur n'est pas trouve,
     *                                  si l'utilisateur envoie de l'argent a
     *                                  lui-meme, ou si le solde de l'utilisateur
     *                                  n'est pas suffisant
     */
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

    /**
     * Renvoie le solde de l'utilisateur
     *
     * @param user l'utilisateur
     *
     * @return le solde de l'utilisateur
     */
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
