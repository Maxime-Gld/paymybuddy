package com.maxgld.paymybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maxgld.paymybuddy.entity.UsersEntity;

@Repository
public interface PersonnesRepository extends JpaRepository<UsersEntity, Integer> {

}
