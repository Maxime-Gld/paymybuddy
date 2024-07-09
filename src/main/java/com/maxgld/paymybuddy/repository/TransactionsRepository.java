package com.maxgld.paymybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maxgld.paymybuddy.entity.TransactionsEntity;

@Repository
public interface TransactionsRepository extends JpaRepository<TransactionsEntity, Integer> {

}
