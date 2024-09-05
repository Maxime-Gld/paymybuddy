package com.maxgld.paymybuddy.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.maxgld.paymybuddy.dto.TransactionDto;
import com.maxgld.paymybuddy.entity.TransactionEntity;
import com.maxgld.paymybuddy.entity.UserEntity;
import com.maxgld.paymybuddy.repository.TransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;
    private UserService userService;

    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void transfer(UserDetails user, int receiverId, double amount, String description) throws Exception {
        try {
            userService.transfer(user, receiverId, amount);
            RecordTransaction(user, receiverId, amount, description);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void RecordTransaction(UserDetails user, int receiverId, double amount, String description) {

        UserEntity sender = userService.findByEmail(user.getUsername());
        UserEntity receiver = userService.findById(receiverId);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transactionRepository.save(transaction);

    }

    public List<TransactionDto> getAllTransaction(UserDetails user) {

        UserEntity userEntity = userService.findByEmail(user.getUsername());
        List<TransactionEntity> listTransactionEntities = transactionRepository
                .findAllBySenderOrReceiver(userEntity, userEntity);
        List<TransactionDto> listTransactionDtos = listTransactionEntities.stream()
                .map(transaction -> new TransactionDto(transaction.getSender().getUsername(),
                        transaction.getReceiver().getUsername(), transaction.getAmount(),
                        transaction.getDescription()))
                .collect(Collectors.toList());

        return listTransactionDtos;
    }

}
