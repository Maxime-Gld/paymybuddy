package com.maxgld.paymybuddy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maxgld.paymybuddy.entity.ConnectionsEntity;

@Repository
public interface ConnectionsRepository extends JpaRepository<ConnectionsEntity, Integer> {

}
