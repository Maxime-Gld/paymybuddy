package com.maxgld.paymybuddy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "transactions")
public class TransactionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sender_id")
    private int senderId;

    @Column(name = "receiver_id")
    private int receiverId;

    private double amount;

    private String description;
}
