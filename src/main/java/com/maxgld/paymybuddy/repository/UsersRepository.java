package com.maxgld.paymybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maxgld.paymybuddy.entity.UsersEntity;
import com.maxgld.paymybuddy.model.User;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Integer> {

    User save(User user);

    User findByEmail(String email);

}
