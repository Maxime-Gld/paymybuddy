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

    /**
     * Transfert une somme d'argent entre deux utilisateurs
     * 
     * @param user        l'utilisateur qui envoie l'argent
     * @param receiverId  l'id de l'utilisateur qui recoit l'argent
     * @param amount      la somme d'argent a transferer
     * @param description la description du transfert
     * @throws Exception si l'utilisateur n'a pas assez d'argent, ou si un autre
     *                   probleme survient
     */
    @Transactional
    public void transfer(UserDetails user, int receiverId, double amount, String description) throws Exception {
        try {
            userService.transfer(user, receiverId, amount);
            RecordTransaction(user, receiverId, amount, description);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Enregistre une transaction
     * 
     * @param user        l'utilisateur qui envoie l'argent
     * @param receiverId  l'id de l'utilisateur qui recoit l'argent
     * @param amount      la somme d'argent a transferer
     * @param description la description du transfert
     * @throws Exception si l'utilisateur n'a pas assez d'argent, ou si un autre
     *                   probleme survient
     */
    private void RecordTransaction(UserDetails user, int receiverId, double amount, String description)
            throws Exception {

        try {
            UserEntity sender = userService.findByEmail(user.getUsername());
            UserEntity receiver = userService.findById(receiverId);

            TransactionEntity transaction = new TransactionEntity();
            transaction.setSender(sender);
            transaction.setReceiver(receiver);
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transactionRepository.save(transaction);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * Retourne la liste de toutes les transactions de l'utilisateur
     * 
     * @param user l'utilisateur
     * @return la liste des transactions
     */
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
