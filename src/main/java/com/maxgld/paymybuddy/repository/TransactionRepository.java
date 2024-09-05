package com.maxgld.paymybuddy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maxgld.paymybuddy.entity.TransactionEntity;
import com.maxgld.paymybuddy.entity.UserEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    List<TransactionEntity> findAllBySenderOrReceiver(UserEntity userEntity, UserEntity userEntity2);

}
